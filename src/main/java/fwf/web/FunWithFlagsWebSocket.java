package fwf.web;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fwf.ApplicationStatus;
import fwf.FunWithFlagsGame;
import fwf.app.Country;
import fwf.app.Game;
import fwf.app.GameFinished;
import fwf.app.GameStarted;
import fwf.app.Guess;
import fwf.app.Lobby;
import fwf.app.LobbyFilled;
import fwf.app.Player;
import fwf.app.PlayerRegistered;
import fwf.app.Score;
import fwf.app.Turn;
import fwf.app.TurnClockTicked;
import fwf.app.TurnFinished;
import fwf.app.TurnGuessRecorded;
import fwf.app.TurnStarted;
import io.quarkus.logging.Log;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.Encoded;

@ServerEndpoint("/game/{player}")
@ApplicationScoped
public class FunWithFlagsWebSocket {
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance submissionPartial(Country choice);
        
        public static native TemplateInstance game$countdownPartial(Player receiver, Game game, Turn turn);

        public static native TemplateInstance game$rankingPartial(Player receiver, Game game, Turn turn);

        public static native TemplateInstance lobby(Player receiver, List<Player> players);

        public static native TemplateInstance game(Player receiver, Game game, Turn turn);

        public static native TemplateInstance game$turnPartial(Player receiver, Game game, Turn turn, Country countryToGuess);

        public static native TemplateInstance turnover(Player receiver, Game game, Turn turn, Guess guess);

        public static native TemplateInstance gameover(Player receiver, Game game);
    }

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    FunWithFlagsGame funWithFlagsGame;

    @Inject
    ApplicationStatus applicationStatus;

    @Inject
    Lobby lobby;

    @OnOpen
    public void onOpen(Session session, @PathParam("player") String encodedPlayer) {
        var player = URLDecoder.decode(encodedPlayer, StandardCharsets.UTF_8);
        Log.infof("User %s joined", player);
        sessions.put(player, session);
        funWithFlagsGame.registerPlayer(session, player);
    }

    @OnError
    public void onError(Session session, @PathParam("player") String encodedPlayer, Throwable throwable) {
        var player = URLDecoder.decode(encodedPlayer, StandardCharsets.UTF_8);
        sessions.remove(player);
        Log.infof("User %s left on error: ", player, throwable);
    }

    @Scheduled(every = "1s")
    public void tickGames() {
        funWithFlagsGame.tick();
    }

    public void onPlayerRegistered(@Observes PlayerRegistered event) {
        var waitingPlayers = lobby.waitingPlayers();
        for (var player : waitingPlayers) {
            var html = Templates.lobby(player, waitingPlayers).render();
            player.session().getAsyncRemote().sendObject(html);
        }
    }

    public void onLobbyFilled(@Observes LobbyFilled lobbyFilled) {
        var players = lobbyFilled.playersInLobby();
        for (Player player : players) {
            var html = Templates.lobby(player, players).render();
            player.session().getAsyncRemote().sendObject(html);
        }
    }

    public void onGameStarted(@Observes GameStarted event) {
        var game = event.game();
        var players = game.players();
        var turn = game.currentTurn().get();
        for (Player player : players) {
            var html = Templates.game(player, game, turn).render();
            player.session().getAsyncRemote().sendObject(html);
        }
    }

    public void onGameFinished(@Observes GameFinished event) {
        var game = event.game();
        var players = game.players();
        for (Player player : players) {
            var html = Templates.gameover(player, game).render();
            Log.infof("Sending %s to %s", html, player.name());
            player.session().getAsyncRemote().sendObject(html);
        }
        funWithFlagsGame.destroyGame(game);
    }

    public void onTurnStarted(@Observes TurnStarted event) {
        var game = event.game();
        var turn = event.turn();
        var countryToGuess = turn.countryToGuess();
        for (Player player : game.players()) {
            var html = Templates.game$turnPartial(player, game, turn, countryToGuess).render() + "\n"
                    + Templates.game$rankingPartial(player, game, turn).render();
            player.session().getAsyncRemote().sendObject(html);
        }
    }

    public void onTurnClockTicked(@Observes TurnClockTicked turnClockTicked) {
        var game = turnClockTicked.game();
        for (var player : game.players()) {
            var html = Templates.game$countdownPartial(player, game, turnClockTicked.turn()).render();
            player.session().getAsyncRemote().sendObject(html);
        }
    }

    public void onTurnGuessRecorded(@Observes TurnGuessRecorded event) {
        var game = event.game();
        var player = event.player();
        var session = sessions.get(player.name());
        var guess = event.guess();
        var html = Templates.submissionPartial(guess.guessedCountry()).render();
        session.getAsyncRemote().sendObject(html);
        for (Score score : game.scores()) {
            Log.infof("Score for player %s: %d", score.player().name(), score.score());
        }
        // todo notify other players that this player has submitted a guess
    }

    public void onTurnFinished(@Observes TurnFinished event) {
        var game = event.game();
        var turn = event.turn();

        Log.infof("Turn %d finished for game %s", turn.turnNumber(), game.players().stream().map(Player::name).toList());
        for(var receiver : game.players()) {
            var guess = turn.guesses().stream().filter(g -> g.player().equals(receiver)).findFirst();
            var html = Templates.turnover(receiver, game, turn, guess.orElse(null)).render();
            receiver.session().getAsyncRemote().sendObject(html);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("player") String encodedPlayer) {
        var player = URLDecoder.decode(encodedPlayer, StandardCharsets.UTF_8);
        Log.infof("User %s left", player);
        sessions.remove(player);
        funWithFlagsGame.unregisterPlayer(session);
    }
}
