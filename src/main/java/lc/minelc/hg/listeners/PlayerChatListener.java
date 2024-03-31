package lc.minelc.hg.listeners;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.spawn.SpawnStorage;
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

import java.util.Collection;
import java.util.stream.Collectors;

public class PlayerChatListener implements EventListener {

    @ListenerData(
        priority = EventPriority.LOWEST,
        event = AsyncPlayerChatEvent.class
    )
    public void handle(Event e){
        final AsyncPlayerChatEvent event = (AsyncPlayerChatEvent)e;
        event.setCancelled(true);
        Player p = event.getPlayer();
        PlayerData pp = UserProvider.getInstance().getUserCache(p.getName());
        if(pp == null) return;

        String message = event.getMessage();

        if(!p.hasPermission("minelc.vip")) {
            message = StringUtils.remove(message , '&');
        }

        final GameInProgress game = GameStorage.getStorage().getGame(p.getUniqueId());

        if (game == null) {
            return;
        }
    
        if(p.getGameMode() == GameMode.SPECTATOR){
            final String spectatorMessage = "&8&lEspectador " + p.getName() + " &8» &f" + message;
            Messages.sendNoGet(game.getPlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                .collect(Collectors.toList()), spectatorMessage);

            return;
        }
    
        final String global_format = pp.getRankInfo().getRank().getPrefix() + " &7" + pp.getRankInfo().getUserColor() + p.getName() + " &8» &f" + message;

        final Collection<Player> players = (game.getState() == GameState.PREGAME)
            ? game.getPlayers()
            : SpawnStorage.getStorage().location().getWorld().getPlayers();

        Messages.sendNoGet(players,  global_format);
    }
}