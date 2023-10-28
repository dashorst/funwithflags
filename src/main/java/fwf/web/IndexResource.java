package fwf.web;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import fwf.ApplicationStatus;
import fwf.FunWithFlagsGame;
import fwf.config.Configuration;
import fwf.lobby.Lobby;
import fwf.player.Player;
import io.quarkus.logging.Log;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("")
@Produces(MediaType.TEXT_HTML)
public class IndexResource {
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index(int nrOfPlayersPerGame, int nrOfGames);
        public static native TemplateInstance joined(String playername, List<Player> players);
    }

    @Inject
    FunWithFlagsGame funWithFlagsGame;

    @Inject
    ApplicationStatus applicationStatus;

    @Inject
    Lobby lobby;

    @Inject
    Configuration configuration;
    
    @GET
    @Path("/")
    public TemplateInstance get() {
        return Templates.index(configuration.numberOfPlayersPerGame(), applicationStatus.numberOfGames());
    }

    @POST
    @Path("/join")
    public TemplateInstance join(@FormParam("playername") String playername) {
        var name = URLDecoder.decode(playername, StandardCharsets.UTF_8);
        Log.infof("Player %s tries to join", name);
        return Templates.joined(name, lobby.waitingPlayers());
    }
}
