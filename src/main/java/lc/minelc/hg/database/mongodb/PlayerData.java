package lc.minelc.hg.database.mongodb;

import gnu.trove.set.hash.TIntHashSet;

public final class PlayerData {
    public int kills = 0,
                deaths = 0,
                wins = 0,
                kitSelected = 0,
                coins = 0,
                level = 0;

    public TIntHashSet kits; 

    public static PlayerData createEmptyData() {
        final PlayerData data = new PlayerData();
        data.kits = new TIntHashSet();

        return data;
    }
}