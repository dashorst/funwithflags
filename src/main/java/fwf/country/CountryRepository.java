package fwf.country;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
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
            if (is == null)
                throw new IllegalStateException("File not packaged: /countries.json");

            var parser = Json.createParser(is);
            if (parser.hasNext()) {
                parser.next();
                return parser.getArrayStream()
                        .map(jo -> jo.asJsonObject())
                        .map(j -> new Country(j.getString("capital", ""), j.getString("code"),
                                j.getString("continent", ""),
                                j.getString("flag_1x1"), j.getString("flag_4x3"), j.getBoolean("iso"),
                                j.getString("name"),
                                Normalizer.normalize(j.getString("name"), Form.NFKD).replaceAll("\\p{M}", "")
                                        .toLowerCase()))
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
        if (search == null || search.isBlank())
            return Collections.emptyList();
        if (!Normalizer.isNormalized(search, Form.NFKD))
            search = Normalizer.normalize(search, Form.NFKD);

        var searchString = search;
        return countries().stream()
                .filter(c -> c.normalizedName().contains(searchString.toLowerCase()))
                .limit(maxResults).sorted().toList();

    }

    public Optional<Country> findCountryByCode(String code) {
        return countries.stream().filter(c -> c.code().equals(code)).findFirst();
    }

    public Optional<Country> guess(String countryName) {
        var countries = findCountries(countryName, 2);

        var country = countries.stream()
                .filter(c -> countryName.equalsIgnoreCase(c.name())).findFirst()
                .or(() -> countries.stream().findFirst());

        return country;
    }
}
