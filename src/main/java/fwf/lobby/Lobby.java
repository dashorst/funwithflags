package fwf.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import fwf.config.Configuration;
import fwf.player.Player;
import fwf.player.PlayerRegistered;
import fwf.player.PlayerUnregistered;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class Lobby {
    @Inject
    Event<LobbyFilled> lobbyFilledEvent;

    @Inject
    Configuration configuration;

    private BlockingQueue<Player> waitingPlayers = new LinkedBlockingDeque<>();

    public List<Player> waitingPlayers() {
        return new ArrayList<>(waitingPlayers);
    }

    void clear() {
        waitingPlayers.clear();
    }

    void registerPlayer(@Observes PlayerRegistered playerRegistered) {
        Log.infof("Player '%s' registered in lobby", playerRegistered.player().name());
        waitingPlayers.offer(playerRegistered.player());
    }

    void unregisterPlayer(@Observes PlayerUnregistered playerUnregistered) {
        Log.infof("Player '%s' unregistered in lobby", playerUnregistered.player().name());
        waitingPlayers.remove(playerUnregistered.player());
    }

    @Scheduled(every = "1s")
    void checkLobbyFilled() {
        int numberOfPlayersPerGame = configuration.numberOfPlayersPerGame();
        if (waitingPlayers.size() < numberOfPlayersPerGame)
            return;

        var gamePlayers = new ArrayList<Player>();
        int added = waitingPlayers.drainTo(gamePlayers, numberOfPlayersPerGame);
        if (added < numberOfPlayersPerGame) {
            // re-add the picked players to the waiting list
            waitingPlayers.addAll(gamePlayers);
            return;
        }
        lobbyFilledEvent.fire(new LobbyFilled(gamePlayers));
    }
}
