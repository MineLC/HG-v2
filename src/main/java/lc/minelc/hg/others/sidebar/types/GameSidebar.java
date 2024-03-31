package lc.minelc.hg.others.sidebar.types;

import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.LightSidebarLib;
import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.others.sidebar.EggwarsSidebar;

public final class GameSidebar implements EggwarsSidebar {
    private final String title;

    public GameSidebar(String title) {
        this.title = title;
    }

    @Override
    public void send(Player player) {
        send(List.of(player));
    }

    @Override
    public void send(Collection<Player> players) {
        final GameInProgress game = GameStorage.getStorage().getGame(players.iterator().next().getUniqueId());
        if (game == null) {
            return;
        }
        final Sidebar sidebar = new LightSidebarLib().createSidebar();

        sidebar.setLines(sidebar.createLines(new String[] {"test"}));
        sidebar.setTitle(title);

        for (final Player player : players) {
            sidebar.sendLines(player);
            sidebar.sendTitle(player);
        }
    }
}