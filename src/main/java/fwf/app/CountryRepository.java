package fwf.app;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;

@ApplicationScoped
public class CountryRepository {
    private final List<Country> countries;

    public CountryRepository() {
        countries = readCountries();
    }

    private List<Country> readCountries() {
        try (var is = getClass().getResourceAsStream("/countries.json");) {
            var parser = Json.createParser(is);
            if (parser.hasNext()) {
                parser.next();
                return parser.getArrayStream()
                        .map(jo -> jo.asJsonObject())
                        .map(j -> new Country(j.getString("capital", ""), j.getString("code"),
                                j.getString("continent", ""),
                                j.getString("flag_1x1"), j.getString("flag_4x3"), j.getBoolean("iso"),
                                j.getString("name")))
                        .toList();
            }
            throw new IllegalStateException("No countries found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Country> countries() {
        return countries;
    }

    public List<Country> findCountries(String search, int maxResults) {
        if(search == null || search.isBlank()) 
            return Collections.emptyList();
        return countries().stream()
                .filter(c -> c.name().toLowerCase().contains(search.toLowerCase()))
                .limit(15).sorted().toList();
    }

    public Optional<Country> findCountryByCode(String code) {
        return countries.stream().filter(c -> c.code().equals(code)).findFirst();
    }
}
