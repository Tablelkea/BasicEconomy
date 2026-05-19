package fr.kilian.basiceconomy.commands;

import fr.kilian.basiceconomy.Main;
import fr.kilian.basiceconomy.managers.GuiManager;
import fr.kilian.basiceconomy.model.ItemMarket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player player))
            return true;

        GuiManager gui = Main.getInstance().getGuiManager();

        // /shop
        if (args.length == 0) {
            gui.open(player, gui.createMarketGui());
            return true;
        }

        // /shop search dia
        if (args.length == 2 && args[0].equalsIgnoreCase("search")) {

            String search = args[1].toUpperCase();

            List<ItemMarket> results = ItemMarket.itemsMarket.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().name().contains(search))
                    .map(Map.Entry::getValue)
                    .toList();

            if (results.isEmpty()) {
                player.sendMessage(
                        Component.text("✘ Aucun article trouvé.")
                                .color(NamedTextColor.RED)
                );
                return true;
            }

            // un seul résultat → détails
            if (results.size() == 1) {
                sendItemInfo(player, results.get(0));
                return true;
            }

            // plusieurs résultats → liste
            player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━")
                    .color(NamedTextColor.GOLD));

            player.sendMessage(Component.text("✦ Résultats trouvés")
                    .color(NamedTextColor.GOLD));

            for (ItemMarket item : results) {

                boolean disabled = Main.getInstance()
                        .getMarketManager()
                        .isDisable(item);

                Component hover = Component.text()
                        .append(Component.text("✦ " + pretty(item.getMaterial()) + "\n")
                                .color(NamedTextColor.GOLD))

                        .append(Component.text("Catégorie : ")
                                .color(NamedTextColor.GRAY))
                        .append(Component.text(item.getCategory().name() + "\n")
                                .color(NamedTextColor.YELLOW))

                        .append(Component.text("Achat : ")
                                .color(NamedTextColor.GRAY))
                        .append(Component.text(item.getBuyPrice() + "$\n")
                                .color(NamedTextColor.GREEN))

                        .append(Component.text("Vente : ")
                                .color(NamedTextColor.GRAY))
                        .append(Component.text(item.getSellPrice() + "$\n")
                                .color(NamedTextColor.RED))

                        .append(Component.text("Etat : ")
                                .color(NamedTextColor.GRAY))
                        .append(Component.text(
                                        disabled ? "Désactivé\n" : "Actif\n")
                                .color(disabled
                                        ? NamedTextColor.RED
                                        : NamedTextColor.GREEN))

                        .append(Component.text("\n▶ Clique pour voir")
                                .color(NamedTextColor.AQUA))
                        .build();

                player.sendMessage(
                        Component.text("• ")
                                .color(NamedTextColor.DARK_GRAY)
                                .append(
                                        Component.text(pretty(item.getMaterial()))
                                                .color(NamedTextColor.YELLOW)
                                                .clickEvent(
                                                        ClickEvent.runCommand(
                                                                "/shop search " + item.getMaterial().name()
                                                        )
                                                )
                                                .hoverEvent(
                                                        HoverEvent.showText(hover)
                                                )
                                )
                );
            }

            player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━")
                    .color(NamedTextColor.GOLD));

            return true;
        }

        player.sendMessage(
                Component.text("Usage: /shop [search <item>]")
                        .color(NamedTextColor.YELLOW)
        );

        return true;
    }

    private String pretty(Material material) {
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return sb.toString().trim();
    }

    private void sendItemInfo(Player player, ItemMarket item) {
        boolean disabled = Main.getInstance()
                .getMarketManager()
                .isDisable(item);

        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━")
                .color(NamedTextColor.GOLD));

        player.sendMessage(Component.text("✦ " + pretty(item.getMaterial()))
                .color(NamedTextColor.GOLD));

        player.sendMessage(Component.text(" Catégorie : ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(item.getCategory().name())
                        .color(NamedTextColor.YELLOW)));

        player.sendMessage(Component.text(" Achat : ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(item.getBuyPrice() + "$")
                        .color(NamedTextColor.GREEN)));

        player.sendMessage(Component.text(" Vente : ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(item.getSellPrice() + "$")
                        .color(NamedTextColor.RED)));

        player.sendMessage(Component.text(" Etat : ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(
                                disabled ? "Désactivé" : "Actif")
                        .color(
                                disabled
                                        ? NamedTextColor.RED
                                        : NamedTextColor.GREEN
                        )));

        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━")
                .color(NamedTextColor.GOLD));
    }
}