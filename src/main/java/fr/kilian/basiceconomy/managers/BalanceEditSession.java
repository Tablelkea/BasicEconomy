package fr.kilian.basiceconomy.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BalanceEditSession {

    public enum EditType { ADD, REMOVE, SET }

    private final UUID     targetUuid;
    private final String   targetName;
    private final EditType editType;

    private BalanceEditSession(UUID targetUuid, String targetName, EditType editType) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.editType   = editType;
    }

    public UUID     getTargetUuid() { return targetUuid; }
    public String   getTargetName() { return targetName; }
    public EditType getEditType()   { return editType;   }

    private static final Map<UUID, BalanceEditSession> pending = new HashMap<>();

    public static void put(UUID adminUuid, UUID targetUuid, String targetName, EditType editType) {
        pending.put(adminUuid, new BalanceEditSession(targetUuid, targetName, editType));
    }

    public static boolean      hasPending(UUID uuid) { return pending.containsKey(uuid);  }
    public static BalanceEditSession consume(UUID uuid) { return pending.remove(uuid);     }
}
