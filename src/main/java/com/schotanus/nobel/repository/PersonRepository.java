package com.schotanus.nobel.repository;

import static com.schotanus.nobel.Tables.COUNTRY;
import static com.schotanus.nobel.tables.Person.PERSON;
import static org.jooq.impl.DSL.trueCondition;
import static org.jooq.impl.DSL.upper;

import com.schotanus.nobel.model.Person;
import com.schotanus.nobel.service.CountryService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityExistsException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.exception.IntegrityConstraintViolationException;

import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.List;


/**
 * This repository is responsible for maintaining and selecting persons.
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
    @Nonnull
    public Integer createPerson(@Nonnull final Person person) {
        Integer countryId = countryService.getPrimaryKeyOfCountry(person.getBirthCountryCode());

        try {
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
            .fetch().getFirst().value1();
        } catch (IntegrityConstraintViolationException exception) {
            throw new EntityExistsException("This person already exists");
        }
    }

    /**
     * Gets a person by its unique person identifier.
     * @param personIdentifier Person identifier.
     * @return The person with the supplied identifier, or null when not found.
     */
    @Nullable
    public Person getPerson(@Nonnull String personIdentifier) {
        return dsl.select(personFields)
            .from(PERSON)
            .join(COUNTRY).on(COUNTRY.ID.eq(PERSON.BIRTHCOUNTRYID))
            .where(PERSON.PERSONIDENTIFIER.eq(personIdentifier))
            .fetchOneInto(Person.class);
    }

    /**
     * Gets the primary key of a person by its unique person identifier.
     * @param personIdentifier Person identifier.
     * @return The primary key of the person, or null when not found.
     */
    @Nullable
    public Integer getPrimaryKey(@Nonnull String personIdentifier) {
        return dsl.select(PERSON.ID)
            .from(PERSON)
            .where(PERSON.PERSONIDENTIFIER.eq(personIdentifier))
            .fetchOneInto(Integer.class);
    }

    /**
     * Gets all persons matching the supplied selection criteria.
     *
     * @param name Name (or first part of the name) of the person.
     * @param countryCode Country where the person was born.
     * @param yearOfBirth Year the person was born.
     * @param yearOfDeath Year the person died or NULL for living persons.
     * @return All Persons matching the supplied selection criteria.
     */
    @Nonnull
    public List<Person> getPersons(
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

    /**
     * Updates an existing person.
     * @param person The person to update.
     * @return True when the person was updated.
     */
    public boolean updatePerson(@Nonnull Person person) {
        int records = dsl.update(PERSON)
            .set(PERSON.NAME, person.getName())
            .set(PERSON.DISPLAYNAME, person.getDisplayName())
            .set(PERSON.DESCRIPTION, person.getDescription())
            .set(PERSON.BIRTHDATE, person.getBirthDate())
            .set(PERSON.BIRTHCOUNTRYID,
                dsl.select(COUNTRY.ID).from(COUNTRY).where(COUNTRY.CODE.eq(person.getBirthCountryCode())))
            .set(PERSON.DEATHDATE, person.getDeathDate())
            .set(PERSON.URL, person.getUrl())
            .set(PERSON.LASTMODIFIEDBYID, 1)
            .set(PERSON.LASTMODIFIEDAT, OffsetDateTime.now())
            .where(PERSON.PERSONIDENTIFIER.eq(person.getPersonIdentifier()))
            .execute();

        return records == 1;
    }

}
