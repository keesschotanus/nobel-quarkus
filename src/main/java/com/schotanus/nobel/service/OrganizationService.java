package com.schotanus.nobel.service;

import com.schotanus.nobel.model.Organization;
import com.schotanus.nobel.repository.OrganizationRepository;
import io.quarkus.logging.Log;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.List;


/**
 * This service is responsible for maintaining organizations which can win the Nobel Peace Prize.
 */
@ApplicationScoped
public class OrganizationService extends AbstractService {

    private final OrganizationRepository repository;

    OrganizationService(OrganizationRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates an organization in the database.
     *
     * @param organization model to create the organization from.
     * @return URL to access the created organization.
     */
    @Nonnull
    public String createOrganization(@Nonnull final Organization organization) {
        try {
            Integer id = repository.createOrganization(organization);
            Log.info("Organization created with id:" + id);
            return getBaseUrl() + "organizations/" + organization.getOrganizationIdentifier();
        } catch(EntityExistsException exception) {
            throw new ClientErrorException("This organization already exists", Response.Status.CONFLICT);
        }
    }

    /**
     * Gets an organization by its unique organization identifier.
     *
     * @param organizationIdentifier Organization identifier.
     * @return The organization with the supplied identifier.
     * @throws NotFoundException when no organization with the supplied identifier exists.
     */
    @Nonnull
    public Organization getOrganization(@Nonnull String organizationIdentifier) {
        Organization organization = repository.getOrganization(organizationIdentifier);
        if (organization == null) {
            throw new NotFoundException("Organization with identifier: " + organizationIdentifier + ", not found");
        }

        return organization;
    }

    /**
     * Gets all organizations matching the supplied selection criteria.
     *
     * @param name Name (or first part of the name) of the organization.
     * @return All organizations matching the supplied selection criteria.
     */
    @Nonnull
    public List<Organization> getOrganizations(@Nullable String name) {
        return repository.getOrganizations(name);
    }

    /**
     * Gets the primary key of an organization by its unique organization identifier.
     *
     * @param organizationIdentifier Organization identifier.
     * @return The primary key of the organization.
     * @throws NotFoundException When no organization with the supplied identifier exists.
     */
    @Nonnull
    public Integer getPrimaryKey(@Nonnull final String organizationIdentifier) {
        final Integer primaryKey = repository.getPrimaryKey(organizationIdentifier);
        if (primaryKey == null) {
            throw new NotFoundException("Organization with identifier: " + organizationIdentifier + ", not found");
        }

        return primaryKey;
    }

}
