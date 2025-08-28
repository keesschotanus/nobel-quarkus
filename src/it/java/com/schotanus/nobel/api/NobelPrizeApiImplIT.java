package com.schotanus.nobel.api;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schotanus.nobel.DataHelper;
import com.schotanus.nobel.model.NobelPrize;
import com.schotanus.nobel.model.NobelPrizeCategoryEnum;
import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreateType;
import com.schotanus.nobel.model.Person;
import com.schotanus.nobel.service.NobelPrizeService;
import com.schotanus.nobel.service.PersonService;
import com.schotanus.nobel.util.NobelPrizeCreateBuilder;
import com.schotanus.nobel.util.NobelPrizeLaureateCreateBuilder;
import com.schotanus.nobel.util.PersonBuilder;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.HttpURLConnection;
import java.util.List;


/**
 * Tests {@link NobelPrizeApiImpl}.
 * This class always uses the "Economics" category and years before 1969 since actual Nobel Prizes in this category were not
 * awarded before 1969.
 */
@QuarkusTest
@TestHTTPEndpoint(NobelPrizeApiImpl.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NobelPrizeApiImplIT {

    private final DataHelper dataHelper;
    private final NobelPrizeService nobelPrizeService;
    private final PersonService personService;

    NobelPrizeApiImplIT(DataHelper dataHelper, NobelPrizeService nobelPrizeService, PersonService personService) {
        this.dataHelper = dataHelper;
        this.nobelPrizeService = nobelPrizeService;
        this.personService = personService;
    }

    @AfterAll
    void cleanUp() {
        dataHelper.deleteNobelPrizeTestData();
    }

    /**
     * Tests {@link NobelPrizeApiImpl#createNobelPrize(NobelPrizeCreate)}.
     */
    @Test
    void createNobelPrizeWithValidDataShouldPass() {
        // A person must exist before we can create a Nobel Prize
        final Person person = new PersonBuilder().build();
        personService.createPerson(person);

        NobelPrizeLaureateCreateType laureateType = NobelPrizeLaureateCreateBuilder.createLaureatePerson(person.getPersonIdentifier());
        NobelPrizeLaureateCreate laureate = new NobelPrizeLaureateCreateBuilder(laureateType, 1, 1).build();

        NobelPrizeCreate nobelPrize = new NobelPrizeCreateBuilder(NobelPrizeCategoryEnum.E, 1901, List.of(laureate)).build();

        given()
            .contentType("application/json")
            .body(nobelPrize)
            .when()
            .post()
            .then()
            .statusCode(HttpURLConnection.HTTP_CREATED);
    }

    /**
     * Tests {@link NobelPrizeApiImpl#createNobelPrize(NobelPrizeCreate)}.
     */
    @Test
    void createNobelPrizeThatAlreadyExistsShouldFail() {
        final Integer year = 1902;
        NobelPrizeCreate createdNobelPrize = this.createTestNobelPrize(year);

        given()
            .contentType("application/json")
            .body(createdNobelPrize)
            .when()
            .post()
            .then()
            .statusCode(HttpURLConnection.HTTP_CONFLICT);
    }

    /**
     * Tests {@link NobelPrizeApiImpl#createNobelPrize(NobelPrizeCreate)}.
     */
    @Test
    void createNobelPrizeWithInvalidDataShouldFail() {
        given()
            .contentType("application/json")
            .body(new NobelPrizeCreate())
            .when()
            .post()
            .then()
            .statusCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    /**
     * Tests {@link NobelPrizeApiImpl#getNobelPrizes(Integer, String)} without using any parameters.
     */
    @Test
    void getNobelPrizesWithoutQueryParametersShouldPass() {
        final Integer year = 1903;
        this.createTestNobelPrize(year);

        List<NobelPrize> foundNobelPrizes = given()
            .when()
            .get()
            .then()
            .statusCode(HttpURLConnection.HTTP_OK)
            .extract().as(new TypeRef<>() {});

        assertNotNull(foundNobelPrizes);
        assertTrue(foundNobelPrizes.stream().anyMatch(
            np -> np.getYear().equals(year) && np.getCategory() == NobelPrizeCategoryEnum.E));
    }

    /**
     * Tests {@link NobelPrizeApiImpl#getNobelPrizes(Integer, String)} filtered by year.
     */
    @Test
    void getNobelPrizesByYearShouldPass() {
        Integer year = 1904;
        this.createTestNobelPrize(year);

        List<NobelPrize> foundNobelPrizes = given()
            .when()
            .queryParam("year")
            .get()
            .then()
            .statusCode(HttpURLConnection.HTTP_OK)
            .extract().as(new TypeRef<>() {});

        assertNotNull(foundNobelPrizes);
        assertTrue(foundNobelPrizes.stream().anyMatch(
            np -> np.getYear().equals(year) && np.getCategory() == NobelPrizeCategoryEnum.E));
    }

    /**
     * Tests {@link NobelPrizeApiImpl#getNobelPrizes(Integer, String)} filtered by year and category.
     */
    @Test
    void getNobelPrizesByYearAndCategoryShouldPass() {
        Integer year = 1905;
        this.createTestNobelPrize(year);

        List<NobelPrize> foundNobelPrizes = given()
            .when()
            .queryParam("year", year)
            .queryParam("category", NobelPrizeCategoryEnum.E)
            .get()
            .then()
            .statusCode(HttpURLConnection.HTTP_OK)
            .extract().as(new TypeRef<>() {});

        assertNotNull(foundNobelPrizes);
        assertEquals(1, foundNobelPrizes.size());

        NobelPrize foundNobelPrize = foundNobelPrizes.getFirst();
        assertEquals(year, foundNobelPrize.getYear());
        assertEquals(NobelPrizeCategoryEnum.E, foundNobelPrize.getCategory());
    }

    /**
     * Creates a Nobel Prize (in the category "Economics"), in the database.
     * @param year Year the Nobel Prize was awarded.
     * @return The created Nobel Prize.
     */
    private NobelPrizeCreate createTestNobelPrize(@Nonnull final Integer year) {
        // First create a person
        final Person person = new PersonBuilder().build();
        personService.createPerson(person);

        // The laureate will be a person
        NobelPrizeLaureateCreateType laureateType = NobelPrizeLaureateCreateBuilder.createLaureatePerson(person.getPersonIdentifier());
        NobelPrizeLaureateCreate laureate = new NobelPrizeLaureateCreateBuilder(laureateType, 1, 1).build();

        NobelPrizeCreate nobelPrize = new NobelPrizeCreateBuilder(NobelPrizeCategoryEnum.E, year, List.of(laureate)).build();
        nobelPrizeService.createNobelPrize(nobelPrize);

        return nobelPrize;
    }

}
