package fr.kilian.basiceconomy.managers;

import fr.kilian.basiceconomy.model.ItemMarket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PriceEditSession {

    public enum EditType { BUY, SELL }

    private final ItemMarket itemMarket;
    private final EditType   editType;

    public PriceEditSession(ItemMarket itemMarket, EditType editType) {
        this.itemMarket = itemMarket;
        this.editType   = editType;
    }

    public ItemMarket getItemMarket() { return itemMarket; }
    public EditType   getEditType()   { return editType;   }

    public static final Map<UUID, PriceEditSession> pending = new HashMap<>();

    public static boolean hasPending(UUID uuid)                        { return pending.containsKey(uuid); }
    public static PriceEditSession consume(UUID uuid)                  { return pending.remove(uuid); }
    public static void put(UUID uuid, ItemMarket item, EditType type)  { pending.put(uuid, new PriceEditSession(item, type)); }
}