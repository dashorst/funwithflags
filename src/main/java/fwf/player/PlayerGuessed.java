package fwf.player;

import fwf.country.Country;
import fwf.game.Game;
import fwf.turn.Turn;

public record PlayerGuessed(Game game, Turn turn, Player player, Country country) {
}
