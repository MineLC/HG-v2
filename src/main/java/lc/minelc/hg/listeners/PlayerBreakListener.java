package lc.minelc.hg.listeners;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.others.abilities.GameAbility;
import lc.minelc.hg.others.events.GameEventType;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;

import java.util.UUID;

import static lc.minelc.hg.others.abilities.AbilitiesFunctions.fastBreakTree;
import static lc.minelc.hg.others.abilities.AbilitiesFunctions.heartRecovery;

public class PlayerBreakListener implements EventListener {

    @ListenerData(
        priority = EventPriority.LOWEST,
        event = BlockBreakEvent.class
    )
    public void handle(Event defaultEvent) {
        final BlockBreakEvent event = (BlockBreakEvent)defaultEvent;
        final UUID playerUUID = ((BlockBreakEvent) defaultEvent).getPlayer().getUniqueId();
        final GameInProgress game = GameStorage.getStorage().getGame(playerUUID);
        if (SpawnStorage.getStorage().isInSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
        if (event.getBlock() != null){
            handleAbilitiesInteract(event, game);
        }
    }
    private void handleAbilitiesInteract(final BlockBreakEvent event, GameInProgress game) {
        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(event.getPlayer().getUniqueId());
        GameAbility[] abilities = playerInGame.getGameAbilities();

        for (GameAbility ability : abilities) {
            switch (ability) {
                case FAST_TREE_BREAK:
                    fastBreakTree(event, game.getWorld());
                    break;
                default:
                    break;
            }
        }
    }
}