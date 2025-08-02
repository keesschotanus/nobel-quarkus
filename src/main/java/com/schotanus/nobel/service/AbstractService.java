package com.schotanus.nobel.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;


/**
 * Base class for services.
 */
public abstract class AbstractService {

    @ConfigProperty(name = "base.url")
    private Optional<String> baseUrl;

    public String getBaseUrl() {
        return baseUrl.orElse("http://localhost:8080/nobel/");
    }

}
