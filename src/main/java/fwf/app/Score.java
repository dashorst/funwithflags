package fwf.app;

import fwf.game.Game;
import fwf.player.Player;

public record Score(Game game, Player player, int score) {
    
}
