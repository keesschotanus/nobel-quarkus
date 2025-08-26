package com.schotanus.nobel.api;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.schotanus.nobel.DataHelper;
import com.schotanus.nobel.model.Organization;
import com.schotanus.nobel.service.OrganizationService;
import com.schotanus.nobel.util.OrganizationBuilder;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.HttpURLConnection;
import java.util.List;


/**
 * Tests {@link OrganizationApiImpl}.
 */
@QuarkusTest
@TestHTTPEndpoint(OrganizationApiImpl.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizationApiImplIT {

    private final OrganizationService service;
    private final DataHelper dataHelper;

    OrganizationApiImplIT(OrganizationService service, DataHelper dataHelper) {
        this.service = service;
        this.dataHelper = dataHelper;
    }

    @AfterAll
    void cleanUp() {
        dataHelper.deleteOrganizationsWithTestIdentifiers();
    }

    /**
     * Tests {@link OrganizationApiImpl#createOrganization(Organization)}.
     */
    @Test
    void createValidOrganizationShouldPass() {
        given()
            .contentType("application/json")
            .body(new OrganizationBuilder().build())
            .when()
            .post()
            .then()
            .statusCode(HttpURLConnection.HTTP_CREATED);
    }

    /**
     * Tests {@link OrganizationApiImpl#createOrganization(Organization)}.
     */
    @Test
    void createInvalidOrganizationShouldFail() {
        given()
            .contentType("application/json")
            .body(new Organization())
            .when()
            .post()
            .then()
            .statusCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    /**
     * Tests {@link OrganizationApiImpl#createOrganization(Organization)}.
     */
    @Test
    void createExistingOrganizationShouldFail() {
        // First create a new organization
        Organization organization = new OrganizationBuilder().build();
        given()
            .contentType("application/json")
            .body(organization)
            .when()
            .post()
            .then()
            .statusCode(HttpURLConnection.HTTP_CREATED);

        // Now try to create the same organization again
        given()
            .contentType("application/json")
            .body(organization)
            .when()
            .post()
            .then()
            .statusCode(HttpURLConnection.HTTP_CONFLICT);
    }

    /**
     * Tests {@link OrganizationApiImpl#getOrganization(String)}.
     */
    @Test
    void getExistingOrganizationByIdShouldPass() {
        final Organization organization = new OrganizationBuilder().build();
        service.createOrganization(organization);

        Organization foundOrganization = given()
            .when()
            .pathParam("id", organization.getOrganizationIdentifier())
            .get("{id}")
            .then()
            .statusCode(HttpURLConnection.HTTP_OK)
            .extract().as(Organization.class);

        assertNotNull(foundOrganization);
    }

    /**
     * Tests {@link OrganizationApiImpl#getOrganization(String)}.
     */
    @Test
    void getNonExistingOrganizationByIdShouldFail() {
        given()
            .when()
            .pathParam("id", "Unknown")
            .get("{id}")
            .then()
            .statusCode(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Tests {@link OrganizationApiImpl#getOrganizations(String)} without specifying a name.
     */
    @Test()
    void gettingAllOrganizationsShouldPass() {
        final Organization organization = new OrganizationBuilder().build();
        service.createOrganization(organization);

        // Find all organizations
        List<Organization> foundOrganizations = given()
            .when()
            .get()
            .then()
            .statusCode(HttpURLConnection.HTTP_OK)
            .extract().as(new TypeRef<>() {});

        assertNotNull(foundOrganizations);

        // Check that created organization is present in the response
        for (Organization foundOrganization : foundOrganizations) {
            if (foundOrganization.getOrganizationIdentifier().equals(organization.getOrganizationIdentifier())
                    && foundOrganization.getName().equals(organization.getName())) {
                return;
            }
        }
        Assertions.fail("Did not find the previously created organization");
    }

    /**
     * Tests {@link OrganizationApiImpl#getOrganizations(String)} with an existing name.
     */
    @Test()
    void gettingAllOrganizationsWithExistingNameShouldPass() {
        final Organization organization = new OrganizationBuilder().build();
        service.createOrganization(organization);

        // Find all organizations
        List<Organization> foundOrganizations = given()
                .when()
                .queryParam("name", organization.getName())
                .get()
                .then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract().as(new TypeRef<>() {});

        assertNotNull(foundOrganizations);
        assertEquals(1, foundOrganizations.size());

        final Organization foundOrganisation = foundOrganizations.getFirst();
        assertEquals(organization.getOrganizationIdentifier(), foundOrganisation.getOrganizationIdentifier());
        assertEquals(organization.getName(), foundOrganisation.getName());
    }

    /**
     * Tests {@link OrganizationApiImpl#getOrganizations(String)} with a non-existing name.
     */
    @Test()
    void gettingAllOrganizationsWithNonExistingNameShouldPass() {
        final Organization organization = new OrganizationBuilder().build();
        service.createOrganization(organization);

        // Find all organizations
        List<Organization> foundOrganizations = given()
                .when()
                .queryParam("name", "Unknown organization")
                .get()
                .then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract().as(new TypeRef<>() {});

        assertNotNull(foundOrganizations);
        assertEquals(0, foundOrganizations.size());
    }
}
