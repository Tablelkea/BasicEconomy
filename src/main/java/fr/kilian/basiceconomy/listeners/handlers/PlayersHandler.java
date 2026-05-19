package fr.kilian.basiceconomy.listeners.handlers;

import fr.kilian.basiceconomy.listeners.GuiListener;
import fr.kilian.basiceconomy.managers.BalanceEditSession;
import fr.kilian.basiceconomy.model.EcoPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PlayersHandler {

    public static void handleAllPlayers(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = GuiListener.getClickedType(event);
        if (type != Material.PLAYER_HEAD) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        String targetName = PlainTextComponentSerializer
                .plainText()
                .serialize(item.getItemMeta().displayName());

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) return;

        Player  admin    = (Player) event.getWhoClicked();
        boolean isLeft   = event.getClick().isLeftClick();
        boolean isMiddle = event.getClick() == ClickType.MIDDLE;

        BalanceEditSession.EditType editType;
        String label;

        if (isMiddle) {
            editType = BalanceEditSession.EditType.SET;
            label    = "Définir";
        } else if (isLeft) {
            editType = BalanceEditSession.EditType.ADD;
            label    = "Ajouter";
        } else {
            editType = BalanceEditSession.EditType.REMOVE;
            label    = "Retirer";
        }

        EcoPlayer eco = EcoPlayer.ecoPlayers.get(target.getUniqueId());
        if (eco == null) return;

        BalanceEditSession.put(admin.getUniqueId(), target.getUniqueId(), targetName, editType);
        admin.closeInventory();

        admin.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
        admin.sendMessage(Component.text(" ✦ Modification de balance")
                .color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true));
        admin.sendMessage(Component.text(" Joueur : ").color(NamedTextColor.GRAY)
                .append(Component.text(targetName).color(NamedTextColor.WHITE)));
        admin.sendMessage(Component.text(" Balance actuelle : ").color(NamedTextColor.GRAY)
                .append(Component.text(eco.getStringBalance() + "$").color(NamedTextColor.YELLOW)));
        admin.sendMessage(Component.text(" Action : ").color(NamedTextColor.GRAY)
                .append(Component.text(label).color(NamedTextColor.AQUA)));
        admin.sendMessage(Component.text(" ▶ Tape le montant dans le chat.").color(NamedTextColor.AQUA));
        admin.sendMessage(Component.text(" ▶ Tape ").color(NamedTextColor.AQUA)
                .append(Component.text("annuler").color(NamedTextColor.RED))
                .append(Component.text(" pour abandonner.").color(NamedTextColor.AQUA)));
        admin.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
    }

}
