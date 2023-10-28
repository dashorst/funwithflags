package fwf;

import fwf.country.Country;
import fwf.game.Game;
import fwf.player.Player;
import io.quarkus.qute.TemplateGlobal;
import jakarta.websocket.Session;

@TemplateGlobal
public interface FunWithFlagsGame {
    public void registerPlayer(Session session, String name);

    public void unregisterPlayer(Session session);

    public void guess(Player player, int turnNumber, Country country);

    public void destroyGame(Game game);

    public void tick();
}
