package fr.kilian.basiceconomy.listeners;

import fr.kilian.basiceconomy.listeners.handlers.BalanceChatHandler;
import fr.kilian.basiceconomy.listeners.handlers.PriceChatHandler;
import fr.kilian.basiceconomy.listeners.handlers.TradeChatHandler;
import fr.kilian.basiceconomy.managers.BalanceEditSession;
import fr.kilian.basiceconomy.managers.BuySession;
import fr.kilian.basiceconomy.managers.PriceEditSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (PriceEditSession.hasPending(player.getUniqueId())) {
            PriceChatHandler.handlePriceChat(event, player);
            return;
        }

        if (BalanceEditSession.hasPending(player.getUniqueId())) {
            BalanceChatHandler.handleBalanceChat(event, player);
            return;
        }

        // Session joueur (quantité)
        if (BuySession.hasPending(player.getUniqueId())) {
            TradeChatHandler.handleTradeChat(event, player);
        }
    }

}
