package fr.kilian.basiceconomy.listeners.handlers;

import fr.kilian.basiceconomy.listeners.GuiListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AdminPanelHandler {

    public static void handleAdminPanel(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = GuiListener.getClickedType(event);
        if (type == null) return;

        Player player = (Player) event.getWhoClicked();

        switch (type) {
            case PLAYER_HEAD -> GuiListener.getGui().open(player, GuiListener.getGui().allPlayersBalance());
            case CHEST       -> GuiListener.getGui().open(player, GuiListener.getGui().allCategory());
            case GOLD_INGOT  -> GuiListener.getGui().open(player, GuiListener.getGui().allArticles(0));
            default          -> {}
        }
    }


}
