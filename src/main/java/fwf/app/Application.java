package fwf.app;

import java.util.concurrent.ConcurrentLinkedDeque;

import fwf.ApplicationStatus;
import fwf.FunWithFlagsGame;
import fwf.clock.Clock;
import fwf.config.Configuration;
import fwf.country.Country;
import fwf.game.Game;
import fwf.game.GameDestroyed;
import fwf.game.GameFinished;
import fwf.game.GameStarted;
import fwf.lobby.LobbyFilled;
import fwf.player.Player;
import fwf.player.PlayerGuessed;
import fwf.player.PlayerRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.websocket.Session;

@ApplicationScoped
@Named("funWithFlags")
public class Application implements FunWithFlagsGame, ApplicationStatus {
    @Inject
    Configuration configuration;

    @Inject
    Event<PlayerGuessed> playerGuessed;

    @Inject
    Event<GameStarted> gameStarted;

    @Inject
    Event<GameDestroyed> gameDestroyed;

    @Inject
    Instance<Game> gameFactory;

    @Inject
    Clock clock;

    @Inject
    PlayerRepository playerRepository;

    private ConcurrentLinkedDeque<Game> games = new ConcurrentLinkedDeque<>();

    private ConcurrentLinkedDeque<Game> finishedGames = new ConcurrentLinkedDeque<>();

    public int numberOfTurnsPerGame() {
        return configuration.numberOfTurnsPerGame();
    }

    public int numberOfPlayersPerGame() {
        return configuration.numberOfPlayersPerGame();
    }

    public int numberOfSecondsPerResult() {
        return configuration.numberOfSecondsPerResult();
    }

    public int numberOfSecondsPerTurn() {
        return configuration.numberOfSecondsPerTurn();
    }

    @Override
    public void registerPlayer(Session session, String name) {
        playerRepository.registerPlayer(session, name);
    }

    @Override
    public void unregisterPlayer(Session session) {
        playerRepository.unregisterPlayer(session);
    }

    @Override
    public void tick() {
        for (Game game : games) {
            game.onTick();
        }
    }

    @Override
    public void guess(Player player, int turnNumber, Country country) {
        var game = games.stream().filter(g -> g.players().contains(player)).findFirst();
        if (game.isEmpty()) {
            Log.infof("Game for player %s not found", player);
            return;
        }
        var turn = game.get().currentTurn();
        if (turn.isEmpty()) {
            Log.infof("No turn active for game %s", game.get());
            return;
        }
        playerGuessed.fire(new PlayerGuessed(game.get(), turn.get(), player, country));
    }

    @Override
    public void destroyGame(Game game) {
        gameDestroyed.fire(new GameDestroyed(game));
    }

    @Override
    public int numberOfGames() {
        return games.size();
    }

    public ConcurrentLinkedDeque<Game> games() {
        return games;
    }

    void startGame(@Observes LobbyFilled lobbyFilled) {
        var game = gameFactory.get();
        game.init(lobbyFilled.playersInLobby(), numberOfTurnsPerGame(),
                numberOfSecondsPerTurn(), numberOfSecondsPerResult());
        games.add(game);
        gameStarted.fire(new GameStarted(game));
    }

    void onGameFinished(@Observes GameFinished event) {
        finishedGames.add(event.game());
    }

    void destroyed(@Observes GameDestroyed event) {
        Game finishedGame = event.game();
        games.remove(finishedGame);
        finishedGames.remove(finishedGame);
        gameFactory.destroy(finishedGame);
    }
}
