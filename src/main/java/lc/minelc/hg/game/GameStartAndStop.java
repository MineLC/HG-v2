package lc.minelc.hg.game;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.countdown.GameCountdown;
import lc.minelc.hg.mapsystem.MapStorage;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.events.EventStorage;
import lc.minelc.hg.others.events.GameEvent;
import lc.minelc.hg.others.kits.KitStorage;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;

final class GameStartAndStop {

    void start(final ArenaHGPlugin plugin, final GameInProgress game, final String worldName) {
        MapStorage.getStorage().load(worldName).thenAccept((none) -> {
            final World world = Bukkit.getWorld(worldName);
            game.setWorld(world);

            game.setEvents(EventStorage.getStorage().createEvents(game));

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                world.getWorldBorder().setCenter(world.getSpawnLocation());
                world.getWorldBorder().setSize(game.getMapData().getBorderSize());
                startForPlayers(game);
            });

            sendEventMessage(game);

            game.startTime();
            game.setState(GameState.IN_GAME);
            game.setCountdown(null);
        });
    }

    private void startForPlayers(final GameInProgress game) {
        final Set<Player> players = game.getPlayers();
        for (final Player player : players) {
            KitStorage.getStorage().setKit(player, true);
        }
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(game.getPlayers());
    }
    
    private void sendEventMessage(final GameInProgress game) {
        final StringBuilder builder = new StringBuilder(Messages.get("events.format"));
        final GameEvent[] events = game.getEvents();
        for (final GameEvent event : events) {
            builder.append(event.information().replace("%time%", GameCountdown.parseTime(event.secondToStart())));
            builder.append('\n');
        }
        Messages.sendNoGet(game.getPlayers(), builder.toString());
    }

    void stop(final GameInProgress game) {       
        if (game.getCountdown() != null) {
            Bukkit.getScheduler().cancelTask(game.getCountdown().getId());
        }
        final World world = game.getWorld();
        Bukkit.unloadWorld(world, false);
        System.gc();
    }
}