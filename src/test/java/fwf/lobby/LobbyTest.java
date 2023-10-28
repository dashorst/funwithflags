package fwf.lobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fwf.app.Application;
import fwf.app.MockSession;
import fwf.config.Configuration;
import fwf.player.Player;
import fwf.player.PlayerRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.websocket.Session;

@QuarkusTest
public class LobbyTest {
    @Inject
    Application application;

    @Inject
    PlayerRepository playerRepository;

    @Inject
    Configuration configuration;

    @Inject
    Lobby lobby;

    static LobbyFilled lobbyFilledEvent;

    void lobbyFilledObserver(@Observes LobbyFilled lobbyFilled) {
        lobbyFilledEvent = lobbyFilled;
    }

    @BeforeEach
    public void reset() {
        lobby.clear();
        lobbyFilledEvent = null;
    }

    @Test
    public void emptyLobbyDoesntFireLobbyFilledEvent() {
        lobby.checkLobbyFilled();
        assertNull(lobbyFilledEvent);
    }

    @Test
    public void registeringUnregisteringPlayersDoesntFireLobbyFilledEvent() {
        lobby.checkLobbyFilled();
        assertNull(lobbyFilledEvent);

        Session session1 = new MockSession();
        application.registerPlayer(session1, "Player 1");
        Player player1 = playerRepository.bySession(session1).get();

        lobby.checkLobbyFilled();
        assertNull(lobbyFilledEvent);

        application.unregisterPlayer(player1.session());

        List<Session> sessions = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= configuration.numberOfPlayersPerGame(); i++) {
            Session session = new MockSession();
            application.registerPlayer(session, "Player " + i);
            sessions.add(session);
            players.add(playerRepository.bySession(session).get());
        }
        lobby.checkLobbyFilled();

        assertNotNull(lobbyFilledEvent, "Lobby should be filled");
        assertEquals(configuration.numberOfPlayersPerGame(), lobbyFilledEvent.playersInLobby().size());
        for (Player player : players) {
            assertTrue(lobbyFilledEvent.playersInLobby().stream().map(Player::name).anyMatch(player.name()::equals),
                    player.name());
        }
    }

    @Test
    public void fillingLobbyFiresLobbyFilledEvent() {
        for (int i = 1; i <= configuration.numberOfPlayersPerGame(); i++) {
            application.registerPlayer(new MockSession(), "Player " + i);
        }
        lobby.checkLobbyFilled();
        assertNotNull(lobbyFilledEvent);
        assertEquals(configuration.numberOfPlayersPerGame(), lobbyFilledEvent.playersInLobby().size());
        assertEquals("Player 1", lobbyFilledEvent.playersInLobby().get(0).name());
    }
}
