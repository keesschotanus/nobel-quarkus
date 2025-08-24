package com.schotanus.nobel.service;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link CountryService}.
 */
@QuarkusTest
class CountryServiceTests {

    private final CountryService service;

    CountryServiceTests(CountryService service) {
        this.service = service;

    }

    @Test
    void gettingPrimaryKeyOfExistingCountryShouldPass() {
        assertNotNull(service.getPrimaryKeyOfCountry("NL"));
    }

    @Test()
    void gettingPrimaryKeyOfNonExistingCountryShouldFail() {
        assertThrows(NotFoundException.class, () -> service.getPrimaryKeyOfCountry("QQ"));
    }
}
