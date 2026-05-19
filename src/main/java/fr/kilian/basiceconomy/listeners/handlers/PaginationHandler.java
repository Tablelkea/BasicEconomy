package fr.kilian.basiceconomy.listeners.handlers;

import fr.kilian.basiceconomy.listeners.GuiListener;
import fr.kilian.basiceconomy.managers.GuiManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PaginationHandler {

    public static void handlePagination(InventoryClickEvent event, Player player){
        String title = event.getView().getTitle();
        ItemStack current = event.getCurrentItem();
        if (title.startsWith(GuiManager.ALL_ARTICLES_TITLE)) {

            int currentPage = GuiListener.getGui().extractPage(title);

            if (current.getType() == Material.ARROW) {

                if(event.getSlot() == 53){
                    GuiListener.getGui().open(player, GuiListener.getGui().allArticles(currentPage + 1));

                }else if(event.getSlot() == 45){
                    GuiListener.getGui().open(player, GuiListener.getGui().allArticles(currentPage - 1));
                }
            }
        }
    }

}
