package lc.minelc.hg.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.game.countdown.pregame.PreGameCountdown;
import lc.minelc.hg.game.pregame.PregameStorage;
import lc.minelc.hg.mapsystem.MapStorage;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.minelc.hg.others.specialitems.TrackerItem;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerInteractListener implements EventListener {

    @ListenerData(
        event = PlayerInteractEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerInteractEvent event = (PlayerInteractEvent)defaultEvent;

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.PHYSICAL
            || event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (event.getItem() != null && handleInteractWithItems(event)) {
            return;
        }
        event.setCancelled(false);
        if (SpawnStorage.getStorage().isInSpawn(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getItem() == null) {
                return;
            }
            final Inventory inventory = SpawnStorage.getStorage().items().get(event.getItem().getType());
            if (inventory != null) {
                event.getPlayer().openInventory(inventory);
            }
            return;
        }
    }


    private boolean handleInteractWithItems(final PlayerInteractEvent event) {
        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(event.getPlayer().getUniqueId());
        final Material type = event.getItem().getType();

        if (playerInGame != null) {
            if (playerInGame.getGame().getState() == GameState.IN_GAME) {
                return handleSpecialItems(event, playerInGame, event.getPlayer(), event.getItem(), type);
            }
            if (playerInGame.getGame().getState() == GameState.PREGAME) {
                event.setCancelled(true);
                return true;
            }
        }

        final Inventory inventory = SpawnStorage.getStorage().items().get(type);
        if (inventory != null) {
            event.setCancelled(true);
            event.getPlayer().openInventory(inventory);
        }
        return true;
    }

    private boolean handleSpecialItems(final PlayerInteractEvent event, final PlayerInGame game, final Player player, final ItemStack item, final Material material) {
        switch (material) {
            case COMPASS:
                new TrackerItem().handle(player, game.getGame());
                return true;
            default:
                event.setUseItemInHand(Result.ALLOW);
                return false;
        }
    }
}