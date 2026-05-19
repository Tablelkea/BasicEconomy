package fr.kilian.basiceconomy;

import fr.kilian.basiceconomy.commands.AdminPanelCommand;
import fr.kilian.basiceconomy.commands.BalanceCommand;
import fr.kilian.basiceconomy.commands.ShopCommand;
import fr.kilian.basiceconomy.listeners.GuiListener;
import fr.kilian.basiceconomy.listeners.PlayerJoinListener;
import fr.kilian.basiceconomy.managers.GuiManager;
import fr.kilian.basiceconomy.managers.MarketManager;
import fr.kilian.basiceconomy.model.ItemMarket;
import fr.kilian.basiceconomy.storage.ReadConfig;
import fr.kilian.basiceconomy.storage.SaveConfig;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private GuiManager guiManager;
    private MarketManager marketManager;

    private ReadConfig readConfig;
    private SaveConfig saveConfig;

    private static Main instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;

        guiManager = new GuiManager();
        marketManager = new MarketManager();

        readConfig = new ReadConfig();
        saveConfig = new SaveConfig();

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new GuiListener(), this);
        pm.registerEvents(new PlayerJoinListener(), this);

        getCommand("panel").setExecutor(new AdminPanelCommand());
        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("shop").setExecutor(new ShopCommand());

        ItemMarket.items = readConfig.read();
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig.saveData(ItemMarket.items);
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public MarketManager getMarketManager() {
        return marketManager;
    }

    public static Main getInstance() {
        return instance;
    }
}
