package lc.minelc.hg.game.pregame;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import lc.minelc.hg.inventory.internal.InventoryCreator.Item;
import lc.minelc.hg.others.spawn.SpawnStorage;

public record PregameStorage(Location mapLocation, boolean addShopSpawnitem) {
    private static PregameStorage storage;

    public static PregameStorage getStorage() {
        return storage;
    }

    public void send(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        final Item shop = SpawnStorage.getStorage().getShopItem();
        inventory.setItem(shop.slot(), shop.item());
    }

    static void update(PregameStorage newStorage) {
        storage = newStorage;
    }
}
