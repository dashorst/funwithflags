package fwf.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import fwf.ApplicationStatus;
import fwf.FunWithFlagsGame;
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
    Event<PlayerRegistered> playerRegistered;

    @Inject
    Event<PlayerUnregistered> playerUnregistered;

    @Inject
    Event<PlayerGuessed> playerGuessed;

    @Inject
    Event<GameStarted> gameStarted;

    @Inject
    Event<GameDestroyed> gameDestroyed;

    @Inject
    Instance<Player> playerFactory;

    @Inject
    Instance<Game> gameFactory;

    private Map<Session, Player> players = new ConcurrentHashMap<>();

    private ConcurrentLinkedDeque<Game> games = new ConcurrentLinkedDeque<>();

    @Override
    public Player registerPlayer(Session session, String name) {
        var player = playerFactory.get();
        player.init(session, name);
        var oldPlayer = players.put(session, player);
        if (oldPlayer != null) {
            playerUnregistered.fire(new PlayerUnregistered(oldPlayer));
        }
        playerRegistered.fire(new PlayerRegistered(player));
        return player;
    }

    @Override
    public Player unregisterPlayer(Session session) {
        var player = players.remove(session);
        if (player != null) {
            playerUnregistered.fire(new PlayerUnregistered(player));
            playerFactory.destroy(player);
        }
        return player;
    }

    @Override
    public void tick() {
        for (Game game : games) {
            game.tick();
        }
    }

    @Override
    public void guess(Session session, int turnNumber, Country country) {
        var player = players.get(session);
        if (player == null)
            return;

        playerGuessed.fire(new PlayerGuessed(turnNumber, player, country));
    }

    @Override
    public void destroyGame(Game game) {
        gameDestroyed.fire(new GameDestroyed(game));
    }

    @Override
    public int numberOfGames() {
        return games.size();
    }

    ConcurrentLinkedDeque<Game> games() {
        return games;
    }

    void startGame(@Observes LobbyFilled lobbyFilled) {
        var game = gameFactory.get();
        game.init(lobbyFilled.playersInLobby(), FunWithFlagsGame.NUMBER_OF_TURNS_PER_GAME, FunWithFlagsGame.SECONDS_PER_TURN);
        games.add(game);
        gameStarted.fire(new GameStarted(game));
    }

    void destroyed(@Observes GameDestroyed event) {
        Game finishedGame = event.game();
        games.remove(finishedGame);
        gameFactory.destroy(finishedGame);
    }
}
