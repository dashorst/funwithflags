package fwf.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    Event<TurnFinished> turnFinished;

    @Inject
    CountryRepository countryRepository;

    private List<Player> players = Collections.emptyList();
    private List<Turn> turns = new ArrayList<>();

    private boolean gameOver = false;

    private Turn currentTurn = null;

    private int turnSummaryCountdown = 10;

    public Game() {
    }

    public void init(List<Player> playersInLobby, int numberOfTurns, int secondsPerTurn) {
        this.players = playersInLobby;

        var countries = new ArrayList<>(countryRepository.countries());
        Collections.shuffle(countries);

        for (int i = 0; i < numberOfTurns; i++) {
            var turn = turnFactory.get();

            turn.init(this, i + 1, countries.get(i), secondsPerTurn);
            turns.add(turn);
        }
        currentTurn = turns.get(0);
        turnStarted.fire(new TurnStarted(this, currentTurn));
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

    public Turn currentTurn() {
        return currentTurn;
    }

    void tick() {
        if (gameOver)
            return;

        if (currentTurn.isDone()) {
            turnFinished.fire(new TurnFinished(this, currentTurn));
            var nextTurn = turns.stream().filter(t -> !t.isDone()).findFirst();
            if (nextTurn.isPresent()) {
                currentTurn = nextTurn.get();
                turnStarted.fire(new TurnStarted(this, currentTurn));
            } else {
                gameOver = true;
                gameFinished.fire(new GameFinished(this, "Game over"));
            }
        } else {
            Log.infof("Game ticked: %s, turn: #%d", players().stream().map(Player::name).toList(), turnNumber());
            currentTurn.tick();
        }
    }

    void removePlayer(@Observes PlayerUnregistered playerUnregistered) {
        var playerToRemove = playerUnregistered.player();
        if (players.remove(playerToRemove)) {
            if (players.size() <= 1) {
                // fire game over
                gameFinished.fire(new GameFinished(this, "Last player standing"));
            }
        }
    }

    void destroy(@Observes GameDestroyed gameDestroyed) {
        for (Turn turn : gameDestroyed.game().turns) {
            turnFactory.destroy(turn);
        }
        gameDestroyed.game().turns.clear();
    }

    @Override
    public String toString() {
        return Objects.toString(players) + ", turn " + turnNumber();
    }
}
