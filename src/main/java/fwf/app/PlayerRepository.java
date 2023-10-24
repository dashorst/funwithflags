package fwf.app;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.websocket.Session;

@ApplicationScoped
public class PlayerRepository {
    @Inject
    Event<PlayerRegistered> playerRegistered;

    @Inject
    Event<PlayerUnregistered> playerUnregistered;

    @Inject
    Instance<Player> playerFactory;

    private ConcurrentHashMap<Session, Player> sessionsToPlayer = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Player> playernameToPlayer = new ConcurrentHashMap<>();

    public void registerPlayer(Session session, String name) {
        var player = playerFactory.get();
        player.init(session, name);

        var oldPlayer = sessionsToPlayer.put(session, player);
        if(oldPlayer != null) {
            playernameToPlayer.remove(oldPlayer.name());
            playerUnregistered.fire(new PlayerUnregistered(oldPlayer));
        }
        playernameToPlayer.put(player.name(), player);
        playerRegistered.fire(new PlayerRegistered(player));
    }

    public void unregisterPlayer(Session session) {
        var player = sessionsToPlayer.remove(session);
        if (player != null) {
            playernameToPlayer.remove(player.name());
            playerUnregistered.fire(new PlayerUnregistered(player));
            playerFactory.destroy(player);
        }
    }

    public Optional<Player> bySession(Session session) {
        return Optional.ofNullable(sessionsToPlayer.get(session));
    }

    public Optional<Player> byPlayername(String playername) {
        return Optional.ofNullable(playernameToPlayer.get(playername));
    }
}
