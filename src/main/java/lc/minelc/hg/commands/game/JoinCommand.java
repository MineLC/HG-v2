package lc.minelc.hg.commands.game;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.pregame.PregameStorage;
import lc.minelc.hg.mapsystem.MapData;
import lc.minelc.hg.mapsystem.MapStorage;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;

public final class JoinCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        if (args.length != 1) {
            sendWithColor(player, "&cFormat: /join &7(worldname)");
            return;
        }
        if (GameStorage.getStorage().getGame(player.getUniqueId()) != null) {
            send(sender, "Ya estás en un juego. Usa /leave para salir");
            return;
        }
        final MapData map = MapStorage.getStorage().getMapData(args[0]);
        if (map == null) {
            sendWithColor(player, "&cThis map don't exist. Available maps: " + MapStorage.getStorage().getMaps().keySet());
            return;
        }

        GameInProgress game = map.getGameInProgress();

        if (game == null) {
            game = new GameInProgress(map);
            map.setGame(game);
        }

        if (game.getState() == GameState.PREGAME || game.getState() == GameState.NONE) {
            final int maxPlayers = map.getMaxPlayers();
            if (game.getPlayers().size() >= maxPlayers) {
                Messages.send(player, "pregame.full");
                return;
            }
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(PregameStorage.getStorage().mapLocation());
    
            PregameStorage.getStorage().send(player);
            GameStorage.getStorage().join(args[0], game, player);
            SidebarStorage.getStorage().getSidebar(SidebarType.PREGAME).send(player);
            showPlayers(player, game.getPlayers());
            return;
        }

        // Ingame or endgame state. You can spectate but no play
        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(game.getWorld().getSpawnLocation());  
        GameStorage.getStorage().join(args[0], game, player);
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(player);
        showPlayers(player, game.getPlayers());
    }

    private void showPlayers(final Player player, final Set<Player> players) {
        for (final Player otherPlayer : players) {
            otherPlayer.showPlayer(player);
            player.showPlayer(otherPlayer);
        }
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        return (args.length == 2) ? (String[])MapStorage.getStorage().getMaps().keySet().toArray() : none();
    }
}