package fwf.web;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fwf.ApplicationStatus;
import fwf.FunWithFlagsGame;
import fwf.app.Country;
import fwf.app.Game;
import fwf.app.GameFinished;
import fwf.app.GameStarted;
import fwf.app.LobbyFilled;
import fwf.app.Player;
import fwf.app.Turn;
import fwf.app.TurnClockTicked;
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

@ServerEndpoint("/game/{player}")
@ApplicationScoped
public class FunWithFlagsWebSocket {
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance activeGamesPartial(int nrOfGames);

        public static native TemplateInstance secondsLeftPartial(int secondsLeft);

        public static native TemplateInstance lobby(Player receiver, List<Player> players);

        public static native TemplateInstance game(Player receiver, Game game, Turn turn);

        public static native TemplateInstance turn(Player receiver, Game game, Turn turn, Country countryToGuess);

        public static native TemplateInstance gameover(Player receiver, Game game);
    }

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    FunWithFlagsGame funWithFlagsGame;

    @Inject
    ApplicationStatus applicationStatus;

    @OnOpen
    public void onOpen(Session session, @PathParam("player") String player) {
        Log.infof("User %s joined", player);
        sessions.put(player, session);
        funWithFlagsGame.registerPlayer(session, player);
    }

    @OnError
    public void onError(Session session, @PathParam("player") String player, Throwable throwable) {
        sessions.remove(player);
        Log.infof("User %s left on error: ", player, throwable);
    }

    @Scheduled(every = "5s")
    public void updateActiveGames() {
        var html = Templates.activeGamesPartial(applicationStatus.numberOfGames()).render();
        for (Session session : sessions.values()) {
            session.getAsyncRemote().sendObject(html);
        }
    }

    @Scheduled(every = "1s")
    public void tickGames() {
        funWithFlagsGame.tick();
    }

    public void onTurnClockTicked(@Observes TurnClockTicked turnClockTicked) {
        var game = turnClockTicked.game();
        var secondsLeft = turnClockTicked.secondsLeft();
        var html = Templates.secondsLeftPartial(secondsLeft).render();
        for(var player : game.players()) {
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
        for (Player player : players) {
            var html = Templates.game(player, game, game.currentTurn()).render()
                    + Templates.activeGamesPartial(applicationStatus.numberOfGames()).render();
            player.session().getAsyncRemote().sendObject(html);
        }
    }

    public void onGameFinished(@Observes GameFinished event) {
        var game = event.game();
        var players = game.players();
        for (Player player : players) {
            var html = Templates.gameover(player, game).render();
            player.session().getAsyncRemote().sendObject(html);
        }
    }

    public void onTurnStarted(@Observes TurnStarted event) {
        var game = event.game();
        var turn = event.turn();
        var countryToGuess = turn.countryToGuess();
        for(Player player : game.players()) {
            var html = Templates.turn(player, game, turn, countryToGuess).render();
            player.session().getAsyncRemote().sendObject(html);
        }
    }
    @OnClose
    public void onClose(Session session, @PathParam("player") String player) {
        Log.infof("User %s left", player);
        sessions.remove(player);
        funWithFlagsGame.unregisterPlayer(session);
    }
}
