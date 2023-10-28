package fwf.country;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class CountriesRepositoryTest {
    @Inject
    CountryRepository countryRepository;

    @Test
    public void netherlands() {
        var found = countryRepository.findCountryByCode("nl");
        assertEquals("Netherlands", found.get().name());
    }

    @Test
    public void germany() {
        var found = countryRepository.findCountryByCode("de");
        assertEquals("Germany", found.get().name());
    }

    @Test
    public void search_ther() {
        var found = countryRepository.findCountries("ther", 5);
        assertEquals(4, found.size());
        var first = found.get(0);
        assertEquals("French Southern Territories", first.name());
    }

    @Test
    public void guessExactMatch() {
        var found = countryRepository.guess("sudan");
        assertEquals("Sudan", found.get().name());
    }

    @Test
    public void guessExactMatch2() {
        var found = countryRepository.guess("guyana");
        assertEquals("Guyana", found.get().name());
    }

    @Test
    public void guessAccented() {
        var found = countryRepository.guess("turkiye");
        assertEquals("Türkiye", found.get().name());
    }

    @Test
    public void guessAccented2() {
        var found = countryRepository.findCountries("tur", 5);
        assertEquals(Arrays.asList("tc", "tm", "tr"), found.stream().map(Country::code).sorted().toList());
    }

    @Test
    public void guessAccented3() {
        var found = countryRepository.findCountries("cot", 15);
        assertEquals(Arrays.asList("ci", "gb-sct"), found.stream().map(Country::code).sorted().toList());

        var found2 = countryRepository.findCountries("ivo", 5);
        assertEquals(Arrays.asList("ci"), found2.stream().map(Country::code).sorted().toList());

        var found3 = countryRepository.findCountries("co", 15);
        assertEquals(Arrays.asList("cc", "cd", "cg", "ci", "ck", "co", "cr", "eac", "es-pv", "gb-sct", "km", "ma", "mc",
                "mx", "pr"), found3.stream().map(Country::code).sorted().toList());
    }
}
