package lc.minelc.hg.inventory.types;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.mapsystem.MapData;
import lc.minelc.hg.mapsystem.MapStorage;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.selectgame.MapSelectorInventoryHolder;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.minelc.hg.others.tab.TabStorage;

public final class MapSelectorInventory {

    public void handle(final MapSelectorInventoryHolder mapSelector, final InventoryClickEvent event) {
        final MapData map = mapSelector.getGame(event.getSlot());
        event.setCancelled(true);
        if (map != null && SpawnStorage.getStorage().isInSpawn(event.getWhoClicked())) {
            tryJoinToGame((Player)event.getWhoClicked(), map);
        }
    }
    
    private void tryJoinToGame(final Player player, final MapData map) {
        if (map.getGameInProgress().getState() == GameState.NONE) {
            final World world = Bukkit.getWorld(map.toString());
            if (world == null) {
                map.getGameInProgress().setState(GameState.LOADING);      
                MapStorage.getStorage().load(map.toString()).thenAccept((none) -> {
                    map.getGameInProgress().setState(GameState.NONE);
                    // Sync teleport
                    ArenaHGPlugin.getInstance().getServer().getScheduler().runTask(ArenaHGPlugin.getInstance(), () -> sendToGame(map, player));
                });
                return;
            }
        }

        if (map.getGameInProgress().getState() == GameState.LOADING) {
            Messages.send(player, "game.map-loading");
            return;
        }
        sendToGame(map, player);
    }

    private void sendToGame(final MapData map, final Player player) {
        final World world = Bukkit.getWorld(map.toString());
        if (world == null) {
            Messages.send(player, "game.world-dont-exist");
            return;
        }

        final GameInProgress game = map.getGameInProgress();
        game.setWorld(world);

        if (game.getState() == GameState.PREGAME || game.getState() == GameState.NONE) {
            setPregameItems(SpawnStorage.getStorage(), player.getInventory());

            GameStorage.getStorage().join(world, game, player);
            SidebarStorage.getStorage().getSidebar(SidebarType.PREGAME).send(player);

            TabStorage.getStorage().removeOnePlayer(player, SpawnStorage.getStorage().getPlayers());
            TabStorage.getStorage().removePlayers(player, SpawnStorage.getStorage().getPlayers());
            TabStorage.getStorage().sendPlayerInfo(player, game.getPlayers());

            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.teleport(world.getSpawnLocation());
    
            for (final Player gamePlayer : game.getPlayers()) {
                gamePlayer.showPlayer(player);
                player.showPlayer(gamePlayer);
            }
            return;
        }
        // Ingame or endgame state. You can spectate but no play
        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(game.getWorld().getSpawnLocation());  

        GameStorage.getStorage().join(world, game, player);
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(player);

        TabStorage.getStorage().removeOnePlayer(player, SpawnStorage.getStorage().getPlayers());
        TabStorage.getStorage().removePlayers(player, SpawnStorage.getStorage().getPlayers());
        TabStorage.getStorage().sendPlayerInfo(player, game.getPlayers());

        for (final Player gamePlayer : game.getPlayers()) {
            player.showPlayer(gamePlayer);
        }
    }

    private void setPregameItems(final SpawnStorage spawn, final PlayerInventory inventory) {
        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setItem(spawn.getShopItem().slot(), spawn.getShopItem().item());
        inventory.setItem(spawn.getTopItem().slot(), spawn.getTopItem().item());
    }
}