package fwf;

import fwf.app.Country;
import fwf.app.Game;
import fwf.app.Player;
import jakarta.websocket.Session;

public interface FunWithFlagsGame {
    public static final int PLAYERS_PER_GAME = 2;
    public static final int NUMBER_OF_TURNS_PER_GAME = 4;
    public static final int SECONDS_PER_TURN = 20;

    public Player registerPlayer(Session session, String name);

    public Player unregisterPlayer(Session session);

    public void guess(Session session, int turnNumber, Country country);

    public void destroyGame(Game game);

    public void tick();
}
