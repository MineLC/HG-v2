package lc.minelc.hg.others.events;

public record GameEvent(String name, String information, int secondToStart, int duration, GameEventType eventType) {
}