package fwf.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import fwf.app.Score;
import fwf.country.CountryRepository;
import fwf.player.Player;
import fwf.player.PlayerUnregistered;
import fwf.turn.Turn;
import fwf.turn.TurnFinished;
import fwf.turn.TurnStarted;
import fwf.turn.TurnSwitched;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@Dependent
public class Game {
    @Inject
    Instance<Turn> turnFactory;

    @Inject
    Event<GameFinished> gameFinished;

    @Inject
    Event<TurnStarted> turnStarted;

    @Inject
    Event<TurnSwitched> turnSwitched;

    @Inject
    Event<TurnFinished> turnFinished;

    @Inject
    CountryRepository countryRepository;

    private List<Player> players = Collections.emptyList();
    private List<Turn> turns = new ArrayList<>();

    private int secondsPerTurn;

    private int secondsPerResult;

    private boolean gameOver = false;

    private Turn currentTurn = null;

    public Game() {
    }

    public void init(List<Player> playersInLobby, int numberOfTurns, int secondsPerTurn, int secondsPerResult) {
        this.players = playersInLobby;
        this.secondsPerResult = secondsPerResult;
        this.secondsPerTurn = secondsPerTurn;

        var countries = new ArrayList<>(countryRepository.countries());
        Collections.shuffle(countries);

        for (int i = 0; i < numberOfTurns; i++) {
            var turn = turnFactory.get();

            turn.init(this, i + 1, countries.get(i), secondsPerTurn, secondsPerResult);
            turns.add(turn);
        }
        currentTurn = turns.get(0);
        turnStarted.fire(new TurnStarted(this, currentTurn));
    }

    public List<Turn> turns() {
        return turns;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int numberOfTurns() {
        return turns.size();
    }

    public int turnNumber() {
        if (currentTurn == null)
            return numberOfTurns();
        return turns.indexOf(currentTurn) + 1;
    }

    public Collection<Player> players() {
        return Collections.unmodifiableCollection(players);
    }

    public List<Score> scores() {
        var scores = new ArrayList<Score>();
        for (Player player : players) {
            int score = 0;
            for (Turn turn : turns) {
                score += turn.scoreForPlayer(player);
            }
            scores.add(new Score(this, player, score));
        }
        Collections.sort(scores, Comparator.comparing(Score::score, Comparator.reverseOrder()));
        return scores;
    }

    public Optional<Turn> currentTurn() {
        return Optional.ofNullable(currentTurn);
    }

    // todo observe ClockTick event
    public void onTick() {
        if (gameOver)
            return;

        if (currentTurn == null)
            return;

        if (currentTurn.isDone()) {
            if (!currentTurn.isResultDone()) {
                currentTurn.tick();
                turnFinished.fire(new TurnFinished(this, currentTurn));
                Log.debugf("Game ticked: %s, turn: #%d, results left: %ds",
                        players().stream().map(Player::name).toList(), turnNumber(), currentTurn.resultsSecondsLeft());
            } else {
                var previousTurn = currentTurn;
                var nextTurn = turns.stream().filter(t -> !t.isDone()).findFirst();
                if (nextTurn.isPresent()) {
                    currentTurn = nextTurn.get();
                    turnSwitched.fire(new TurnSwitched(this, previousTurn, currentTurn));
                    turnStarted.fire(new TurnStarted(this, currentTurn));
                } else {
                    gameOver = true;
                    gameFinished.fire(new GameFinished(this, "Game over"));
                }
            }
        } else {
            Log.debugf("Game ticked: %s, turn: #%d, seconds left: %ds", players().stream().map(Player::name).toList(),
                    turnNumber(), currentTurn.secondsLeft());
            currentTurn.tick();
        }
    }

    void onPlayerUnregistered(@Observes PlayerUnregistered event) {
        var playerToRemove = event.player();
        if (players.remove(playerToRemove)) {
            if (players.size() <= 1) {
                // fire game over
                gameFinished.fire(new GameFinished(this, "Last player standing"));
            }
        }
    }

    void onGameDestroyed(@Observes GameDestroyed event) {
        for (Turn turn : event.game().turns) {
            turnFactory.destroy(turn);
        }
        event.game().turns.clear();
    }

    @Override
    public String toString() {
        return players.stream().map(Player::name).toList() + ", turn " + turnNumber();
    }
}
