package fr.kilian.basiceconomy.model;

import fr.kilian.basiceconomy.model.enums.Category;
import org.bukkit.Material;

import java.util.*;

public class ItemMarket {

    public static Map<Material, ItemMarket> itemsMarket = new HashMap<>();
    public static List<ItemMarket> items = new ArrayList<>();

    private final double basePrice;
    private double buyPrice;
    private double sellPrice;
    private final Material material;
    private int demand;
    private final int supply;
    private Category category;

    public ItemMarket(double basePrice, double buyPrice, double sellPrice, Material material, Category category, int demand, int supply){
        this.basePrice = basePrice;
        this.material = material;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.demand = demand;
        this.supply = supply;
        this.category = category;

        itemsMarket.put(material, this);
        items.add(this);
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

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public Category getCategory() {return category;}

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getSupply() {return supply;}
}
