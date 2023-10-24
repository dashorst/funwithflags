package fwf.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
