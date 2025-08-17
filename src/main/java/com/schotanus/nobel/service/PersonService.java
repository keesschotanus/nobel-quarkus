package com.schotanus.nobel.service;

import com.schotanus.nobel.model.Person;
import com.schotanus.nobel.repository.PersonRepository;
import io.quarkus.logging.Log;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.List;


/**
 * This service is responsible for maintaining and selecting persons.
 * The Persons that are maintained are normally Nobel Prize laureates, like scientists and writers.
 */
@ApplicationScoped
public class PersonService extends AbstractService {

    private final PersonRepository repository;

    PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a person in the database.
     *
     * @param person Model to create the person from.
     * @return URL to access the created person.
     */
    @Nonnull
    public String createPerson(@Nonnull final Person person) {
        Integer id = repository.createPerson(person);
        Log.info("Person created with id:" + id);
        return getBaseUrl() + "persons/" + person.getPersonIdentifier();
    }

    /**
     * Gets a person by its unique person identifier.
     *
     * @param personIdentifier Person identifier.
     * @return The Person with the supplied identifier.
     * @throws NotFoundException when no person with the supplied identifier exists.
     */
    @Nonnull
    public Person getPerson(@Nonnull final String personIdentifier) {
        Person person = repository.getPerson(personIdentifier);
        if (person == null) {
            throw new NotFoundException("Person wih identifier: " + personIdentifier + ", not found");
        }

        return person;
    }

    /**
     * Gets the primary key of a person by its unique person identifier.
     *
     * @param personIdentifier Person identifier.
     * @return The primary key of the person.
     * @throws NotFoundException when no person with the supplied identifier exists.
     */
    @Nonnull
    public Integer getPrimaryKey(@Nonnull final String personIdentifier) {
        final Integer primaryKey = repository.getPrimaryKey(personIdentifier);
        if (primaryKey == null) {
            throw new NotFoundException("Person wih identifier: " + personIdentifier + ", not found");
        }

        return primaryKey;
    }

    /**
     * Gets all persons matching the supplied selection criteria.
     *
     * @param name Name (or first part of the name) of the person.
     * @param countryCode Country where the person was born.
     * @param yearOfBirth Year the person was born.
     * @param yearOfDeath Year the person died or NULL for living persons.
     * @return All persons matching the supplied selection criteria.
     */
    @Nonnull
    public List<Person> getPersons(
            @Nullable String name,
            @Nullable String countryCode,
            @Nullable Integer yearOfBirth,
            @Nullable Integer yearOfDeath) {
        return repository.getPersons(name, countryCode, yearOfBirth, yearOfDeath);
    }

}
