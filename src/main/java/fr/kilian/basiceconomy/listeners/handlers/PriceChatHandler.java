package fr.kilian.basiceconomy.listeners.handlers;

import fr.kilian.basiceconomy.Main;
import fr.kilian.basiceconomy.listeners.GuiListener;
import fr.kilian.basiceconomy.managers.PriceEditSession;
import fr.kilian.basiceconomy.model.ItemMarket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PriceChatHandler {

    public static void handlePriceChat(AsyncPlayerChatEvent event, Player player) {
        event.setCancelled(true);
        String input = event.getMessage().trim();

        if (input.equalsIgnoreCase("annuler")) {
            PriceEditSession.consume(player.getUniqueId());
            player.sendMessage(Component.text("✘ Modification annulée.").color(NamedTextColor.RED));
            return;
        }

        double newPrice;
        try {
            newPrice = Double.parseDouble(input);
            if (newPrice < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("✘ Prix invalide. Saisis un nombre positif ou ")
                    .color(NamedTextColor.RED)
                    .append(Component.text("annuler").color(NamedTextColor.GOLD))
                    .append(Component.text(".").color(NamedTextColor.RED)));
            return;
        }

        PriceEditSession session    = PriceEditSession.consume(player.getUniqueId());
        ItemMarket itemMarket = session.getItemMarket();
        String           label;

        if (session.getEditType() == PriceEditSession.EditType.BUY) {
            GuiListener.getMarket().editItemPrices(itemMarket, itemMarket.getSellPrice(), newPrice);
            label = "achat";
        } else {
            GuiListener.getMarket().editItemPrices(itemMarket, newPrice, itemMarket.getBuyPrice());
            label = "vente";
        }

        player.sendMessage(Component.text("✔ Prix de " + label + " mis à jour !")
                .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
        player.sendMessage(Component.text("  Article : ").color(NamedTextColor.GRAY)
                .append(Component.text(itemMarket.getMaterial().name()).color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text("  Nouveau prix : ").color(NamedTextColor.GRAY)
                .append(Component.text(newPrice + "$").color(NamedTextColor.GREEN)));

        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () ->
                GuiListener.getGui().open(player, GuiListener.getGui().allArticles(0))
        );
    }

}
