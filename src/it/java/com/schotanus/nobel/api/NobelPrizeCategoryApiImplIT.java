package com.schotanus.nobel.api;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.schotanus.nobel.model.NobelPrizeCategory;
import com.schotanus.nobel.model.NobelPrizeCategoryEnum;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.HttpURLConnection;
import java.util.List;


/**
 * Tests {@link NobelPrizeCategoryApiImpl}.
 */
@QuarkusTest
@TestHTTPEndpoint(NobelPrizeCategoryApiImpl.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NobelPrizeCategoryApiImplIT {

    NobelPrizeCategoryApiImplIT() {
    }

    /**
     * Tests {@link NobelPrizeCategoryApiImpl#getNobelPrizeCategory(String)} with existing category.
     */
    @Test
    void getExistingCategoryShouldPass() {
        NobelPrizeCategory foundCategory = given()
            .when()
            .pathParam("code", NobelPrizeCategoryEnum.P)
            .get("{code}")
            .then()
            .statusCode(HttpURLConnection.HTTP_OK)
            .extract().as(NobelPrizeCategory.class);

        assertNotNull(foundCategory);
    }

    /**
     * Tests {@link NobelPrizeCategoryApiImpl#getNobelPrizeCategory(String)} with unknown category.
     */
    @Test
    void getNonExistingCategoryShouldFail() {
        given()
            .when()
            .pathParam("code", "Q")
            .get("{code}")
            .then()
            .statusCode(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Tests {@link NobelPrizeCategoryApiImpl#getNobelPrizeCategories()}.
     */
    @Test
    void getAllNobelPrizeCategoriesShouldPass() {
        List<NobelPrizeCategory> foundNobelPrizeCategories = given()
            .when()
            .get()
            .then()
            .statusCode(HttpURLConnection.HTTP_OK)
            .extract().as(new TypeRef<>() {});

        assertEquals(NobelPrizeCategoryEnum.values().length, foundNobelPrizeCategories.size());
    }

}
