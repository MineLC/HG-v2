package lc.minelc.hg.listeners.inventory;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.pregame.PregameStorage;
import lc.minelc.hg.mapsystem.MapData;
import lc.minelc.hg.others.kits.KitStorage;
import lc.minelc.hg.others.selectgame.MapSelectorInventoryHolder;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.minelc.hg.utils.InventoryUtils;

public class PlayerInventoryClickListener implements EventListener {


    private final int kitInventoryID = InventoryUtils.getId(KitStorage.getStorage().inventory().getInventory()),
                      spawnShopInventoryID = InventoryUtils.getId(SpawnStorage.getStorage().getShopInventory().getInventory());

    @ListenerData(
        event = InventoryClickEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(final Event defaultEvent) {
        final InventoryClickEvent event = (InventoryClickEvent)defaultEvent;

        if (event.getClickedInventory() == null || event.getInventory() == null) {
            return;
        }
        if (event.getInventory().getHolder() instanceof MapSelectorInventoryHolder mapSelector) {
            final MapData map = mapSelector.getGame(event.getSlot());
            event.setCancelled(true);
            if (map != null) {
                tryJoinToGame((Player)event.getWhoClicked(), map);
            }
            return;
        }
        final int inventory = InventoryUtils.getId(event.getInventory());
        if (inventory == -1) {
            if (SpawnStorage.getStorage().isInSpawn(event.getWhoClicked())) {
                event.setCancelled(true);
                return;
            }
            final GameInProgress gameInProgress = GameStorage.getStorage().getGame(event.getWhoClicked().getUniqueId());

            if (gameInProgress != null && gameInProgress.getState() == GameState.PREGAME) {
                event.setCancelled(true);
            }
            return;
        }

        if (!SpawnStorage.getStorage().isInSpawn(event.getWhoClicked())) {
            event.setCancelled(true);
            final GameInProgress gameInProgress = GameStorage.getStorage().getGame(event.getWhoClicked().getUniqueId());

            if (gameInProgress == null || gameInProgress.getState() != GameState.PREGAME) {
                return;
            }
        }

        if (inventory == kitInventoryID) {
            KitStorage.getStorage().inventory().handle(event);
            return; 
        }
        if (inventory == spawnShopInventoryID) {
            SpawnStorage.getStorage().getShopInventory().handle(event);
            return;
        }
    }

    private void tryJoinToGame(final Player player, final MapData map) {
        GameInProgress game = map.getGameInProgress();

        if (game == null) {
            game = new GameInProgress(map);
            map.setGame(game);
        }
        if (game.getState() == GameState.PREGAME || game.getState() == GameState.NONE) {
            PregameStorage.getStorage().send(player);
            GameStorage.getStorage().join(map.toString(), game, player);
            SidebarStorage.getStorage().getSidebar(SidebarType.PREGAME).send(player);

            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(PregameStorage.getStorage().mapLocation());
    
            showPlayers(player, game.getPlayers());
            return;
        }
        // Ingame or endgame state. You can spectate but no play
        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(game.getWorld().getSpawnLocation());  
        GameStorage.getStorage().join(map.toString(), game, player);
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(player);
        showPlayers(player, game.getPlayers());
    }

    private void showPlayers(final Player player, final Set<Player> players) {
        for (final Player otherPlayer : players) {
            otherPlayer.showPlayer(player);
            player.showPlayer(otherPlayer);
        }
    }
}