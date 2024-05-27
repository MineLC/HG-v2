package lc.minelc.hg.others.deaths;

import java.util.Collection;

import org.bukkit.entity.Player;

import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.messages.Messages;

public final class DeathStorage {
    private static DeathStorage storage;

    private final String[] deathMessages;
    private final String fallbackDeathMessage;
    private final String suffixIfPlayerKill;

    DeathStorage(String[] deathMessages, String fallbackDeathMessage, String suffixIfPlayerKill) {
        this.deathMessages = deathMessages;
        this.fallbackDeathMessage = fallbackDeathMessage;
        this.suffixIfPlayerKill = suffixIfPlayerKill;
    }

    public void onDeath(final PlayerInGame game, final Collection<Player> playersToSendMessage, final Player player) {
        String deathMessage = (player.getLastDamageCause() == null)
            ? fallbackDeathMessage
            : deathMessages[player.getLastDamageCause().getCause().ordinal()];
        
        if (deathMessage == null) {
            deathMessage = (fallbackDeathMessage == null) ? "%v ha muerto" : fallbackDeathMessage;
        }
        if (player.getKiller() != null) {
            deathMessage = deathMessage.replace("%v%", player.getDisplayName()) + 
                ((suffixIfPlayerKill == null)
                    ? " por " + player.getKiller().getDisplayName()
                    : suffixIfPlayerKill.replace("%d%", player.getKiller().getDisplayName()));
        } else {
            deathMessage = deathMessage.replace("%v%", player.getDisplayName());
        }
        Messages.sendNoGet(playersToSendMessage, deathMessage);
    }

    static void update(DeathStorage newStorage) {
        storage = newStorage;
    }

    public static DeathStorage getStorage() {
        return storage;
    }
}
