package lc.minelc.hg.listeners;

import java.util.concurrent.CompletableFuture;

import obed.me.lccommons.api.entities.PlayerData;
import obed.me.lccommons.api.services.UserProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import lc.minelc.hg.database.mongodb.MongoDBManager;
import lc.minelc.hg.database.mongodb.HGPlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.minelc.hg.others.tab.TabStorage;
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
            TabStorage.getStorage().removeOnePlayer(player, game.getPlayers());
        } else if (SpawnStorage.getStorage().isInSpawn(player)) {
            TabStorage.getStorage().removeOnePlayer(player, SpawnStorage.getStorage().getPlayers());
        }

        CompletableFuture.runAsync(() -> {
            final HGPlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
            PlayerData playerData = UserProvider.getInstance().getUserByName(player.getName());
            playerData.setCoins(data.coins);
            UserProvider.getInstance().savePlayer(playerData);
            MongoDBManager.getManager().saveData(player.getUniqueId(), data);
        });
    }
}