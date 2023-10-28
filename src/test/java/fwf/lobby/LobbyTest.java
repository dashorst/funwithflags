package fwf.lobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fwf.FunWithFlagsGame;
import fwf.app.Application;
import fwf.app.MockSession;
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

        Session session2 = new MockSession();
        application.registerPlayer(session2, "Player 2");
        Player player2 = playerRepository.bySession(session2).get();

        lobby.checkLobbyFilled();
        assertNull(lobbyFilledEvent);

        Session session3 = new MockSession();
        application.registerPlayer(session3, "Player 3");
        Player player3 = playerRepository.bySession(session3).get();
        lobby.checkLobbyFilled();

        assertNotNull(lobbyFilledEvent);
        assertEquals(FunWithFlagsGame.PLAYERS_PER_GAME, lobbyFilledEvent.playersInLobby().size());
        assertEquals(player2.name(), lobbyFilledEvent.playersInLobby().get(0).name());
        assertEquals(player3.name(), lobbyFilledEvent.playersInLobby().get(1).name());
    }

    @Test
    public void fillingLobbyFiresLobbyFilledEvent() {
        for (int i = 1; i <= FunWithFlagsGame.PLAYERS_PER_GAME; i++) {
            application.registerPlayer(new MockSession(), "Player " + i);
        }
        lobby.checkLobbyFilled();
        assertNotNull(lobbyFilledEvent);
        assertEquals(FunWithFlagsGame.PLAYERS_PER_GAME, lobbyFilledEvent.playersInLobby().size());
        assertEquals("Player 1", lobbyFilledEvent.playersInLobby().get(0).name());
    }
}
