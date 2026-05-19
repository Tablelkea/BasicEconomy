package fr.kilian.basiceconomy.listeners;

import fr.kilian.basiceconomy.model.EcoPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();

        EcoPlayer.ecoPlayers.putIfAbsent(player.getUniqueId(), new EcoPlayer(player, 1500));

    }

}
