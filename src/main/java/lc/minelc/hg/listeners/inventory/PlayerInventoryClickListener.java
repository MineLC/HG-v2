package lc.minelc.hg.listeners.inventory;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.inventory.types.MapSelectorInventory;
import lc.minelc.hg.others.kits.KitStorage;
import lc.minelc.hg.others.selectgame.MapSelectorInventoryHolder;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.minelc.hg.others.top.TopData;
import lc.minelc.hg.others.top.TopInventoryBuilder;
import lc.minelc.hg.others.top.TopStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.minelc.hg.utils.InventoryUtils;

public class PlayerInventoryClickListener implements EventListener {


    private final int kitInventoryID = InventoryUtils.getId(KitStorage.getStorage().inventory().getInventory()),
                      spawnShopInventoryID = InventoryUtils.getId(SpawnStorage.getStorage().getShopInventory().getInventory()),
                      topSelect = "topSelect".hashCode(),
                      top = "top".hashCode();
                    
    private final MapSelectorInventory mapSelectorInventory = new MapSelectorInventory();

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
            mapSelectorInventory.handle(mapSelector, event);
            return;
        }
        final int inventory = InventoryUtils.getId(event.getInventory());

        if (inventory == top) {
            event.setCancelled(true);
            return;
        }
        if (inventory == topSelect) {
            event.setCancelled(true);
            final TopData data = TopStorage.getStorage().getTopData(event.getSlot());
            if (data != null) {
                event.getWhoClicked().openInventory(new TopInventoryBuilder().build(data.getTops(), data.getInventoryName()));
            }
            return;
        }

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
}