package fwf.app;

public record Guess(
        Player player,
        Turn turn,
        Game game,
        Country guessedCountry) {
}
