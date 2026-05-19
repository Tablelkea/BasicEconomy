package fr.kilian.basiceconomy.commands.tabcompleter;

import fr.kilian.basiceconomy.model.ItemMarket;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShopTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {

        List<String> completions = new ArrayList<>();

        // /shop ...
        if (args.length == 1) {
            if ("search".startsWith(args[0].toLowerCase())) {
                completions.add("search");
            }
        }

        // /shop search ...
        if (args.length == 2 && args[0].equalsIgnoreCase("search")) {
            String input = args[1].toUpperCase();

            for (Material material : ItemMarket.itemsMarket.keySet()) {
                if (material.name().startsWith(input)) {
                    completions.add(material.name());
                }
            }
        }

        return completions;
    }
}
