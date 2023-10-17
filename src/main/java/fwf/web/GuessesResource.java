package fwf.web;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/guess/{playername}")
public class GuessesResource {
    @POST
    public Response guess(@PathParam("playername") String playername, @FormParam("country") String country) {
        return Response.ok().build();
    }
}
