package com.schotanus.nobel.service;

import com.schotanus.nobel.repository.CountryRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;


/**
 * This service is only responsible for selecting a single country.
 * To add or update a country, update Liquibase's changeLog.xml file.
 */
@ApplicationScoped
public class CountryService extends AbstractService {

    private final CountryRepository repository;

    CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets the primary key of the country with the supplied code.
     * @param code The code (ISO-3166, alpha-2 code) of the country.
     * @return The primary key of the country with the supplied code.
     * @throws NotFoundException When no country with the supplied code exists.
     */
    public @Nonnull Integer getPrimaryKeyOfCountry(@Nonnull final String code) {
        Integer id = repository.getPrimaryKeyOfCountry(code);

        if (id == null) {
            throw new NotFoundException("Country code: " + code + ", not found");
        }

        return id;
    }
}
