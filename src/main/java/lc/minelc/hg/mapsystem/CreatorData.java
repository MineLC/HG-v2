package lc.minelc.hg.mapsystem;

import java.util.HashSet;
import java.util.Set;

import lc.minelc.hg.utils.EntityLocation;

public final class CreatorData {

    private final Set<EntityLocation> spawns = new HashSet<>();

    public Set<EntityLocation> getSpawns() {
        return spawns;
    }
}