package fr.kilian.basiceconomy.listeners.handlers;

import fr.kilian.basiceconomy.listeners.GuiListener;
import fr.kilian.basiceconomy.managers.BuySession;
import fr.kilian.basiceconomy.managers.MarketManager;
import fr.kilian.basiceconomy.model.EcoPlayer;
import fr.kilian.basiceconomy.model.ItemMarket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TradeChatHandler {

    public static void handleTradeChat(AsyncPlayerChatEvent event, Player player) {
        event.setCancelled(true);
        String input = event.getMessage().trim();

        if (input.equalsIgnoreCase("annuler")) {
            BuySession.consume(player.getUniqueId());
            player.sendMessage(Component.text("✘ Transaction annulée.").color(NamedTextColor.RED));
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(input);
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("✘ Quantité invalide. Saisis un entier positif ou ")
                    .color(NamedTextColor.RED)
                    .append(Component.text("annuler").color(NamedTextColor.GOLD))
                    .append(Component.text(".").color(NamedTextColor.RED)));
            return;
        }

        BuySession session    = BuySession.consume(player.getUniqueId());
        ItemMarket itemMarket = session.getItemMarket();
        EcoPlayer eco        = EcoPlayer.ecoPlayers.get(player.getUniqueId());
        MarketManager market     = GuiListener.getMarket();

        if (eco == null) {
            player.sendMessage(Component.text("✘ Profil économique introuvable. Reconnecte-toi.")
                    .color(NamedTextColor.RED));
            return;
        }

        if (session.getTradeType() == BuySession.TradeType.BUY) {
            if (market.canBuy(eco, itemMarket, quantite)) {
                market.buyItems(eco, itemMarket, quantite);
                double total = itemMarket.getBuyPrice() * quantite;
                player.sendMessage(Component.text("✔ Achat effectué !")
                        .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
                player.sendMessage(Component.text("  Article : ").color(NamedTextColor.GRAY)
                        .append(Component.text(itemMarket.getMaterial().name()).color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("  Quantité : ").color(NamedTextColor.GRAY)
                        .append(Component.text(quantite + "x").color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("  Total payé : ").color(NamedTextColor.GRAY)
                        .append(Component.text(total + "$").color(NamedTextColor.RED)));
                market.updateBuyPrice(itemMarket, quantite);
            } else {
                player.sendMessage(Component.text("✘ Solde insuffisant pour " + quantite + "x " + itemMarket.getMaterial().name() + ".")
                        .color(NamedTextColor.RED));
            }
        } else {
            if (market.canSell(eco, itemMarket, quantite)) {
                market.sellItems(eco, itemMarket, quantite);
                double total = itemMarket.getSellPrice() * quantite;
                player.sendMessage(Component.text("✔ Vente effectuée !")
                        .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
                player.sendMessage(Component.text("  Article : ").color(NamedTextColor.GRAY)
                        .append(Component.text(itemMarket.getMaterial().name()).color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("  Quantité : ").color(NamedTextColor.GRAY)
                        .append(Component.text(quantite + "x").color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("  Total reçu : ").color(NamedTextColor.GRAY)
                        .append(Component.text(total + "$").color(NamedTextColor.GREEN)));
                market.updateSellPrice(itemMarket, quantite);
            } else {
                player.sendMessage(Component.text("✘ Vous n'avez pas " + quantite + "x " + itemMarket.getMaterial().name() + " en inventaire.")
                        .color(NamedTextColor.RED));
            }
        }
    }

}
