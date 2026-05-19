package fr.kilian.basiceconomy.storage;

import fr.kilian.basiceconomy.Main;
import fr.kilian.basiceconomy.model.ItemMarket;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SaveConfig {

    public void saveData(List<ItemMarket> items) {

        if (items == null || items.isEmpty()) {
            Main.getInstance().getLogger().warning("Market not saved (empty list)");
            return;
        }

        FileConfiguration config =
                Main.getInstance().getConfig();

        config.set("items", null);

        for (ItemMarket item : items) {

            String path = "items." + item.getMaterial().name();

            config.set(path + ".base-price", item.getBasePrice());
            config.set(path + ".current-price", item.getBuyPrice());
            config.set(path + ".sell-price", item.getBuyPrice());
            config.set(path + ".demand", item.getDemand());
            config.set(path + ".supply", item.getSupply());
            config.set(path + ".category", item.getCategory().name());
        }

        Main.getInstance().saveConfig();
    }

}
