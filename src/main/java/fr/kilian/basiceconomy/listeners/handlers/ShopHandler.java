package fr.kilian.basiceconomy.listeners.handlers;

import fr.kilian.basiceconomy.listeners.GuiListener;
import fr.kilian.basiceconomy.model.enums.Category;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopHandler {

    public static void handleShop(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = GuiListener.getClickedType(event);
        if (type == null) return;

        Category matched = null;
        for (Category cat : Category.values()) {
            if (cat.getMaterial() == type) {
                matched = cat;
                break;
            }
        }

        if (matched == null) return;

        GuiListener.getGui().open((Player) event.getWhoClicked(), GuiListener.getGui().createCategoryGui(matched));
    }

}
