package lc.minelc.hg.game.pregame;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import lc.minelc.hg.others.spawn.SpawnStorage;

public record PregameStorage(Location mapLocation, boolean addShopitem) {
    private static PregameStorage storage;
    public static PregameStorage getStorage() {
        return storage;
    }

    public void send(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        if (addShopitem) {
            inventory.setItem(SpawnStorage.getStorage().getShopItem().slot(), SpawnStorage.getStorage().getShopItem().item());
        }
        inventory.setItem(SpawnStorage.getStorage().getTopItem().slot(), SpawnStorage.getStorage().getTopItem().item());
    }

    static void update(PregameStorage newStorage) {
        storage = newStorage;
    }
}
