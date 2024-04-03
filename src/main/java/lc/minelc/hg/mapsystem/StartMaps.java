package lc.minelc.hg.mapsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.plugin.SWMPlugin;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.GameManagerThread;

import lc.minelc.hg.utils.EntityLocation;

public final class StartMaps {

    private final ArenaHGPlugin plugin;
    private final SlimeLoader loader;
    private final SWMPlugin slimePlugin;

    public StartMaps(ArenaHGPlugin plugin, SWMPlugin slimePlugin) {
        this.plugin = plugin;
        this.slimePlugin = slimePlugin;
        this.loader = slimePlugin.getLoader(plugin.getConfig().getString("slime-loader"));
    }

    public void load() {
        final File mapFolder = new File(plugin.getDataFolder(), "maps");

        if (!mapFolder.exists()) {
            mapFolder.mkdir();
            MapStorage.update(new MapStorage(slimePlugin, loader, new HashMap<>(), null));
            return;
        }

        final File[] mapFiles = mapFolder.listFiles();
        if (mapFiles == null) {
            MapStorage.update(new MapStorage(slimePlugin, loader, new HashMap<>(), null));
            return;
        }
        final Map<String, MapData> mapsPerName = new HashMap<>();
        final MapData[] maps = new MapData[mapFiles.length];
        if (mapFiles.length > 0) {
            loadMapData(maps, mapFiles, mapsPerName);
        }
        MapStorage.update(new MapStorage(slimePlugin, loader, mapsPerName, maps));
        GameManagerThread.setMaps(maps);
    }

    private void loadMapData(final MapData[] maps, final File[] mapFiles, final Map<String, MapData> mapsPerName) {
        final Gson gson = new Gson();
        int index = 0;

        for (final File mapFile : mapFiles) {
            if (!mapFile.getName().endsWith(".json")) {
                continue;
            }
            try {
                final JsonMapData data = gson.fromJson(new JsonReader(new BufferedReader(new FileReader(mapFile))), JsonMapData.class);
                final MapData map = new MapData(
                    getSpawns(data),
                    data.borderSize(),
                    index,
                    data.world()
                );

                maps[index] = map;
                mapsPerName.put(data.world(), map);
                index++;
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                Logger.error("Error on load the map: " + mapFile.getName() + ". Check the json in: " + mapFile.getAbsolutePath());
                Logger.error(e);
            }
        }
    }


    private EntityLocation[] getSpawns(final JsonMapData data) {
        final String[] locations = data.spawns();
        final EntityLocation[] parsedLocations = new EntityLocation[locations.length];
        int index = 0;
        for (final String location : locations) {
            final EntityLocation entityLocation = EntityLocation.create(location);
            parsedLocations[index++] = entityLocation;
        }
        return parsedLocations;
    }
}