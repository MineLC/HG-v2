package lc.minelc.hg.others.deaths;

import java.util.Collection;

import org.bukkit.entity.Player;

import lc.minelc.hg.game.GameInProgress;
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
        final String message = createMessage(game.getGame(), player, true);
        if (message != null) {
            Messages.sendNoGet(playersToSendMessage, message);
        }
    }

    private String createMessage(final GameInProgress game, final Player player, boolean finalKill) {
        String deathMessage = (player.getLastDamageCause() == null)
            ? fallbackDeathMessage
            : deathMessages[player.getLastDamageCause().getCause().ordinal()];
        
        if (deathMessage == null) {
            deathMessage = fallbackDeathMessage;
        }
        if (player.getKiller() != null) {
            deathMessage = deathMessage.replace("%v%", player.getName()) + suffixIfPlayerKill.replace("%d%", player.getKiller().getName());
        }
        return deathMessage;
    }

    static void update(DeathStorage newStorage) {
        storage = newStorage;
    }

    public static DeathStorage getStorage() {
        return storage;
    }
}
