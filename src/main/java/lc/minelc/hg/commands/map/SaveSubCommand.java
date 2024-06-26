package lc.minelc.hg.commands.map;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.io.Files;
import com.google.gson.Gson;

import lc.minelc.hg.ArenaHGPlugin;
import lc.lcspigot.commands.Command;
import lc.minelc.hg.mapsystem.CreatorData;
import lc.minelc.hg.mapsystem.JsonMapData;
import lc.minelc.hg.mapsystem.MapCreatorData;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.minelc.hg.utils.EntityLocation;

final class SaveSubCommand implements Command {

    private final ArenaHGPlugin plugin;
    private final MapCreatorData data;

    SaveSubCommand(ArenaHGPlugin plugin, MapCreatorData data) {
        this.plugin = plugin;
        this.data = data;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            sendWithColor(player, "&cTo use this command enable the editor mode");
            return;
        }
        if (args.length != 2) {
            sendWithColor(player, "&cFormat: /map save &7(mapname)");
            return;
        }
        final File mapFolder = new File(plugin.getDataFolder(), "maps");
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
        }
        final File mapFile = new File(mapFolder, args[1] + ".json");
        if (mapFile.exists()) {
            sendWithColor(player, "&cAlready exist a map with this name. Try other");
            return;
        }

        try {
            mapFile.createNewFile();

            data.remove(player.getUniqueId());
            sendWithColor(player, "&aMap saved in: " + mapFile.getPath());

            final JsonMapData object = saveMapInfo(creatorData, player.getWorld());
            Files.write(new Gson().toJson(object), mapFile, Charset.forName("UTF-8"));

            SpawnStorage.getStorage().sendToSpawn(player);
            Bukkit.unloadWorld(player.getWorld(), true);
        } catch (IOException e) {
            sendWithColor(player, "&cError on create the map");
            e.printStackTrace();
            return;
        }
    }

    private JsonMapData saveMapInfo(final CreatorData data, final World world) {
        return new JsonMapData(
            world.getName(),
            world.getName(),
            (int)world.getWorldBorder().getSize(),
            saveSpawns(data)
        );
    }

    private String[] saveSpawns(final CreatorData data) {
        final Set<EntityLocation> spawns = data.getSpawns();
        final String[] parsedSpawns = new String[spawns.size()];
        int index = 0;

        for (final EntityLocation location : spawns) {
            parsedSpawns[index++] = location.toString();
        }
        return parsedSpawns;
    }
}