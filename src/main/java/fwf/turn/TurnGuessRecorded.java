package fwf.turn;

import fwf.game.Game;
import fwf.guess.Guess;
import fwf.player.Player;

public record TurnGuessRecorded(Game game, Turn turn, Player player, Guess guess) {
}
