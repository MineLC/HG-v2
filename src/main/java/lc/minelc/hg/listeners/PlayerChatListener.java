package lc.minelc.hg.listeners;

import lc.minelc.hg.database.mongodb.HGPlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.kits.Kit;
import lc.minelc.hg.others.kits.KitStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import obed.me.lccommons.api.entities.PlayerData;
import obed.me.lccommons.api.services.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.stream.Collectors;

public class PlayerChatListener implements EventListener {

    @ListenerData(
        priority = EventPriority.LOWEST,
        event = AsyncPlayerChatEvent.class
    )
    public void handle(Event e){
        final AsyncPlayerChatEvent event = (AsyncPlayerChatEvent)e;
        event.setCancelled(true);
        final Player p = event.getPlayer();
        final PlayerData pp = UserProvider.getInstance().getUserCache(p.getName());
        if(pp == null) return;

        final HGPlayerData hgPlayerData = PlayerDataStorage.getStorage().get(p.getUniqueId());

        String message = event.getMessage();

        if(!p.hasPermission("minelc.vip")) {
            message = StringUtils.remove(message , '&');
        }
        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(p.getUniqueId());
        final Kit kit = KitStorage.getStorage().kitsPerId().get(hgPlayerData.kitSelected);
        final String kitName = (kit == null) ? "Sin kit" : kit.name();

        final String format = "&aNv " + hgPlayerData.level + " &8[" + kitName + "] " + pp.getRankInfo().getRank().getPrefix() + " " + pp.getRankInfo().getUserColor() + p.getName() + " &8» " + pp.getRankInfo().getUserColor() + message;

        if (playerInGame == null) {
            Messages.sendNoGet(p.getWorld().getPlayers(), format);
            return;
        }
        if(p.getGameMode() == GameMode.SPECTATOR){
            final String spectatorMessage = "&8&lEspectador " + format;
            Messages.sendNoGet(playerInGame.getGame().getPlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                .collect(Collectors.toList()), spectatorMessage);   
            return;
        }
        Messages.sendNoGet(playerInGame.getGame().getPlayers(), format);
    }
}