package lc.minelc.hg.mapsystem;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.plugin.SWMPlugin;

public final class MapStorage {
    private static MapStorage mapStorage;

    private final static SlimePropertyMap PROPERTIES;

    static {
        PROPERTIES = new SlimePropertyMap();
        PROPERTIES.setString(SlimeProperties.DIFFICULTY, "normal");
        PROPERTIES.setBoolean(SlimeProperties.PVP, true);
        PROPERTIES.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        PROPERTIES.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
    }

    private final Map<String, MapData> mapsPerName;
    
    private final SWMPlugin slimePlugin;
    private final SlimeLoader loader;

    MapStorage(SWMPlugin slimePlugin, SlimeLoader loader, Map<String, MapData> mapsPerName) {
        this.slimePlugin = slimePlugin;
        this.loader = loader;
        this.mapsPerName = mapsPerName;
    }

    // Execute this method async
    public CompletableFuture<Void> loadMap(final String worldName) {
        final MapData map = mapsPerName.get(worldName);
        if (map == null) {
            return null;
        }

        return CompletableFuture.runAsync(() -> {
            try {
                slimePlugin.generateWorld(slimePlugin.loadWorld(loader, worldName, false, PROPERTIES));
            } catch (UnknownWorldException | CorruptedWorldException | NewerFormatException | WorldInUseException| IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Execute this method async
    public CompletableFuture<Void> load(final String worldName) {
        return CompletableFuture.runAsync(() -> {
            try {
                SlimeWorld world = slimePlugin.loadWorld(loader, worldName, false, PROPERTIES);
                slimePlugin.generateWorld(world);
            } catch (UnknownWorldException | CorruptedWorldException | NewerFormatException | WorldInUseException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public MapData getMapData(final String worldName) {
        return mapsPerName.get(worldName);
    }

    public Map<String, MapData> getMaps() {
        return mapsPerName;
    }

    public SlimeLoader getFileLoader() {
        return loader;
    }

    public static MapStorage getStorage() {
        return mapStorage;
    }

    final static void update(MapStorage newStorage) {
        mapStorage = newStorage;
    }
}