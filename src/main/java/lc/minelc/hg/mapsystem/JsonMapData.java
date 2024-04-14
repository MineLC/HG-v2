package lc.minelc.hg.mapsystem;

public record JsonMapData(
    String world,
    String name,
    int borderSize,
    String[] spawns
) {
}