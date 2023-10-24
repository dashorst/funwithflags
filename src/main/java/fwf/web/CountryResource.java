package fwf.web;

import java.util.List;

import fwf.app.Country;
import fwf.app.CountryRepository;
import io.quarkus.logging.Log;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
public class CountryResource {
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance results(List<Country> countries);
    }

    @Inject
    CountryRepository countryRepository;

    @POST
    @Path("/country/search")
    public TemplateInstance search(@FormParam("search") String text) {
        Log.infof("Search: %s", text);
        var result = countryRepository.findCountries(text, 15);
        return Templates.results(result);
    }
}
