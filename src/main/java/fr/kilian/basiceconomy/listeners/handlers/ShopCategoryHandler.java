package fr.kilian.basiceconomy.listeners.handlers;

import fr.kilian.basiceconomy.listeners.GuiListener;
import fr.kilian.basiceconomy.managers.BuySession;
import fr.kilian.basiceconomy.model.EcoPlayer;
import fr.kilian.basiceconomy.model.ItemMarket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopCategoryHandler {

    public static void handleShopCategory(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = GuiListener.getClickedType(event);
        if (type == null) return;

        ItemMarket itemMarket = ItemMarket.itemsMarket.get(type);
        if (itemMarket == null) return;

        Player player = (Player) event.getWhoClicked();
        EcoPlayer ecoPlayer = EcoPlayer.ecoPlayers.get(player.getUniqueId());

        boolean  isLeft     = event.getClick().isLeftClick();
        BuySession.TradeType tradeType = isLeft ? BuySession.TradeType.BUY : BuySession.TradeType.SELL;
        String    label     = isLeft ? "achat" : "vente";
        double    prix      = isLeft ? itemMarket.getBuyPrice() : itemMarket.getSellPrice();

        BuySession.put(player.getUniqueId(), itemMarket, tradeType);
        player.closeInventory();

        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                .color(NamedTextColor.GOLD));
        player.sendMessage(Component.text(" ✦ " + (isLeft ? "Achat" : "Vente"))
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true));
        player.sendMessage(Component.text(" Article : ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(type.name())
                        .color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text(" Prix unitaire (" + label + ") : ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(prix + "$")
                        .color(NamedTextColor.YELLOW)));
        if(isLeft)
            player.sendMessage(Component.text(" Quantité maximum achetable: ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text((int)ecoPlayer.getBalance() / itemMarket.getBuyPrice())
                            .color(NamedTextColor.GREEN)));
        player.sendMessage(Component.text(" ▶ Tape la quantité souhaitée dans le chat.")
                .color(NamedTextColor.AQUA));
        player.sendMessage(Component.text(" ▶ Tape ")
                .color(NamedTextColor.AQUA)
                .append(Component.text("annuler")
                        .color(NamedTextColor.RED))
                .append(Component.text(" pour abandonner.")
                        .color(NamedTextColor.AQUA)));
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                .color(NamedTextColor.GOLD));
    }

}
