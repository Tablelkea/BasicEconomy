package fr.kilian.basiceconomy.commands;

import fr.kilian.basiceconomy.Main;
import fr.kilian.basiceconomy.managers.GuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class AdminPanelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NonNull @NotNull String[] strings) {

        if(commandSender instanceof Player player){

            GuiManager guiManager = Main.getInstance().getGuiManager();
            guiManager.open(player, guiManager.createAdminPanel());

        }

        return false;
    }
}
