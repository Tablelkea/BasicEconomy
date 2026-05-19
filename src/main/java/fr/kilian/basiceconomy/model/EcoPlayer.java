package fr.kilian.basiceconomy.model;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EcoPlayer {

    public static Map<UUID, EcoPlayer> ecoPlayers = new HashMap<>();

    private final Player player;
    private double balance;

    public EcoPlayer(Player player, double balance){
        this.player = player;
        this.balance = balance;

        ecoPlayers.put(player.getUniqueId(), this);
    }

    public Player getPlayer() {return player;}

    public double getBalance() {return balance;}

    public void credit(double amount){this.balance += amount;}

    public void debit(double amount){this.balance -= amount;}
}
