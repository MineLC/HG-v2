package lc.minelc.hg.others.sidebar;

import java.util.Collection;

import org.bukkit.entity.Player;

public interface HgSidebar {
    void send(final Player player);
    void send(final Collection<Player> players);
}