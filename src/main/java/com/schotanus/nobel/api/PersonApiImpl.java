package com.schotanus.nobel.api;

import com.schotanus.nobel.model.Person;
import com.schotanus.nobel.service.PersonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;


import java.net.URI;


/**
 * Implements the generated {@link PersonsApi}
 */
public class PersonApiImpl implements PersonsApi {

    private final PersonService service;

    PersonApiImpl(PersonService service) {
        this.service = service;
    }

    @Override
    public Response createPerson(@NotNull @Valid final Person person) {
        return Response.created(URI.create(service.createPerson(person))).build();
    }

    @Override
    public Response getPerson(final String id) {
        return Response.ok(service.getPerson(id)).build();
    }

    @Override
    public Response getPersons(String name, String countryCode, Integer yearOfBirth, Integer yearOfDeath) {
        return Response.ok(service.getPersons(name, countryCode, yearOfBirth, yearOfDeath)).build();
    }

    @Override
    public Response updatePerson(@NotNull @Valid Person person) {
        return Response.ok(service.updatePerson(person)).build();
    }
}
