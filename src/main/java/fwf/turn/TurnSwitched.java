package fwf.turn;

import fwf.game.Game;

public record TurnSwitched(Game game, Turn previousTurn, Turn currentTurn) {
}
