package lc.minelc.hg.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import lc.minelc.hg.others.kits.KitStorage;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.others.selectgame.MapInventoryBuilder;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.minelc.hg.others.specialitems.TrackerItem;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerInteractListener implements EventListener {

    private final MapInventoryBuilder mapInventoryBuilder;

    public PlayerInteractListener(MapInventoryBuilder mapInventoryBuilder) {
        this.mapInventoryBuilder = mapInventoryBuilder;
    }

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
        if (SpawnStorage.getStorage().isInSpawn(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getItem() != null) {
                handleWithSpawnItems(event.getPlayer(), event.getItem().getType());
            }
            return;
        }
        if (event.getItem() != null) {
            handleInteractWithItems(event);
        }
    }

    private void handleInteractWithItems(final PlayerInteractEvent event) {
        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(event.getPlayer().getUniqueId());

        if (playerInGame != null) {
            if (playerInGame.getGame().getState() == GameState.IN_GAME) {
                handleSpecialItems(event, playerInGame, event.getPlayer(), event.getItem(), event.getItem().getType());
                return;
            }
            if (playerInGame.getGame().getState() == GameState.PREGAME) {
                event.getPlayer().openInventory(KitStorage.getStorage().inventory().getInventory());
                return;
            }
            if (playerInGame.getGame().getState() == GameState.END_GAME) {
                event.setCancelled(true);
            }
        }
    }

    private void handleWithSpawnItems(final Player player, final Material type) {
        if (type == SpawnStorage.getStorage().getShopItemMaterial()) {
            player.openInventory(SpawnStorage.getStorage().getShopInventory().getInventory());
            return;
        }
        if (type == SpawnStorage.getStorage().getGameItemMaterial()) {
            player.openInventory(mapInventoryBuilder.build());
        }
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