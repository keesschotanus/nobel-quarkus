package com.schotanus.nobel.util;

import com.schotanus.nobel.model.Organization;
import java.util.UUID;


public class OrganizationBuilder {
    // Required fields
    private final String organizationIdentifier;
    private final String name;

    // Optional fields
    private String description;
    private String url;

    /**
     * Creates random data for an organization's required fields.
     */
    public OrganizationBuilder() {
        UUID uuid = UUID.randomUUID();
        this.organizationIdentifier = "test" + uuid;
        this.name = "name" + uuid;
    }

    public OrganizationBuilder(String organizationIdentifier, String name) {
        this.organizationIdentifier = organizationIdentifier;
        this.name = name;
    }

    public OrganizationBuilder description(String description) {
        this.description = description;
        return this;
    }

    public OrganizationBuilder url(String url) {
        this.url = url;
        return this;
    }

    public Organization build() {
        Organization organization = new Organization();
        organization.setOrganizationIdentifier(organizationIdentifier);
        organization.setName(name);
        organization.setDescription(description);
        organization.setUrl(url);

        return organization;
    }

}
