package fwf.app;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.quarkus.logging.Log;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

public class Turn {
    @Inject
    Event<TurnClockTicked> clockTicked;

    @Inject
    Event<TurnGuessRecorded> guessRecorded;

    private Game game;
    private int turnNumber;
    private Country countryToGuess;
    private boolean done = false;
    private boolean resultDone = false;

    private Map<Player, Guess> guesses = new ConcurrentHashMap<>();
    private int turnTicksRemaining;
    private int resultTicksRemaining;

    void init(Game game, int turnNumber, Country country, int secondsPerTurn, int secondsPerResult) {
        this.game = Objects.requireNonNull(game);
        this.turnNumber = turnNumber;
        this.countryToGuess = Objects.requireNonNull(country);
        this.turnTicksRemaining = secondsPerTurn;
        this.resultTicksRemaining = secondsPerResult;
    }

    public void guess(@Observes PlayerGuessed event) {
        var turn = event.turn();
        var game = event.game();
        var player = event.player();

        if (turn.isDone()) {
            Log.infof("Turn %s is done, guess for player %s not recorded", turn, event.player().name());
            return;
        }

        if (!game.players().contains(player)) {
            Log.infof("Player %s is not part of the game %s, guess not recorded", player, game);
            return;
        }

        var guess = new Guess(player, turn, game, event.country());
        turn.guesses.put(player, guess);

        guessRecorded.fire(new TurnGuessRecorded(game, turn, player, guess));
    }

    public int scoreForPlayer(Player player) {
        if (!done)
            return 0;

        var guess = guesses.get(player);
        return Optional.ofNullable(guess).map(Guess::guessedCountry).filter(g -> countryToGuess.equals(g)).map(g -> 1)
                .orElse(0);
    }

    void tick() {
        if (game == null)
            return;

        turnTicksRemaining = Math.max(-1, turnTicksRemaining - 1);
        done = turnTicksRemaining == -1;
        if (done) {
            resultTicksRemaining = Math.max(-1, resultTicksRemaining - 1);
            resultDone = resultTicksRemaining == -1;
        }
        Log.infof("Game turn ticked: %s, secondsLeft: %d", game.players().stream().map(Player::name).toList(),
                turnTicksRemaining);
        clockTicked.fire(new TurnClockTicked(game, this, secondsLeft()));
    }

    public int turnNumber() {
        return turnNumber;
    }

    public Collection<Guess> guesses() {
        return guesses.values();
    }

    public Country countryToGuess() {
        return countryToGuess;
    }

    public int secondsLeft() {
        return Math.max(0, turnTicksRemaining);
    }

    public int resultsSecondsLeft() {
        return Math.max(0, resultTicksRemaining);
    }

    public boolean isDone() {
        return done;
    }

    public boolean isResultDone() {
        return resultDone;
    }

    @Override
    public String toString() {
        return String.format("Turn %d of game %s: %d seconds remaining", turnNumber, game, secondsLeft());
    }
}
