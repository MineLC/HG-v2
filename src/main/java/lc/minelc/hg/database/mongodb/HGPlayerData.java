package lc.minelc.hg.database.mongodb;

import gnu.trove.set.hash.TIntHashSet;

public final class HGPlayerData {
    public int kills = 0,
                deaths = 0,
                wins = 0,
                kitSelected = -537928882,
                coins = 0,
                level = 0;

    public TIntHashSet kits; 

    public static HGPlayerData createEmptyData() {
        final HGPlayerData data = new HGPlayerData();
        data.kits = new TIntHashSet();

        return data;
    }
}