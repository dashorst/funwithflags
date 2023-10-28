package fwf.guess;

import fwf.country.Country;
import fwf.game.Game;
import fwf.player.Player;
import fwf.turn.Turn;

public record Guess(
                Player player,
                Turn turn,
                Game game,
                Country guessedCountry) {
        public boolean isCorrect() {
                return turn.countryToGuess().equals(guessedCountry);
        }
}
