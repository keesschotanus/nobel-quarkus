package com.schotanus.nobel.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.schotanus.nobel.api.OrganizationApiImpl;
import com.schotanus.nobel.model.Organization;
import com.schotanus.nobel.util.OrganizationBuilder;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link OrganizationService} methods that are not part of {@link OrganizationApiImpl}.
 */
@QuarkusTest
class OrganizationServiceIT {

    private final OrganizationService service;

    OrganizationServiceIT(OrganizationService service) {
        this.service = service;
    }

    /**
     * Tests {@link OrganizationService#getPrimaryKey(String)}
     */
    @Test
    void getPrimaryKeyOfExistingOrganizationShouldPass() {
        final Organization organization = new OrganizationBuilder().build();
        service.createOrganization(organization);

        assertNotNull(service.getPrimaryKey(organization.getOrganizationIdentifier()));
    }

    /**
     * Tests {@link OrganizationService#getPrimaryKey(String)}
     */
    @Test
    void getPrimaryKeyOfNonExistingShouldFail() {
        assertThrows(NotFoundException.class, () -> service.getPrimaryKey("unknown primary key"));
    }
}
