package lc.minelc.hg.listeners;

import lc.minelc.hg.game.pregame.PregameStorage;
import lc.minelc.hg.others.abilities.GameAbility;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

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
                handleAbilitiesInteract(event, playerInGame);
                return;
            }
            if (playerInGame.getGame().getState() == GameState.PREGAME) {
                handleWithPregameItems(event.getPlayer(), event.getMaterial());
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
    private void handleWithPregameItems(final Player player, final Material type) {
        if (type == PregameStorage.getStorage().kitSelectedMaterial()) {
            player.openInventory(KitStorage.getStorage().inventory().getInventory());;
        }
    }

    private void handleAbilitiesInteract(final PlayerInteractEvent event, final PlayerInGame playerInGame){
        if (event.getPlayer().getItemInHand().getType() == Material.MUSHROOM_SOUP && Arrays.asList(playerInGame.getGameAbilities()).contains(GameAbility.HEARTS_RECOVERY_WITH_SOUPS_2)) {
            ItemStack bowl = new ItemStack(Material.BOWL, 1);
            ItemMeta meta = bowl.getItemMeta();
            int heal = 4;
            int feed = 4;
            if (event.getPlayer().getHealth() < event.getPlayer().getMaxHealth() - 1.0D) {
                if (event.getPlayer().getHealth() < event.getPlayer().getMaxHealth() - heal + 1.0D) {
                    event.getPlayer().getItemInHand().setType(Material.BOWL);
                    event.getPlayer().getItemInHand().setItemMeta(meta);
                    event.getPlayer().setItemInHand(bowl);
                    event.getPlayer().setHealth(event.getPlayer().getHealth() + heal);
                } else if (event.getPlayer().getHealth() < event.getPlayer().getMaxHealth() && event.getPlayer().getHealth() > event.getPlayer().getMaxHealth() - heal) {
                    event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
                    event.getPlayer().getItemInHand().setType(Material.BOWL);
                    event.getPlayer().getItemInHand().setItemMeta(meta);
                    event.getPlayer().setItemInHand(bowl);
                }
            } else if (event.getPlayer().getHealth() == event.getPlayer().getMaxHealth() && event.getPlayer().getFoodLevel() < 20) {
                if (event.getPlayer().getFoodLevel() < 20 - feed + 1) {
                    event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + feed);
                    event.getPlayer().getItemInHand().setType(Material.BOWL);
                    event.getPlayer().getItemInHand().setItemMeta(meta);
                    event.getPlayer().setItemInHand(bowl);
                } else if (event.getPlayer().getFoodLevel() < 20 && event.getPlayer().getFoodLevel() > 20 - feed) {
                    event.getPlayer().setFoodLevel(20);
                    event.getPlayer().getItemInHand().setType(Material.BOWL);
                    event.getPlayer().getItemInHand().setItemMeta(meta);
                    event.getPlayer().setItemInHand(bowl);
                }
            }
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