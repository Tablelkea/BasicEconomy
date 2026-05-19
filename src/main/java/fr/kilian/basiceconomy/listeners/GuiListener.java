package fr.kilian.basiceconomy.listeners;

import fr.kilian.basiceconomy.Main;
import fr.kilian.basiceconomy.listeners.handlers.*;
import fr.kilian.basiceconomy.managers.GuiManager;
import fr.kilian.basiceconomy.managers.MarketManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;
        if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) {
            event.setCancelled(true);
            return;
        }

        String title = stripColor(event.getView().getTitle());

        if      (title.equals(GuiManager.ADMIN_PANEL_TITLE))        AdminPanelHandler.handleAdminPanel(event);
        else if (title.equals(GuiManager.SHOP_TITLE))               ShopHandler.handleShop(event);
        else if (title.startsWith(GuiManager.ALL_ARTICLES_TITLE)) ArticlesHandler.handleAllArticles(event);
        else if (title.startsWith(GuiManager.SHOP_CATEGORY_PREFIX)) ShopCategoryHandler.handleShopCategory(event);
        else if (title.equals(GuiManager.ALL_PLAYERS_TITLE)) PlayersHandler.handleAllPlayers(event);
        else if (title.equals(GuiManager.ALL_CATEGORY_TITLE)) {
            event.setCancelled(true);
        }

        PaginationHandler.handlePagination(event, player);
    }

    public String stripColor(String input) {
        return input.replaceAll("§[0-9a-fk-orA-FK-OR]", "");
    }

    public static GuiManager    getGui()    { return Main.getInstance().getGuiManager();    }
    public static MarketManager getMarket() { return Main.getInstance().getMarketManager(); }

    public static Material getClickedType(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        return (item == null || item.getType() == Material.AIR) ? null : item.getType();
    }
}