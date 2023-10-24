package fwf;

import fwf.app.Country;
import fwf.app.Game;
import fwf.app.Player;
import io.quarkus.qute.TemplateGlobal;
import jakarta.websocket.Session;

@TemplateGlobal
public interface FunWithFlagsGame {
    @TemplateGlobal(name = "PLAYERS_PER_GAME")
    public static final int PLAYERS_PER_GAME = 2;

    @TemplateGlobal(name = "NUMBER_OF_TURNS_PER_GAME")
    public static final int NUMBER_OF_TURNS_PER_GAME = 4;

    @TemplateGlobal(name = "SECONDS_PER_TURN")
    public static final int SECONDS_PER_TURN = 20;

    @TemplateGlobal(name = "SECONDS_PER_RESULT")
    public static final int SECONDS_PER_RESULT = 5;

    public void registerPlayer(Session session, String name);

    public void unregisterPlayer(Session session);

    public void guess(Player player, int turnNumber, Country country);

    public void destroyGame(Game game);

    public void tick();
}
