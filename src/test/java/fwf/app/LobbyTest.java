package fwf.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fwf.FunWithFlagsGame;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@QuarkusTest
public class LobbyTest {
    @Inject
    Application application;

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

        Player player1 = application.registerPlayer(new MockSession(), "Player 1");

        lobby.checkLobbyFilled();
        assertNull(lobbyFilledEvent);

        application.unregisterPlayer(player1.session());

        Player player2 = application.registerPlayer(new MockSession(), "Player 2");

        lobby.checkLobbyFilled();
        assertNull(lobbyFilledEvent);

        Player player3 = application.registerPlayer(new MockSession(), "Player 3");
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
