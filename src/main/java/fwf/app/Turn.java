package fwf.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.quarkus.logging.Log;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

public class Turn {
    @Inject
    Event<TurnClockTicked> clockTicked;

    @Inject
    Event<TurnFinished> turnFinished;

    private Game game;
    private int turnNumber;
    private Country countryToGuess;
    private boolean done = false;

    private Map<Player, Guess> guesses = new ConcurrentHashMap<>();
    private int ticksRemaining;

    void init(Game game, int turnNumber, Country country, int secondsPerTurn) {
        this.game = game;
        this.turnNumber = turnNumber;
        this.countryToGuess = country;
        this.ticksRemaining = secondsPerTurn;
    }

    public void guess(@Observes PlayerGuessed playerGuessed) {
        if (done)
            return;

        Player player = playerGuessed.player();

        if (!game.players().contains(player))
            return;

        if (turnNumber != playerGuessed.turn())
            return;

        guesses.put(player, new Guess(player, this, game, playerGuessed.country()));
    }

    public int scoreForPlayer(Player player) {
        var guessed = guesses.get(player);
        if (countryToGuess.equals(guessed == null ? null : guessed.guessedCountry()))
            return 1;
        return 0;
    }

    void tick() {
        if (game == null)
            return;

        ticksRemaining = Math.max(0, ticksRemaining - 1);
        done = ticksRemaining == 0;
        Log.infof("Game turn ticked: %s, secondsLeft: %d", game.players().stream().map(Player::name).toList(),
                ticksRemaining);

        clockTicked.fire(new TurnClockTicked(game, this, ticksRemaining));
    }

    public int turnNumber() {
        return turnNumber;
    }

    public Country countryToGuess() {
        return countryToGuess;
    }

    public int secondsLeft() {
        return ticksRemaining;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        return String.format("Turn %d of game %s: %d seconds remaining", turnNumber, game, secondsLeft());
    }
}
