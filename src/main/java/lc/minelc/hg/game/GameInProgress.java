package lc.minelc.hg.game;

import java.util.Set;
import java.util.HashSet;

import lc.minelc.hg.game.countdown.GameCountdown;
import lc.minelc.hg.mapsystem.MapData;
import lc.minelc.hg.others.events.GameEvent;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class GameInProgress {

    private final MapData data;

    private final Set<Player> players = new HashSet<>();

    private World world;
    private GameState state = GameState.NONE;
    private GameCountdown countdown;

    private GameEvent[] events;
    private int currentEvent = 0;

    private long startTime;

    public GameInProgress(MapData data) {
        this.data = data;
    }

    public void setState(final GameState state) {
        this.state = state;
    }

    public void startTime() {
        this.startTime = System.currentTimeMillis();
    }

    public void setCountdown(GameCountdown countdown) {
        this.countdown = countdown;
    }

    public MapData getMapData() {
        return data;
    }

    public World getWorld() {
        return world;
    }

    public long getStartedTime() {
        return startTime;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public GameCountdown getCountdown() {
        return countdown;
    }

    public GameEvent[] getEvents() {
        return events;
    }

    public GameEvent getCurrentEvent() {
        return (currentEvent >= events.length) ? null : events[currentEvent];
    }

    public int getCurrentEventIndex() {
        return currentEvent;
    }

    public void nextEvent() {
        currentEvent++;
    }

    public void setEvents(final GameEvent[] events) {
        this.events = events;
    }

    public boolean playerIsDead(final Player player) {
        return player.getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof GameInProgress otherGame) ? otherGame.data.equals(this.data) : false;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    void setWorld(final World world) {
        if (this.world == null) {
            this.world = world;
        }
    }
}