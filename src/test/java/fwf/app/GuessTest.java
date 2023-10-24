package fwf.app;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class GuessTest {
    @Inject
    CountryRepository countryRepository;

    @Test
    public void norway() {
        var norway = countryRepository.findCountries("norway", 2);
        norway.forEach(System.out::println);
    }
}
