package fwf.app;

public record TurnSwitched(Game game, Turn previousTurn, Turn currentTurn) {
}
