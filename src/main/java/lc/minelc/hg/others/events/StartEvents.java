package lc.minelc.hg.others.events;

import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.events.special.DeathMatchEvent;
import lc.minelc.hg.others.events.special.PotionEvent;
import lc.minelc.hg.others.kits.StartKits;

public final class StartEvents {

    private final FileConfiguration config;
    private final ArenaHGPlugin plugin;

    public StartEvents(ArenaHGPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.loadConfig("events");
    }

    public void load() {
        EventStorage.update(new EventStorage(
            plugin,
            getDeathMatchData(),
            getBaseEvents(),
            createBaseEvent("deathmatch", GameEventType.DEATHMATCH),
            config.getInt("start-time-tolerance"),
            getPotionEventData(),
            getSecondsToMakeEvent()
        ));
    }

    private DeathMatchEvent.Data getDeathMatchData() {
        return new DeathMatchEvent.Data(
            config.getInt("deathmatch.regresive-count"),
            config.getInt("deathmatch.worldborder-size"),
            config.getInt("deathmatch.potions-add-every-seconds"),
            new StartKits(plugin).createPotionEffects(config, "deathmatch.effects")
        );
    }

    private GameEvent[] getBaseEvents() {
        final GameEventType[] types = GameEventType.values();
        final GameEvent[] events = new GameEvent[types.length - 1];

        for (int i = 1; i < types.length; i++) {
            events[i - 1] = createBaseEvent(types[i].name().toLowerCase(), types[i]);
        }
        return events;
    }

    private PotionEvent.Data getPotionEventData() {
        final StartKits util = new StartKits(plugin);
        return new PotionEvent.Data(
            util.createPotionEffects(config, "fatigue.effects"),
            util.createPotionEffects(config, "willpower.effects"),
            util.createPotionEffects(config, "rush.effects")
        );
    }

    private int[] getSecondsToMakeEvent() {
        final List<Integer> seconds = config.getIntegerList("events-seconds");
        final int size = seconds.size();
        final int[] copy = new int[size];
        for (int i = 0; i < size; i++) {
            copy[i] = seconds.get(i);
        }
        Arrays.sort(copy);
        return copy;
    }

    private GameEvent createBaseEvent(final String section, final GameEventType type) {
        return new GameEvent(
            Messages.color(config.getString(section + ".name")),
            Messages.color(config.getString(section + ".info")),
            config.getInt(section + ".start-time"),
            config.getInt(section + ".duration"),
            type);
    }
}