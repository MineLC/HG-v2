package lc.minelc.hg.others.spawn;

import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import lc.minelc.hg.inventory.internal.InventoryCreator.Item;
import lc.minelc.hg.inventory.types.SpawnShopInventory;

public final record SpawnStorage(Location location, Item shopItem, Map<Material, Inventory> items, SpawnShopInventory shopInventory) {
    private static SpawnStorage storage;

    public void sendToSpawn(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setLevel(0);
        player.setFoodLevel(20);
        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setItem(shopItem.slot(), shopItem.item());
        player.teleport(location);
    }

    public boolean isInSpawn(final HumanEntity player) {
        return (location == null)
            ? false
            : player.getLocation().getWorld().equals(location.getWorld());
    }

    final static void update(SpawnStorage newStorage) {
        storage = newStorage;
    }

    public static SpawnStorage getStorage() {
        return storage;
    }
}