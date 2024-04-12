package lc.minelc.hg.database.mongodb;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerDataStorage {
    private static final PlayerDataStorage storage = new PlayerDataStorage();

    private final Map<UUID, HGPlayerData> playersData = new HashMap<>();

    public HGPlayerData get(final UUID uuid) {
        return playersData.get(uuid);
    }

    public void add(final UUID uuid, final HGPlayerData data) {
        playersData.put(uuid, data);
    }

    public void remove(final UUID uuid) {
        playersData.remove(uuid);
    }

    public static PlayerDataStorage getStorage() {
        return storage;
    }
}