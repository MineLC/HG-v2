package lc.minelc.hg.others.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.tinylog.Logger;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.inventory.internal.InventoryCreator;
import lc.minelc.hg.inventory.internal.InventoryCreator.Item;
import lc.minelc.hg.inventory.types.SpawnShopInventory;
import lc.minelc.hg.utils.EntityLocation;

public class StartSpawn {
    private final ArenaHGPlugin plugin;

    public StartSpawn(ArenaHGPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadItems() {
        final FileConfiguration config = plugin.loadConfig("items/spawn");
        final InventoryCreator creator = new InventoryCreator(config);

        final Item shopItem = creator.create("shop-item");
        final Item gameItem = creator.create("game-join");
        final SpawnShopInventory spawnShopInventory = getSpawnShopInventory();

        SpawnStorage.update(new SpawnStorage(null, shopItem, gameItem, spawnShopInventory));
    }

    public void loadSpawn() {
        final FileConfiguration config = plugin.getConfig();
        final String world = config.getString("spawn.world");
        if (world == null) {
            return;
        }
        final String spawn = config.getString("spawn.cords");
        if (spawn == null) {
            return;
        }
        final World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            Logger.warn("can't found the spawn world: " + world);
            return;
        }
        final EntityLocation entityLocation = EntityLocation.create(spawn);
        final Location location = new Location(bukkitWorld, entityLocation.x(), entityLocation.y(), entityLocation.z(), entityLocation.yaw(), entityLocation.pitch());
        bukkitWorld.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        bukkitWorld.getWorldBorder().setSize(config.getInt("spawn.border"));
        SpawnStorage.update(location);
    }

    private SpawnShopInventory getSpawnShopInventory() {
        final FileConfiguration config = plugin.loadConfig("inventories/spawnshop");
        final InventoryCreator creator = new InventoryCreator(config);
        final Inventory inventory = creator.create("spawnshop", "inventory");
        
        final Item kitSelector = creator.create("kit-selector");
        inventory.setItem(kitSelector.slot(), kitSelector.item());

        return new SpawnShopInventory(kitSelector, inventory);
    }
}