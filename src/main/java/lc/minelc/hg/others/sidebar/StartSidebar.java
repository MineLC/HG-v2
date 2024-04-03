package lc.minelc.hg.others.sidebar;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.ichocomilk.lightsidebar.LightSidebarLib;
import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.sidebar.types.GameSidebar;
import lc.minelc.hg.others.sidebar.types.PregameSidebar;
import lc.minelc.hg.others.sidebar.types.SpawnSidebar;

public final class StartSidebar {

    private final FileConfiguration config;

    public StartSidebar(ArenaHGPlugin plugin) {
        this.config = plugin.loadConfig("sidebars");
    }

    public void load() {
        final HgSidebar[] sidebars = new HgSidebar[3];

        sidebars[SidebarType.SPAWN.ordinal()] = createSidebar(config, "spawn", SidebarType.SPAWN);
        sidebars[SidebarType.PREGAME.ordinal()] = createSidebar(config, "pregame", SidebarType.PREGAME);
        sidebars[SidebarType.IN_GAME.ordinal()] = createSidebar(config, "ingame", SidebarType.IN_GAME);

        SidebarStorage.update(new SidebarStorage(sidebars));
    }

    private HgSidebar createSidebar(final FileConfiguration config, final String path, final SidebarType type) {
        final String sidebarPath = path + '.';
        final String[] lines = toArray(config.getStringList(sidebarPath + "lines")); 
        final String title = Messages.color(config.getString(sidebarPath + "title"));
    
        final HgSidebar sidebar = getSidebar(type, lines, title);

        return sidebar;
    }

    private String[] toArray(final List<String> list) {
        if (list.isEmpty()) {
            return new String[] { "" };
        }
        final String[] array = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = Messages.color(list.get(i));
        }
        return array;
    }

    private HgSidebar getSidebar(final SidebarType type, final String[] lines, final String title) {
        switch (type) {
            case SPAWN: return new SpawnSidebar(lines, title);
            case PREGAME: return new PregameSidebar(lines, title);
            case IN_GAME: return new GameSidebar(new LightSidebarLib().createSidebar());
            default: return null;
        }
    }
}