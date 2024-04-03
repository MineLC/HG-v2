package lc.minelc.hg.others.sidebar.types;

import java.util.Collection;

import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.minelc.hg.others.sidebar.HgSidebar;

public final class GameSidebar implements HgSidebar {

    private final Sidebar sidebar;

    public GameSidebar(Sidebar sidebar) {
        this.sidebar = sidebar;
    }

    @Override
    public void send(Player player) {
        sidebar.delete(player);
    }

    @Override
    public void send(Collection<Player> players) {
        sidebar.delete(players);
    }
}