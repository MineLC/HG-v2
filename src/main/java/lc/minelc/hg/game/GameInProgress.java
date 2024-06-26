package lc.minelc.hg.game;

import java.util.Set;
import java.util.HashSet;

import lc.minelc.hg.game.countdown.GameCountdown;
import lc.minelc.hg.mapsystem.MapData;
import lc.minelc.hg.others.events.GameEvent;
import lc.minelc.hg.others.top.KillsTop;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class GameInProgress {

    private final MapData data;

    private final Set<Player> players = new HashSet<>();
    private final KillsTop gameTop = new KillsTop();
    
    private World world;
    private GameState state = GameState.NONE;
    private boolean invincibility = true;
    private GameCountdown countdown;

    private GameEvent[] events;
    private int currentEvent = 0;
    private GameEvent activeEvent;

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

    public void setInvincibility(boolean value){
        this.invincibility = value;
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

    public KillsTop getTop() {
        return gameTop;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public boolean getInvincibility() {
        return invincibility;
    }
    public GameCountdown getCountdown() {
        return countdown;
    }

    public GameEvent[] getEvents() {
        return events;
    }

    public void setActiveEvent(final GameEvent event) {
        this.activeEvent = event;
    }

    public GameEvent getActiveEvent() {
        return activeEvent;
    }

    public GameEvent getNextEvent() {
        return (currentEvent >= events.length) ? null : events[currentEvent];
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

    public void setWorld(final World world) {
        this.world = world;
    }
}