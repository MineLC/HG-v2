package lc.minelc.hg.game;


public final class PlayerInGame {

    private final GameInProgress game;

    PlayerInGame(GameInProgress game) {
        this.game = game;
    }

    public GameInProgress getGame() {
        return game;
    }
}