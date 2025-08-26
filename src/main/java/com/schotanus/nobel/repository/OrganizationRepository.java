package com.schotanus.nobel.repository;

import static com.schotanus.nobel.Tables.ORGANIZATION;
import static org.jooq.impl.DSL.trueCondition;
import static org.jooq.impl.DSL.upper;

import com.schotanus.nobel.model.Organization;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityExistsException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.exception.IntegrityConstraintViolationException;

import java.util.List;


/**
 * This repository is responsible for maintaining and selecting organizations.
 */
@ApplicationScoped
public class OrganizationRepository {

    private final DSLContext dsl;

    private final List<Field<?>> organizationFields = List.of(
        ORGANIZATION.ORGANIZATIONIDENTIFIER,
        ORGANIZATION.NAME,
        ORGANIZATION.DESCRIPTION,
        ORGANIZATION.URL);

    OrganizationRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Create an organization.
     *
     * @param organization Model to create the organization from.
     * @return The primary key of the created organization.
     */
    @Nonnull
    public Integer createOrganization(@Nonnull final Organization organization) {
        try {
            return dsl.insertInto(ORGANIZATION).columns(
                ORGANIZATION.ORGANIZATIONIDENTIFIER,
                ORGANIZATION.NAME,
                ORGANIZATION.DESCRIPTION,
                ORGANIZATION.URL,
                ORGANIZATION.CREATEDBYID,
                ORGANIZATION.LASTMODIFIEDBYID)
            .values(
                organization.getOrganizationIdentifier(),
                organization.getName(),
                organization.getDescription(),
                organization.getUrl(),
                1,
                1)
            .returningResult(ORGANIZATION.ID)
            .fetch().getFirst().value1();
        } catch (IntegrityConstraintViolationException exception) {
            throw new EntityExistsException("This organization already exists");
        }
    }

    /**
     * Gets an organization by its unique organization identifier.
     *
     * @param organizationIdentifier Organization identifier.
     * @return The Organization with the supplied identifier, or null when not found.
     */
    @Nullable
    public Organization getOrganization(@Nonnull final String organizationIdentifier) {
        return dsl.select(organizationFields)
            .from(ORGANIZATION)
            .where(ORGANIZATION.ORGANIZATIONIDENTIFIER.eq(organizationIdentifier))
            .fetchOneInto(Organization.class);
    }

    /**
     * Gets the primary key of an organization by its unique organization identifier.
     *
     * @param organizationIdentifier Organization identifier.
     * @return The primary key of the organization, or null when not found.
     */
    @Nullable
    public Integer getPrimaryKey(@Nonnull final String organizationIdentifier) {
        return dsl.select(ORGANIZATION.ID)
            .from(ORGANIZATION)
            .where(ORGANIZATION.ORGANIZATIONIDENTIFIER.eq(organizationIdentifier))
            .fetchOneInto(Integer.class);
    }

    /**
     * Gets all organizations matching the supplied selection criteria.
     *
     * @param name Name (or first part of the name) of the organization.
     * @return All Organizations matching the supplied selection criteria.
     */
    @Nonnull
    public List<Organization> getOrganizations(@Nullable final String name) {
        Condition condition = trueCondition();
        if (name != null && !name.isBlank()) {
            condition = condition.and(upper(ORGANIZATION.NAME).like(name.toUpperCase() + "%"));
        }

        return dsl.select(organizationFields)
            .from(ORGANIZATION)
            .where(condition)
            .orderBy(ORGANIZATION.NAME)
            .fetchInto(Organization.class);
    }

}
