package fr.kilian.basiceconomy.listeners.handlers;

import fr.kilian.basiceconomy.Main;
import fr.kilian.basiceconomy.listeners.GuiListener;
import fr.kilian.basiceconomy.managers.BalanceEditSession;
import fr.kilian.basiceconomy.model.EcoPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class BalanceChatHandler {

    public static void handleBalanceChat(AsyncPlayerChatEvent event, Player admin) {
        event.setCancelled(true);
        String input = event.getMessage().trim();

        if (input.equalsIgnoreCase("annuler")) {
            BalanceEditSession.consume(admin.getUniqueId());
            admin.sendMessage(Component.text("✘ Modification annulée.").color(NamedTextColor.RED));
            return;
        }

        double montant;
        try {
            montant = Double.parseDouble(input);
            if (montant < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            admin.sendMessage(Component.text("✘ Montant invalide. Saisis un nombre positif ou ")
                    .color(NamedTextColor.RED)
                    .append(Component.text("annuler").color(NamedTextColor.GOLD))
                    .append(Component.text(".").color(NamedTextColor.RED)));
            return;
        }

        BalanceEditSession session = BalanceEditSession.consume(admin.getUniqueId());
        EcoPlayer eco     = EcoPlayer.ecoPlayers.get(session.getTargetUuid());

        if (eco == null) {
            admin.sendMessage(Component.text("✘ Joueur introuvable (déconnecté ?).")
                    .color(NamedTextColor.RED));
            return;
        }

        String avant = eco.getStringBalance();
        String label;

        switch (session.getEditType()) {
            case ADD    -> { eco.credit(montant);                    label = "ajouté";   }
            case REMOVE -> { eco.debit(montant);                     label = "retiré";   }
            case SET    -> { eco.setBalance(montant);                label = "défini à"; }
            default     -> { return; }
        }

        admin.sendMessage(Component.text("✔ Balance mise à jour !")
                .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
        admin.sendMessage(Component.text("  Joueur : ").color(NamedTextColor.GRAY)
                .append(Component.text(session.getTargetName()).color(NamedTextColor.WHITE)));
        admin.sendMessage(Component.text("  Avant : ").color(NamedTextColor.GRAY)
                .append(Component.text(avant + "$").color(NamedTextColor.YELLOW)));
        admin.sendMessage(Component.text("  " + label + " : ").color(NamedTextColor.GRAY)
                .append(Component.text(EcoPlayer.formatMoney(montant) + "$").color(NamedTextColor.AQUA)));
        admin.sendMessage(Component.text("  Après : ").color(NamedTextColor.GRAY)
                .append(Component.text(eco.getStringBalance() + "$").color(NamedTextColor.GREEN)));

        // Notifie le joueur ciblé
        org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayer(session.getTargetUuid());
        if (target != null) {
            target.sendMessage(Component.text("✦ Votre balance a été modifiée par un administrateur.")
                    .color(NamedTextColor.GOLD));
            target.sendMessage(Component.text("  Nouvelle balance : ").color(NamedTextColor.GRAY)
                    .append(Component.text(eco.getStringBalance() + "$").color(NamedTextColor.GREEN)));
        }

        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () ->
                GuiListener.getGui().open(admin, GuiListener.getGui().allPlayersBalance())
        );
    }

}
