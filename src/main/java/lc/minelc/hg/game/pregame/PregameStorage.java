package lc.minelc.hg.game.pregame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import lc.minelc.hg.inventory.internal.InventoryCreator.Item;
import lc.minelc.hg.others.spawn.SpawnStorage;

public record PregameStorage(Location mapLocation, Item selectKitItem, Material kitSelectedMaterial) {
    private static PregameStorage storage;
    public static PregameStorage getStorage() {
        return storage;
    }

    public void send(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        final Item kitItem = PregameStorage.getStorage().selectKitItem();
        inventory.setItem(kitItem.slot(), kitItem.item());
    }

    static void update(PregameStorage newStorage) {
        storage = newStorage;
    }
}
