package lc.minelc.hg.listeners;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.minelc.hg.database.mongodb.MongoDBManager;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;
import lc.minelc.hg.others.spawn.SpawnStorage;

public final class PlayerJoinListener implements EventListener {

    @ListenerData(
        event = PlayerJoinEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerJoinEvent event = (PlayerJoinEvent)defaultEvent;
        final Player player = event.getPlayer();
        event.setJoinMessage(null);
        SpawnStorage.getStorage().sendToSpawn(event.getPlayer());
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (final Player otherPlayer : players) {
            otherPlayer.hidePlayer(player);
            player.hidePlayer(otherPlayer);
        }

        CompletableFuture.runAsync(() -> {
            PlayerDataStorage.getStorage().add(player.getUniqueId(), MongoDBManager.getManager().getData(player.getUniqueId()));
            SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(player);            
        });       
    }
}