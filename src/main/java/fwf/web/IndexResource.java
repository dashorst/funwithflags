package fwf.web;

import fwf.ApplicationStatus;
import fwf.FunWithFlagsGame;
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
        public static native TemplateInstance joined(String playername);
    }

    @Inject
    FunWithFlagsGame funWithFlagsGame;

    @Inject
    ApplicationStatus applicationStatus;

    @GET
    @Path("/")
    public TemplateInstance get() {
        return Templates.index(FunWithFlagsGame.PLAYERS_PER_GAME, applicationStatus.numberOfGames());
    }

    @POST
    @Path("/join")
    public TemplateInstance join(@FormParam("playername") String playername) {
        return Templates.joined(playername);
    }
}
