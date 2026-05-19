package fr.kilian.basiceconomy.model;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ItemMarket {

    public static Map<Material, ItemMarket> itemsMarket = new HashMap<>();

    private final double basePrice;
    private double buyPrice;
    private double sellPrice;
    private final Material material;
    private int stock;
    private int demand;

    public ItemMarket(double basePrice, Material material){
        this.basePrice = basePrice;
        this.buyPrice = basePrice;
        this.sellPrice = basePrice/5;
        this.material = material;
        this.stock = 1000;
        this.demand = 0;

        itemsMarket.put(material, this);
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Material getMaterial() {
        return material;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }
}
