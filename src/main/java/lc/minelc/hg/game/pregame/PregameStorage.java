package lc.minelc.hg.game.pregame;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import lc.minelc.hg.others.spawn.SpawnStorage;

public record PregameStorage(Location mapLocation, boolean addShopSpawnitem) {
    private static PregameStorage storage;

    public static PregameStorage getStorage() {
        return storage;
    }

    public void send(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();

        inventory.setItem(SpawnStorage.getStorage().shopItem().slot(), SpawnStorage.getStorage().shopItem().item());
        ((CraftInventory)inventory).getInventory().update();
    }

    static void update(PregameStorage newStorage) {
        storage = newStorage;
    }
}
