package lc.minelc.hg.listeners;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import lc.minelc.hg.database.mongodb.MongoDBManager;
import lc.minelc.hg.database.mongodb.PlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerQuitListener implements EventListener {

    @ListenerData(
        event = PlayerQuitEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PlayerQuitEvent event = (PlayerQuitEvent)defaultEvent;
        final Player player = event.getPlayer();
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        event.setQuitMessage(null);
        
        if (game != null) {
            GameStorage.getStorage().leave(game, player);
        }
        CompletableFuture.runAsync(() -> {
            final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
            MongoDBManager.getManager().saveData(player.getUniqueId(), data);
        });
    }
}