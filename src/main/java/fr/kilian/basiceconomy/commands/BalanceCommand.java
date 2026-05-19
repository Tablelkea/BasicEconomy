package fr.kilian.basiceconomy.commands;

import fr.kilian.basiceconomy.model.EcoPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class BalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NonNull @NotNull String[] strings) {
        if(commandSender instanceof Player player){

            String playerBalance = EcoPlayer.ecoPlayers.get(player.getUniqueId()).getStringBalance();
            player.sendMessage(
                    Component.text("✦ ", NamedTextColor.GOLD)
                            .append(Component.text("Balance ", NamedTextColor.YELLOW))
                            .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(playerBalance + "$", NamedTextColor.GREEN))
                            .decoration(TextDecoration.ITALIC, false)
            );
        }
        return false;
    }
}
