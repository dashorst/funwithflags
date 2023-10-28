package fwf.admin;

import fwf.config.Configuration;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("")
public class AdminResource {
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index(Configuration configuration);
    }

    @Inject
    Configuration configuration;

    @GET
    @Path("/admin")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return Templates.index(configuration);
    }
}
