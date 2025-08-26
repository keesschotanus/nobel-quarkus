package com.schotanus.nobel.api;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.schotanus.nobel.model.Person;
import com.schotanus.nobel.service.PersonService;
import com.schotanus.nobel.util.PersonBuilder;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.util.List;


/**
 * Tests {@link PersonApiImpl}.
 */
@QuarkusTest
@TestHTTPEndpoint(PersonApiImpl.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonApiImplIT {

    private final PersonService service;

    PersonApiImplIT(PersonService service) {
        this.service = service;
    }

    /**
     * Tests {@link PersonApiImpl#createPerson(Person)}.
     */
    @Test
    void createValidPersonShouldPass() {
        given()
            .contentType("application/json")
            .body(new PersonBuilder().build())
            .when()
            .post()
            .then()
            .statusCode(HttpURLConnection.HTTP_CREATED);
    }

    /**
     * Tests {@link PersonApiImpl#createPerson(Person)}.
     */
    @Test
    void createInvalidPersonShouldFail() {
        given()
            .contentType("application/json")
            .body(new Person())
            .when()
            .post()
            .then()
            .statusCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    /**
     * Tests {@link PersonApiImpl#getPerson(String)}.
     */
    @Test
    void getExistingPersonByIdShouldPass() {
        final Person person = new PersonBuilder().build();
        service.createPerson(person);

        Person foundPerson = given()
            .when()
            .pathParam("id", person.getPersonIdentifier())
            .get("{id}")
            .then()
            .statusCode(HttpURLConnection.HTTP_OK)
            .extract().as(Person.class);

        assertNotNull(foundPerson);
    }

    /**
     * Tests {@link PersonApiImpl#getPerson(String)}.
     */
    @Test
    void getNonExistingPersonByIdShouldFail() {
        given()
            .when()
            .pathParam("id", "Unknown")
            .get("{id}")
            .then()
            .statusCode(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Tests {@link PersonApiImpl#getPersons(String, String, Integer, Integer)} without using any of the selection criteria.
     */
    @Test()
    void gettingAllPersonsShouldPass() {
        final Person person = new PersonBuilder().build();
        service.createPerson(person);

        // Find all persons
        List<Person> foundPersons = given()
            .when()
            .get()
            .then()
            .statusCode(HttpURLConnection.HTTP_OK)
            .extract().as(new TypeRef<>() {});

        assertNotNull(foundPersons);

        // Check that created person is present in the response
        for (Person foundPerson : foundPersons) {
            if (foundPerson.getDisplayName().equals(person.getDisplayName())) {
                return;
            }
        }

        Assertions.fail("Did not find the previously created person");
    }

    /**
     * Tests {@link PersonApiImpl#getPersons(String, String, Integer, Integer)}, filtering on name.
     */
    @Test()
    void gettingAllPersonsByNameShouldPass() {
        final Person person = new PersonBuilder().build();
        service.createPerson(person);

        // Find the persons with the supplied name
        List<Person> foundPersons = given()
                .when()
                .queryParam("name", person.getDisplayName())
                .get()
                .then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract().as(new TypeRef<>() {});

        assertNotNull(foundPersons);
        assertEquals(1, foundPersons.size());
    }

    /**
     * Tests {@link PersonApiImpl#getPersons(String, String, Integer, Integer)}, using all parameters.
     */
    @Test()
    void gettingAllPersonsByAllQueryParametersShouldPass() {
        final Person person = new PersonBuilder()
                .deathDate(LocalDate.now())
                .url("http://somewhere.com")
                .description("Description")
                .build();
        service.createPerson(person);

        // Find the persons with the supplied name
        List<Person> foundPersons = given()
                .when()
                .queryParam("name", person.getDisplayName())
                .queryParam("countryCode", person.getBirthCountryCode())
                .queryParam("yearOfBirth", person.getBirthDate().getYear())
                .queryParam("yearOfDeath", person.getDeathDate() == null ? "" : person.getDeathDate().getYear())
                .get()
                .then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract().as(new TypeRef<>() {});

        assertNotNull(foundPersons);
        assertEquals(1, foundPersons.size());
    }

    /**
     * Tests {@link PersonApiImpl#getPersons(String, String, Integer, Integer)}, where the person does not exist.
     */
    @Test()
    void gettingAllPersonsWherePersonDoesNotExistShouldPass() {
        final Person person = new PersonBuilder().build();
        service.createPerson(person);

        // Find the persons with the supplied name
        List<Person> foundPersons = given()
                .when()
                .queryParam("yearOfDeath", "1648")
                .get()
                .then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract().as(new TypeRef<>() {
                });

        assertNotNull(foundPersons);
        assertEquals(0, foundPersons.size());
    }

}
