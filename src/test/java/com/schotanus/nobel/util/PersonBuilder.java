package com.schotanus.nobel.util;

import com.schotanus.nobel.model.Person;

import java.time.LocalDate;
import java.time.Period;
import java.util.Random;
import java.util.UUID;


public class PersonBuilder {
    // Required fields
    private final String personIdentifier;
    private final String name;
    private final LocalDate birthDate;
    private final String birthCountryCode;

    // Optional fields
    private String displayName;
    private String description;
    private LocalDate deathDate;
    private String url;

    /**
     * Creates random data for a person's required fields, except for the country which is always 'NL'.
     */
    public PersonBuilder() {
        UUID uuid = UUID.randomUUID();
        this.personIdentifier = "test" + uuid;
        this.name = "name" + uuid;
        this.displayName = "disp" + uuid;
        this.birthDate =  LocalDate.now().minus(Period.ofYears(25 + new Random().nextInt( 50)));
        this.birthCountryCode = "NL";
    }

    public PersonBuilder(String personIdentifier, String name, LocalDate birthDate, String birthCountryCode) {
        this.personIdentifier = personIdentifier;
        this.name = name;
        this.displayName = name;
        this.birthDate = birthDate;
        this.birthCountryCode = birthCountryCode;
    }

    public PersonBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public PersonBuilder description(String description) {
        this.description = description;
        return this;
    }

    public PersonBuilder deathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
        return this;
    }

    public PersonBuilder url(String url) {
        this.url = url;
        return this;
    }

    public Person build() {
        Person person = new Person();
        person.setPersonIdentifier(personIdentifier);
        person.setName(name);
        person.setDisplayName(displayName);
        person.setBirthDate(birthDate);
        person.setDeathDate(deathDate);
        person.setBirthCountryCode(birthCountryCode);
        person.setDescription(description);
        person.setUrl(url);

        return person;
    }

}
