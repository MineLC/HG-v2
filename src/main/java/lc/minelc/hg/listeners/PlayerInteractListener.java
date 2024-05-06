package lc.minelc.hg.listeners;

import lc.minelc.hg.others.abilities.GameAbility;
import lc.minelc.hg.others.abilities.InteractAbilities;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.others.selectgame.MapInventoryBuilder;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.minelc.hg.others.specialitems.TrackerItem;
import lc.minelc.hg.others.top.TopStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerInteractListener implements EventListener {

    private final InteractAbilities interactAbilities = new InteractAbilities();
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

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.PHYSICAL || event.getAction() == Action.LEFT_CLICK_BLOCK
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
                final Material material = event.getItem().getType();
                if (handleSpecialItems(event, playerInGame, event.getPlayer(), event.getItem(), event.getItem().getType())) {
                    return;
                }
                handleAbilitiesInteract(event, playerInGame, material);
                return;
            }
            if (playerInGame.getGame().getState() == GameState.PREGAME) {
                handleWithSpawnItems(event.getPlayer(), event.getMaterial());
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
            return;
        }
        if (type == SpawnStorage.getStorage().getTopMaterial()) {
            player.openInventory(TopStorage.getStorage().getInventory());
            return;
        }
    }

    private void handleAbilitiesInteract(final PlayerInteractEvent event, final PlayerInGame playerInGame, final Material material) {
        final GameAbility[] abilities = playerInGame.getGameAbilities();
        if (abilities == null) {
            return;
        }
        for (GameAbility ability : abilities) {
            switch (ability) {
                case HEARTS_SOUPS_1_5:
                    interactAbilities.soup(event, material, 1.5F);
                    return;
                case HEARTS_SOUPS_2_5:
                    interactAbilities.soup(event, material, 2.5F);
                    return;
                case COOKIE_STRENGTH:
                    interactAbilities.cookie(event, material);
                    break;
                case FAST_GOLDEN_APPLE:
                    interactAbilities.fastGoldenApple(event, material);
                    return;
                default:
                    break;
            }
        }
    }
    private boolean handleSpecialItems(final PlayerInteractEvent event, final PlayerInGame game, final Player player, final ItemStack item, final Material material) {
        switch (material) {
            case COMPASS:
                new TrackerItem().handle(player, game.getGame());
                return true;
            default:
                return false;
        }
    }
}