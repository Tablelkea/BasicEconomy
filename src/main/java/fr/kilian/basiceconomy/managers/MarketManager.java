package fr.kilian.basiceconomy.managers;

import fr.kilian.basiceconomy.model.EcoPlayer;
import fr.kilian.basiceconomy.model.ItemMarket;
import fr.kilian.basiceconomy.model.enums.Category;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MarketManager {

    private static final double INCREASE_RATE = 1.0002;
    private static final double DECREASE_RATE = 0.9998;
    private static final double PRICE_FLOOR  = 0.01;
    private static final double PRICE_CAP    = 100_000.0;

    Set<ItemMarket> disableItem = new HashSet<>();
    public Map<Material, Category> materialCategory = new HashMap<>();

    public void buyItems(@NonNull EcoPlayer player, ItemMarket item, int amount){
        if(player.getBalance() >= calcBuyPrice(item, amount)){
            player.debit(calcBuyPrice(item, amount));
            player.getPlayer().getInventory().addItem(new ItemStack(item.getMaterial(), amount));
        }
    }

    public void sellItems(@NonNull EcoPlayer player, @NonNull ItemMarket item, int amount) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        if (!inventory.contains(item.getMaterial(), amount)) return;

        player.credit(calcSellPrice(item, amount));

        int remaining = amount;
        for (int i = 0; i < inventory.getSize() && remaining > 0; i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot == null || slot.getType() != item.getMaterial()) continue;

            if (slot.getAmount() <= remaining) {
                // Ce stack est entièrement consommé
                remaining -= slot.getAmount();
                inventory.setItem(i, null);
            } else {
                // Ce stack est partiellement consommé
                slot.setAmount(slot.getAmount() - remaining);
                remaining = 0;
            }
        }
    }

    public boolean canBuy(@NonNull EcoPlayer player, ItemMarket item, int amount){
        return player.getBalance() >= calcBuyPrice(item, amount);
    }

    public boolean canSell(@NonNull EcoPlayer player, @NonNull ItemMarket item, int amount){
        return player.getPlayer().getInventory().contains(item.getMaterial(), amount);
    }

    public void updateBuyPrice(@NonNull ItemMarket item, int amount) {
        item.setDemand(item.getDemand() + amount);
        double newBuyPrice = item.getBuyPrice() * Math.pow(INCREASE_RATE, amount);
        item.setBuyPrice(round2(Math.min(PRICE_CAP, newBuyPrice)));
    }

    public void updateSellPrice(@NonNull ItemMarket item, int amount) {
        double newSellPrice = item.getSellPrice() * Math.pow(DECREASE_RATE, amount);
        item.setSellPrice(round2(Math.max(PRICE_FLOOR, newSellPrice)));
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public void editItemPrices(@NonNull ItemMarket item, double sellPrice, double buyPrice){
        item.setBuyPrice(buyPrice);
        item.setSellPrice(sellPrice);
    }

    public void disableItem(ItemMarket item){
        disableItem.add(item);
    }

    public void enableItem(ItemMarket item){
        disableItem.remove(item);
    }

    public double calcBuyPrice(@NonNull ItemMarket item, int amount){
        return item.getBuyPrice()*amount;
    }

    public double calcSellPrice(@NonNull ItemMarket item, int amount){
        return item.getSellPrice()*amount;
    }

}
