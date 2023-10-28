package fwf.admin;

import java.util.concurrent.atomic.AtomicInteger;

import fwf.game.GameFinished;
import fwf.game.GameStarted;
import fwf.player.PlayerGuessed;
import fwf.player.PlayerRegistered;
import fwf.player.PlayerUnregistered;
import fwf.turn.TurnFinished;
import fwf.turn.TurnStarted;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
class Statistics {
    AtomicInteger playersRegistered = new AtomicInteger(0);
    AtomicInteger playersUnregistered = new AtomicInteger(0);
    AtomicInteger gamesStarted = new AtomicInteger(0);
    AtomicInteger gamesPlayed = new AtomicInteger(0);
    AtomicInteger turnsStarted = new AtomicInteger(0);
    AtomicInteger turnsPlayed = new AtomicInteger(0);
    AtomicInteger countriesGuessed = new AtomicInteger(0);
    AtomicInteger correctGuesses = new AtomicInteger(0);

    public int playersRegistered() {
        return playersRegistered.get();
    }

    public int playersUnregistered() {
        return playersUnregistered.get();
    }

    public int gamesStarted() {
        return gamesStarted.get();
    }

    public int gamesPlayed() {
        return gamesPlayed.get();
    }

    public int turnsStarted() {
        return turnsStarted.get();
    }

    public int turnsPlayed() {
        return turnsPlayed.get();
    }

    public int countriesGuessed() {
        return countriesGuessed.get();
    }

    public int correctGuesses() {
        return correctGuesses.get();
    }

    public int correctPercentage() {
        if(countriesGuessed() == 0) return 100;
        return (100 * correctGuesses()) / countriesGuessed();
    }

    void onPlayerRegistered(@Observes PlayerRegistered event) {
        playersRegistered.incrementAndGet();
    }

    void onPlayerUnregistered(@Observes PlayerUnregistered event) {
        playersUnregistered.incrementAndGet();
    }

    void onGameStarted(@Observes GameStarted event) {
        gamesStarted.incrementAndGet();
    }

    void onGameFinished(@Observes GameFinished event) {
        gamesPlayed.incrementAndGet();
    }

    void onTurnStarted(@Observes TurnStarted event) {
        turnsStarted.incrementAndGet();
    }

    void onTurnPlayed(@Observes TurnFinished event) {
        turnsPlayed.incrementAndGet();
    }

    void onCountryGuessed(@Observes PlayerGuessed event) {
        countriesGuessed.incrementAndGet();
        if (event.turn().countryToGuess().equals(event.country()))
            correctGuesses.incrementAndGet();
    }
}
