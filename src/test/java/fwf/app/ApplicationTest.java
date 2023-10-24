package fwf.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.websocket.Session;

@QuarkusTest
public class ApplicationTest {
    @Inject
    Application application;

    static PlayerRegistered playerRegisteredEvent;
    static PlayerUnregistered playerUnregisteredEvent;

    static CountDownLatch gamesStarted;

    void observeRegisterPlayer(@Observes PlayerRegistered playerRegistered) {
        playerRegisteredEvent = playerRegistered;
    }

    void observeUnregisterPlayer(@Observes PlayerUnregistered playerUnregistered) {
        playerUnregisteredEvent = playerUnregistered;
    }

    @BeforeEach
    public void reset() {
        playerRegisteredEvent = null;
        playerUnregisteredEvent = null;
    }

    @Test
    public void registeringPlayerFiresEvent() {
        Session session = new MockSession();
        application.registerPlayer(session, "Martijn");

        assertNotNull(playerRegisteredEvent);
        assertEquals("Martijn", playerRegisteredEvent.player().name());
    }

    @Test
    public void unregisteringUnregisteredPlayerDoesNotFireEvent() {
        Session session = new MockSession();
        application.unregisterPlayer(session);

        assertNull(playerRegisteredEvent);
    }

    @Test
    public void unregisteringRegisteredPlayerFiresEvent() {
        Session session = new MockSession();
        application.registerPlayer(session, "Martijn");

        assertNotNull(playerRegisteredEvent);

        application.unregisterPlayer(session);
        assertNotNull(playerUnregisteredEvent);
        assertEquals("Martijn", playerUnregisteredEvent.player().name());
    }

    @Test
    public void registeringAlreadyRegisteredPlayerDoesntFireEvent() {
        Session session = new MockSession();
        application.registerPlayer(session, "Martijn");

        assertNotNull(playerRegisteredEvent);

        playerRegisteredEvent = null;
        playerUnregisteredEvent = null;

        application.registerPlayer(session, "Pieter");

        assertNotNull(playerRegisteredEvent);
        assertEquals("Pieter", playerRegisteredEvent.player().name());
        
        assertNotNull(playerUnregisteredEvent);
        assertEquals("Martijn", playerUnregisteredEvent.player().name());
    }

    public void onGameStarted(@Observes GameStarted event) {
        if(gamesStarted != null)
            gamesStarted.countDown();
    }

    @Test
    public void destroyTest() throws InterruptedException {
        gamesStarted = new CountDownLatch(1);

        application.registerPlayer(new MockSession(), "Martijn");
        application.registerPlayer(new MockSession(), "Pieter");

        gamesStarted.await(5, TimeUnit.SECONDS);

        assertEquals(1, application.numberOfGames());

        Game game = application.games().element();
        application.destroyGame(game);

        assertEquals(0, application.numberOfGames());
    }
}
