package fr.kilian.basiceconomy.managers;

import fr.kilian.basiceconomy.model.ItemMarket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuySession {

    public enum TradeType { BUY, SELL }

    private final ItemMarket itemMarket;
    private final TradeType  tradeType;

    private BuySession(ItemMarket itemMarket, TradeType tradeType) {
        this.itemMarket = itemMarket;
        this.tradeType  = tradeType;
    }

    public ItemMarket getItemMarket() { return itemMarket; }
    public TradeType  getTradeType()  { return tradeType;  }

    // -------------------------------------------------------------------------

    private static final Map<UUID, BuySession> pending = new HashMap<>();

    public static void put(UUID uuid, ItemMarket itemMarket, TradeType tradeType) {
        pending.put(uuid, new BuySession(itemMarket, tradeType));
    }

    public static boolean hasPending(UUID uuid) { return pending.containsKey(uuid); }

    public static BuySession consume(UUID uuid) { return pending.remove(uuid); }
}
