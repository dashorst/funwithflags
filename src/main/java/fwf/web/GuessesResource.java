package fwf.web;

import fwf.FunWithFlagsGame;
import fwf.app.CountryRepository;
import fwf.app.PlayerRepository;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("")
public class GuessesResource {
    @Inject
    CountryRepository countryRepository;

    @Inject
    PlayerRepository playerRepository;

    @Inject
    FunWithFlagsGame funWithFlagsGame;

    @POST
    @Path("/guess/{playername}/{turn}")
    public Response guess(@PathParam("playername") String playername, @PathParam("turn") int turn,
            @FormParam("search") String countryName) {
        var player = playerRepository.byPlayername(playername);
        if (player.isEmpty()) {
            Log.infof("Player %s not found", playername);
            return Response.ok().build();
        }

        var country = countryRepository.guess(countryName);

        if (country.isEmpty()) {
            Log.infof("Country %s not found", countryName);
            return Response.ok().build();
        }

        funWithFlagsGame.guess(player.get(), turn, country.get());
        return Response.ok().build();
    }
}
