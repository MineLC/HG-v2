package lc.minelc.hg.inventory.types;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.pregame.PregameStorage;
import lc.minelc.hg.mapsystem.MapData;
import lc.minelc.hg.others.selectgame.MapSelectorInventoryHolder;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.minelc.hg.others.tab.TabStorage;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase.EnumNameTagVisibility;
import obed.me.lccommons.api.entities.PlayerData;
import obed.me.lccommons.api.services.UserProvider;

public final class MapSelectorInventory {

    public void handle(final MapSelectorInventoryHolder mapSelector, final InventoryClickEvent event) {
        final MapData map = mapSelector.getGame(event.getSlot());
        event.setCancelled(true);
        if (map != null) {
            tryJoinToGame((Player)event.getWhoClicked(), map);
        }
    }
    
    private void tryJoinToGame(final Player player, final MapData map) {
        GameInProgress game = map.getGameInProgress();

        if (game == null) {
            game = new GameInProgress(map);
            map.setGame(game);
        }
        if (game.getState() == GameState.PREGAME || game.getState() == GameState.NONE) {
            PregameStorage.getStorage().send(player);
            GameStorage.getStorage().join(map.toString(), game, player);
            SidebarStorage.getStorage().getSidebar(SidebarType.PREGAME).send(player);

            TabStorage.getStorage().removeOnePlayer(player, SpawnStorage.getStorage().getPlayers());
            TabStorage.getStorage().removePlayers(player, SpawnStorage.getStorage().getPlayers());
            TabStorage.getStorage().sendPlayerInfo(player, game.getPlayers());

            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(PregameStorage.getStorage().mapLocation());
            final PacketPlayOutScoreboardTeam team = getTeamPacket(player);
    
            for (final Player gamePlayer : game.getPlayers()) {
                gamePlayer.showPlayer(player);
                player.showPlayer(gamePlayer);
                if (team != null) {
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(team);
                }
            }
            return;
        }
        // Ingame or endgame state. You can spectate but no play
        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(game.getWorld().getSpawnLocation());  

        GameStorage.getStorage().join(map.toString(), game, player);
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(player);

        TabStorage.getStorage().removeOnePlayer(player, SpawnStorage.getStorage().getPlayers());
        TabStorage.getStorage().removePlayers(player, SpawnStorage.getStorage().getPlayers());
        TabStorage.getStorage().sendPlayerInfo(player, game.getPlayers());

        for (final Player gamePlayer : game.getPlayers()) {
            player.showPlayer(gamePlayer);
        }
    }

    private PacketPlayOutScoreboardTeam getTeamPacket(final Player player) {
        final PlayerData data = UserProvider.getInstance().getUserCache(player.getName());
        if (data == null) {
            return null;
        }
        final ScoreboardTeam team = new ScoreboardTeam(MinecraftServer.getServer().getWorld().scoreboard, player.getName());
        team.setPrefix(data.getRankInfo().getRank().getPrefix() + data.getRankInfo().getUserColor() + " ");
        team.b(EnumNameTagVisibility.ALWAYS);
        return new PacketPlayOutScoreboardTeam(team, 2);
    }
}