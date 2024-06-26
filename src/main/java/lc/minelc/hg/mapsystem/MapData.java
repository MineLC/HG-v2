package lc.minelc.hg.mapsystem;


import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.utils.EntityLocation;

public final class MapData {
    private final String name, worldName;
    private final EntityLocation[] spawns;
    private final int maxPlayers;
    private final int borderSize;
    private final int id;

    private GameInProgress gameInProgress;

    MapData(
        EntityLocation[] spawns,
        int maxPlayers,
        int borderSize,
        int id,
        String name,
        String worldName
    ) {
        this.spawns = spawns;
        this.maxPlayers = maxPlayers;
        this.borderSize = borderSize;
        this.id = id;
        this.name = name;
        this.worldName = worldName;
    }

    public void setGame(GameInProgress game) {
        this.gameInProgress = game;
    }

    public GameInProgress getGameInProgress() {
        return gameInProgress;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getName() {
        return name;
    }

    public EntityLocation[] getSpawns() {
        return spawns;
    }

    @Override
    public final int hashCode() {
        return id;
    }

    @Override
    public final boolean equals(Object object) {
        return (object instanceof MapData otherMapData) ? otherMapData.id == this.id : false;
    }

    @Override
    public String toString() {
        return worldName;
    }
}