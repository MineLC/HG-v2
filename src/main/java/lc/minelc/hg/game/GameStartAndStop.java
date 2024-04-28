package lc.minelc.hg.game;

import java.util.Set;

import lc.minelc.hg.game.countdown.invencibility.InvencibilityCountdown;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.tinylog.Logger;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.game.countdown.GameCountdown;
import lc.minelc.hg.mapsystem.MapStorage;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.abilities.GameAbility;
import lc.minelc.hg.others.events.EventStorage;
import lc.minelc.hg.others.events.GameEvent;
import lc.minelc.hg.others.kits.Kit;
import lc.minelc.hg.others.kits.KitStorage;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;
import lc.minelc.hg.utils.EntityLocation;

final class GameStartAndStop {

    private InvencibilityCountdown invencibilityCountdown;

    void start(final ArenaHGPlugin plugin, final GameInProgress game, final InvencibilityCountdown.Data invencibilityData, final String worldName) {
        MapStorage.getStorage().load(worldName).thenAccept((none) -> {
            final World world = Bukkit.getWorld(worldName);
            game.setWorld(world);

            game.setEvents(EventStorage.getStorage().createEvents(game));

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    world.getWorldBorder().setCenter(world.getSpawnLocation());
                    world.getWorldBorder().setSize(game.getMapData().getBorderSize());
                    startForPlayers(game);

                } catch (Exception e) {
                    Logger.error(e);
                }
            });

            //sendEventMessage(game);
            invencibilityCountdown = new InvencibilityCountdown(
                    invencibilityData,
                    game.getPlayers(),
                    () -> setInvencibility(game,false)
            );
            game.setInvincibility(true);

            game.startTime();
            game.setState(GameState.IN_GAME);
            game.setCountdown(null);

            final int id = plugin.getServer().getScheduler().runTaskTimer(plugin,invencibilityCountdown, 0, 20).getTaskId();
            invencibilityCountdown.setId(id);
        });
    }

    private void setInvencibility(final GameInProgress game, boolean onInvencibility){
        if (onInvencibility){
            game.setCountdown(invencibilityCountdown);
        }else{
            game.setCountdown(null);
            game.setInvincibility(false);
        }
    }
    private void startForPlayers(final GameInProgress game) {
        final Set<Player> players = game.getPlayers();
        final EntityLocation[] spawns = game.getMapData().getSpawns();

        for (final Player player : players) {
            int kitSelected = PlayerDataStorage.getStorage().get(player.getUniqueId()).kitSelected;
            final Kit kit = KitStorage.getStorage().kitsPerId().get(kitSelected);
            if (kit != null) {
                GameAbility[] gameAbilities = kit.gameAbilities();
                GameStorage.getStorage().getPlayerInGame(player.getUniqueId()).setGameAbilities(gameAbilities);
    
                KitStorage.getStorage().setKit(player, true);   
            }
            player.setGameMode(GameMode.SURVIVAL);
            final EntityLocation spawn = spawns[0];
            player.teleport(new Location(game.getWorld(), spawn.x(), spawn.y(), spawn.z(), spawn.yaw(), spawn.pitch()));
        }
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(game.getPlayers());
        sendEventMessage(game);
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
        game.getMapData().setGame(null);
        Bukkit.unloadWorld(world, false);
        System.gc();
    }
}