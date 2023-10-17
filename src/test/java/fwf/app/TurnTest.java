package fwf.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@QuarkusTest
public class TurnTest {
    @Inject
    Instance<Game> gameFactory;

    @Test
    public void countdownSimple() {
        Player player1 = new Player();
        player1.init(new MockSession(), "player1");

        Player player2 = new Player();
        player2.init(new MockSession(), "player2");

        Game game = gameFactory.get();
        game.init(Arrays.asList(player1, player2), 1, 2);

        Turn turn = game.currentTurn();

        assertFalse(turn.isDone());
        assertEquals(2, turn.secondsLeft());

        turn.tick();
        assertFalse(turn.isDone());
        assertEquals(1, turn.secondsLeft());

        turn.tick();
        assertTrue(turn.isDone());
        assertEquals(0, turn.secondsLeft());
    }

    @Test
    public void countdownMany() {
        int numberOfTicks = 20;

        Player player1 = new Player();
        player1.init(new MockSession(), "player1");

        Player player2 = new Player();
        player2.init(new MockSession(), "player2");

        Game game = gameFactory.get();
        game.init(Arrays.asList(player1, player2), 1, numberOfTicks);

        Turn turn = game.currentTurn();

        for (int i = 0; i < numberOfTicks; i++) {
            assertFalse(turn.isDone());
            assertEquals(numberOfTicks - i, turn.secondsLeft());
            turn.tick();
        }
        assertTrue(turn.isDone());
        assertEquals(0, turn.secondsLeft());
    }
}
