package fr.kilian.basiceconomy.storage;

import fr.kilian.basiceconomy.Main;
import fr.kilian.basiceconomy.model.ItemMarket;
import fr.kilian.basiceconomy.model.enums.Category;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ReadConfig {

    public List<ItemMarket> read(){

        FileConfiguration config = Main.getInstance().getConfig();

        List<ItemMarket> items = new ArrayList<>();

        var section = config.getConfigurationSection("items");

        if (section == null) return items;

        for (String key : section.getKeys(false)) {

            try {
                Material material = Material.valueOf(key);

                double base = config.getDouble("items." + key + ".base-price");
                double buy = config.getDouble("items." + key + ".current-price");
                int demand = config.getInt("items." + key + ".demand");
                int supply = config.getInt("items." + key + ".supply");
                int sell = config.getInt("items." + key + ".sell-price");
                String category = config.getString("items."+key+".category");

                items.add(new ItemMarket(base, buy, sell, material, Category.valueOf(category), demand, supply));

            } catch (Exception ignored) {}
        }

        return items;

    }

}
