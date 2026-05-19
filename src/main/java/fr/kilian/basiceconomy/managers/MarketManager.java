package fr.kilian.basiceconomy.managers;

import fr.kilian.basiceconomy.model.EcoPlayer;
import fr.kilian.basiceconomy.model.ItemMarket;
import org.bukkit.entity.Player;

public class MarketManager {

    public void buyItems(EcoPlayer player, ItemMarket item, int amount){}

    public void sellItems(EcoPlayer player, ItemMarket item, int amount){}

    public double getSellPrice(ItemMarket item, int amount){return 0;}

    public double getBuyPrice(ItemMarket item, int amount){return 0;}

    public boolean canBuy(EcoPlayer player, ItemMarket itemMarket, int amount){return false;};

    public boolean canSell(EcoPlayer player, ItemMarket itemMarket, int amount){return false;};

    public void cancelSale(EcoPlayer player){}

    public void updateBuyPrice(ItemMarket item, int amount){}

    public void updateSellPrice(ItemMarket item, int amount){}

    public void editItemPrices(ItemMarket item, double salePrice, double buyPrice){}

    public void disableItem(ItemMarket item){}

    public void enableItem(ItemMarket item){}

}
