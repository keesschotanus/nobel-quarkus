package com.schotanus.nobel.api;

import com.schotanus.nobel.model.Organization;
import com.schotanus.nobel.service.OrganizationService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.core.Response;

import java.net.URI;


/**
 * Implements the generated {@link OrganizationsApi}
 */
public class OrganizationApiImpl implements OrganizationsApi {

    private final OrganizationService service;

    OrganizationApiImpl(OrganizationService service) {
        this.service = service;
    }

    @Override
    public Response createOrganization(@Nonnull final Organization organization) {
        String url = service.createOrganization(organization);

        return Response.created(URI.create(url)).build();
    }

    @Override
    public Response getOrganization(@Nonnull final String id) {
        return Response.ok(service.getOrganization(id)).build();
    }

    @Override
    public Response getOrganizations(@Nullable String name) {
        return Response.ok(service.getOrganizations(name)).build();
    }

}
