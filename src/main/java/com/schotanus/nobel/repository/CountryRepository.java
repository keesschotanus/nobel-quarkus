package com.schotanus.nobel.repository;

import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import org.jooq.DSLContext;

import static com.schotanus.nobel.Tables.COUNTRY;


/**
 * This CountryRepository is only responsible for selecting a single country.
 * To add or update a country, update Liquibase's changeLog.xml file.
 */
@ApplicationScoped
public class CountryRepository {

    private final DSLContext dsl;

    CountryRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Gets the primary of the country with the supplied code.
     * @param code The code (ISO-3166, alpha-2 code) of the country.
     * @return The primary key of the country with the supplied code,
     *  or null when no such country exists.
     */
    public Integer getPrimaryKeyOfCountry(@Nonnull String code) {
        return dsl.select(COUNTRY.ID)
            .from(COUNTRY)
            .where(COUNTRY.CODE.eq(code))
            .fetchOneInto(Integer.class);
    }

}
