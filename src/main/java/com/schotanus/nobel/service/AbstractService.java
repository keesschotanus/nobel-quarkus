package com.schotanus.nobel.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;


/**
 * Base class for services.
 */
@ApplicationScoped
public abstract class AbstractService {

    @ConfigProperty(name = "base.url")
    String baseUrl;

    /**
     * Gets the base url of the application from an application properties file.
     *
     * @return The base url of the application from an application properties file.
     */
    public String getBaseUrl() {
        return baseUrl == null ? "http://localhost:8080/nobel/" : baseUrl;
    }

}
