package lc.minelc.hg.database.mongodb;

import org.bukkit.entity.Player;

import lc.minelc.hg.others.kits.KitStorage;

public final class HGPlayerData {
    public Player player;
    public int kills = 0,
                deaths = 0,
                wins = 0,
                kitSelected = (KitStorage.getStorage().defaultKit() != null) ? KitStorage.getStorage().defaultKit().id() : 0,
                coins = 0,
                deathMessage = 0,
                level = 0;
}