package lc.minelc.hg.game.pregame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.tinylog.Logger;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.utils.EntityLocation;

public final class StartPreGameData {

    public void loadItems(ArenaHGPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("items/pregame");
        PregameStorage.update(new PregameStorage(null, config.getBoolean("add-shop-spawn-item")));
    }

    public void loadMap(final ArenaHGPlugin plugin) {
        final FileConfiguration config = plugin.getConfig();
        final String world = config.getString("pregame.world");
        if (world == null) {
            return;
        }
        final String spawn = config.getString("pregame.cords");
        if (spawn == null) {
            return;
        }

        final World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            Logger.warn("can't found the pregame world: " + world);
            return;
        }
        final EntityLocation entityLocation = EntityLocation.create(spawn);
        final Location location = new Location(bukkitWorld, entityLocation.x(), entityLocation.y(), entityLocation.z(), entityLocation.yaw(), entityLocation.pitch());
        bukkitWorld.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        bukkitWorld.getWorldBorder().setSize(config.getInt("pregame.border"));
        final PregameStorage oldStorage = PregameStorage.getStorage();
        PregameStorage.update(new PregameStorage(location, oldStorage.addShopitem()));
    }
}