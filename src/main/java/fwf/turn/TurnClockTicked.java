package fwf.turn;

import fwf.game.Game;

public record TurnClockTicked(Game game, Turn turn, int secondsLeft) {
}
