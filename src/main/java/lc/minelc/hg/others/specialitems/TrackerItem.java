package lc.minelc.hg.others.specialitems;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.messages.Messages;

public final class TrackerItem {

    public void handle(final Player player, final GameInProgress game) {
        final Set<Player> players = game.getPlayers();

        for (final Player otherPlayer : players) {
            if (otherPlayer.equals(player)) {
                continue;
            }
            if (otherPlayer.getGameMode() == GameMode.SPECTATOR) {
                continue;
            }
            final float distance = (float)otherPlayer.getLocation().distance(player.getLocation());
            player.sendMessage(Messages.get("special-items.tracker")
                .replace("%distance%", String.valueOf(distance))
                .replace("%player%", otherPlayer.getName()));

            player.setCompassTarget(otherPlayer.getLocation());
            break;
        }
    }
}