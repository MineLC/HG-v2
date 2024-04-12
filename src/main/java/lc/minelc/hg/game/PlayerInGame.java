package lc.minelc.hg.game;


import lc.minelc.hg.others.abilities.GameAbility;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;

@Getter
public final class PlayerInGame {

    private final GameInProgress game;
    @Setter
    private GameAbility[] gameAbilities;

    PlayerInGame(GameInProgress game) {
        this.game = game;
    }
}