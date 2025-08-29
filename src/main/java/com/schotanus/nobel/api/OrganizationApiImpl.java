package com.schotanus.nobel.api;

import com.schotanus.nobel.model.Organization;
import com.schotanus.nobel.service.OrganizationService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public Response createOrganization(@NotNull @Valid final Organization organization) {
        return Response.created(URI.create(service.createOrganization(organization))).build();
    }

    @Override
    public Response getOrganization(final String id) {
        return Response.ok(service.getOrganization(id)).build();
    }

    @Override
    public Response getOrganizations(@Nullable String name) {
        return Response.ok(service.getOrganizations(name)).build();
    }

}
