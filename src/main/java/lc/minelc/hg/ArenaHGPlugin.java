package lc.minelc.hg;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tinylog.Logger;

import com.grinderwolf.swm.plugin.SWMPlugin;

import lc.lcspigot.commands.CommandStorage;
import lc.lcspigot.listeners.ListenerRegister;
import lc.minelc.hg.commands.BasicCommandsRegister;
import lc.minelc.hg.commands.game.JoinCommand;
import lc.minelc.hg.commands.game.LeaveCommand;
import lc.minelc.hg.commands.map.MapCreatorCommand;
import lc.minelc.hg.database.mongodb.MongoDBHandler;
import lc.minelc.hg.game.GameManagerThread;
import lc.minelc.hg.game.StartGameData;
import lc.minelc.hg.game.pregame.StartPreGameData;
import lc.minelc.hg.listeners.ItemPickupListener;
import lc.minelc.hg.listeners.PlayerBreakListener;
import lc.minelc.hg.listeners.PlayerChatListener;
import lc.minelc.hg.listeners.PlayerDropitemListener;
import lc.minelc.hg.listeners.PlayerInteractListener;
import lc.minelc.hg.listeners.PlayerJoinListener;
import lc.minelc.hg.listeners.PlayerQuitListener;
import lc.minelc.hg.listeners.PlayerSaturationEvent;
import lc.minelc.hg.listeners.inventory.PlayerInventoryClickListener;
import lc.minelc.hg.listeners.pvp.PlayerDeathListener;
import lc.minelc.hg.listeners.pvp.PlayerRespawnListener;
import lc.minelc.hg.listeners.pvp.damage.EntityDamageListener;
import lc.minelc.hg.listeners.pvp.damage.PlayerDamageByPlayerListener;
import lc.minelc.hg.mapsystem.MapCreatorData;
import lc.minelc.hg.mapsystem.StartMaps;
import lc.minelc.hg.messages.StartMessages;
import lc.minelc.hg.others.deaths.StartDeaths;
import lc.minelc.hg.others.events.StartEvents;
import lc.minelc.hg.others.kits.StartKits;
import lc.minelc.hg.others.levels.StartLevels;
import lc.minelc.hg.others.sidebar.StartSidebar;
import lc.minelc.hg.others.spawn.StartSpawn;

public final class ArenaHGPlugin extends JavaPlugin {

    private static final MongoDBHandler MONGODB = new MongoDBHandler();

    @Override
    public void onEnable() {

        final SWMPlugin slimePlugin = (SWMPlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        if (slimePlugin == null) {
            Logger.info("EggwarsCore need slimeworld manager to work");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {   
                MONGODB.init(this);

                new StartMaps(this, slimePlugin).load();
                new StartSpawn(this).loadSpawn();
                new StartPreGameData().loadMap(this);
                GameManagerThread.startThread();   
            } catch (Exception e) {
                getServer().getScheduler().runTask(this, () -> Logger.error(e));
                setEnabled(false);
            }
        });

        new StartMessages().load(this);
        new StartGameData().load(this);
        new StartKits(this).load();
        new StartDeaths(this).load(this);
        new StartSpawn(this).loadItems();
        new StartLevels(this).load();
        new StartPreGameData().loadItems(this);
        new StartSidebar().load(this);
        new StartEvents(this).load();

        loadCommands();
        registerBasicListeners();
    }

    private void registerBasicListeners() {
        final ListenerRegister listeners = new ListenerRegister(this);

        listeners.register(new PlayerDeathListener(this), true);
        listeners.register(new PlayerRespawnListener(this), true);
        listeners.register(new EntityDamageListener(), true);
        listeners.register(new PlayerDamageByPlayerListener(), true);
        listeners.register(new PlayerInventoryClickListener(), true);
        listeners.register(new PlayerInteractListener(), true);

        listeners.register(new PlayerBreakListener(), true);
        listeners.register(new PlayerJoinListener(), true);  
        listeners.register(new PlayerQuitListener(), true);  
        listeners.register(new PlayerDropitemListener(), true);  
        listeners.register(new PlayerChatListener(), true);
        listeners.register(new PlayerSaturationEvent(), true);
        listeners.register(new ItemPickupListener(), true);

        listeners.cancelEvent(BlockPhysicsEvent.class);
        listeners.cancelEvent(BlockGrowEvent.class);
        listeners.cancelEvent(PlayerArmorStandManipulateEvent.class);
        listeners.cancelEvent(WeatherChangeEvent.class);
    }

    @Override
    public void onDisable() {
        MONGODB.shutdown();
    }

    public FileConfiguration loadConfig(final String name) {
        final String fileFormat = name + ".yml";
        final File file = new File(getDataFolder(), fileFormat);
        if (!file.exists()) {
            saveResource(fileFormat, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void tryCreateFiles(final String... files) {
        for (final String file : files) {
            if (!new File(file).exists()) {
                saveResource(file, false);
            }
        }
    }

    private void loadCommands() {
        CommandStorage.register(new MapCreatorCommand(this, new MapCreatorData()), "map");
        CommandStorage.register(new JoinCommand(), "join");
        CommandStorage.register(new LeaveCommand(), "leave");

        new BasicCommandsRegister().registerBasicCommands();
    }
}