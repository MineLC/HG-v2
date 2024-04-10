package lc.minelc.hg.game.countdown.invencibility;

import lc.minelc.hg.game.countdown.CountdownCallback;
import lc.minelc.hg.game.countdown.GameCountdown;
import lc.minelc.hg.game.countdown.pregame.PreGameCountdown;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.sidebar.HgSidebar;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Set;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.YELLOW;

public class InvencibilityCountdown extends GameCountdown {

    private int countdown = 0;
    private int waitingCountdown = 0;

    private final Set<Player> players;
    private final InvencibilityCountdown.Data data;

    private final CountdownCallback start;
    private final CountdownCallback complete;

    public InvencibilityCountdown(InvencibilityCountdown.Data data, Set<Player> players, CountdownCallback startCountDown, CountdownCallback completeCountdown) {
        this.data = data;
        this.waitingCountdown = data.waitingTime;
        this.countdown = data.waitingTime;
        this.players = players;
        this.start = startCountDown;
        this.complete = completeCountdown;
    }

    @Override
    public void run() {
        // Poner a los jugadores en modo aventura
        if (countdown == waitingCountdown) {
            //logica mensajes cada messageInterval segundos
            for (final Player player : players) {
                player.setGameMode(GameMode.SURVIVAL);
            }
            Messages.sendNoGet(
                    players,
                    Messages.get("invencibility.starting").replace("%time%", parseTime(countdown))
            );
        }

        // Enviar mensaje a los jugadores cada 'messageInterval' segundos
        if (countdown > 5 && countdown % 5 == 0) {
            Messages.sendNoGet(
                    players,
                    Messages.get("invencibility.start-in").replace("%time%", parseTime(countdown))
            );
        }
        if (countdown <= 0) {
            players.forEach((player) -> player.setLevel(0));

            complete.execute();
            Messages.send(players, "invencibility.finished");
            Bukkit.getScheduler().cancelTask(getId());
            return;
        }
        // Contar hacia atrás cada segundo cuando quedan 5 segundos
        if (countdown <= data.secondsToMakeSound) {
            Messages.sendNoGet(
                    players,
                    Messages.get("invencibility.start-in").replace("%time%", parseTime(countdown))
            );
            for (final Player player : players) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, -1);
            }
            countdown--;
        } else {
            // Reducir el contador interno
            countdown--;
        }
        /*if (players.size() < data.minPlayers) {
            countdown = data.waitingTime;
            --waitingCountdown;
            if (waitingCountdown <= 0) {
                waitingCountdown = data.waitingTime;
            }
            return;
        }

        players.forEach((player) -> player.setLevel(countdown));

        if (countdown == waitingCountdown){
            start.execute();
        }
        if (countdown <= 0) {
            players.forEach((player) -> player.setLevel(0));

            complete.execute();
            Messages.send(players, "invencibility.finished");
            Bukkit.getScheduler().cancelTask(getId());
            return;
        }

        // Send the message every x seconds
        if (countdown % data.messageTime == 0) {
            Messages.sendNoGet(
                    players,
                    Messages.get("invencibility.start-in").replace("%time%", parseTime(countdown))
            );
            countdown--;
            return;
        }
        if (countdown <= data.spamMessage) {
            Messages.sendNoGet(
                    players,
                    Messages.get("invencibility.start-in").replace("%time%", parseTime(countdown))
            );
        }

        if (countdown <= data.secondsToMakeSound) {
            for (final Player player : players) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, -1);
            }
        }

        countdown--;*/
    }

    public String getCountdown() {
        return parseTime(countdown);
    }

    public InvencibilityCountdown.Data getData() {
        return data;
    }

    public static record Data(
            int waitingTime,
            int messageTime,
            int secondsToMakeSound,
            int spamMessage) {
    }
}