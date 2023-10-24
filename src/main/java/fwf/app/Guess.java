package fwf.app;

public record Guess(
                Player player,
                Turn turn,
                Game game,
                Country guessedCountry) {
        public boolean isCorrect() {
                return turn.countryToGuess().equals(guessedCountry);
        }
}
