package com.schotanus.nobel.repository;

import com.schotanus.nobel.model.Person;
import com.schotanus.nobel.service.CountryService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static com.schotanus.nobel.Tables.COUNTRY;
import static com.schotanus.nobel.tables.Person.PERSON;
import static org.jooq.impl.DSL.trueCondition;
import static org.jooq.impl.DSL.upper;


/**
 * This PersonRepository is responsible for maintaining and selecting Persons.
 */
@ApplicationScoped
public class PersonRepository {

    private final DSLContext dsl;
    private final CountryService countryService;

    private final List<Field<?>> personFields = List.of(
            PERSON.PERSONIDENTIFIER,
            PERSON.NAME,
            PERSON.DISPLAYNAME,
            PERSON.DESCRIPTION,
            PERSON.URL,
            COUNTRY.CODE.as("birthCountryCode"),
            PERSON.BIRTHDATE,
            PERSON.DEATHDATE,
            PERSON.CREATEDAT,
            PERSON.LASTMODIFIEDAT);

    PersonRepository(DSLContext dsl, CountryService countryService) {
        this.dsl = dsl;
        this.countryService = countryService;
    }

    /**
     * Create a person.
     * @param person Model to create the person from.
     * @return The primary key of the created person.
     */
    public @Nonnull Integer createPerson(@Nonnull final Person person) {
        Integer countryId = countryService.getPrimaryKeyOfCountry(person.getBirthCountryCode());

        return dsl.insertInto(PERSON).columns(
                        PERSON.PERSONIDENTIFIER,
                        PERSON.NAME,
                        PERSON.DISPLAYNAME,
                        PERSON.DESCRIPTION,
                        PERSON.BIRTHDATE,
                        PERSON.BIRTHCOUNTRYID,
                        PERSON.DEATHDATE,
                        PERSON.URL,
                        PERSON.CREATEDBYID,
                        PERSON.LASTMODIFIEDBYID)
                .values(
                        person.getPersonIdentifier(),
                        person.getName(),
                        person.getDisplayName(),
                        person.getDescription(),
                        person.getBirthDate(),
                        countryId,
                        person.getDeathDate(),
                        person.getUrl(),
                        1,
                        1)
                .returningResult(PERSON.ID)
                .fetchSingleInto(Integer.class);
    }

    /**
     * Gets a person by its unique person identifier.
     * @param personIdentifier Person identifier.
     * @return The Person with the supplied identifier, or null when not found.
     */
    public @Nullable Person getPerson(@Nonnull String personIdentifier) {
        return dsl.select(personFields)
                .from(PERSON)
                .join(COUNTRY).on(COUNTRY.ID.eq(PERSON.BIRTHCOUNTRYID))
                .where(PERSON.PERSONIDENTIFIER.eq(personIdentifier))
                .fetchOneInto(Person.class);
    }

    /**
     * @param name        Name (or first part of the name) of the person.
     * @param countryCode Country where the person was born.
     * @param yearOfBirth Year the person was born.
     * @param yearOfDeath Year the person died or NULL for living persons.
     * @return All Persons matching the supplied selection criteria.
     */
    public @Nonnull List<Person> getPersons(
            @Nullable String name,
            @Nullable String countryCode,
            @Nullable Integer yearOfBirth,
            @Nullable Integer yearOfDeath) {
        Condition condition = trueCondition();
        if (name != null && !name.isBlank()) {
            condition = condition.and(upper(PERSON.DISPLAYNAME).like(name.toUpperCase() + "%"));
        }
        if (countryCode != null && !countryCode.isBlank()) {
            condition = condition.and(upper(COUNTRY.CODE).eq(countryCode.toUpperCase()));
        }
        if (yearOfBirth != null) {
            condition = condition.and(PERSON.BIRTHDATE.between(
                LocalDate.of(yearOfBirth, Month.JANUARY, 1), LocalDate.of(yearOfBirth, Month.DECEMBER, 31)));
        }
        if (yearOfDeath != null) {
            condition = condition.and(PERSON.DEATHDATE.between(
                LocalDate.of(yearOfDeath, Month.JANUARY, 1), LocalDate.of(yearOfDeath, Month.DECEMBER, 31)));
        }
        return dsl.select(personFields)
            .from(PERSON)
            .join(COUNTRY).on(COUNTRY.ID.eq(PERSON.BIRTHCOUNTRYID))
            .where(condition)
            .orderBy(PERSON.DISPLAYNAME)
            .fetchInto(Person.class);
    }

}
