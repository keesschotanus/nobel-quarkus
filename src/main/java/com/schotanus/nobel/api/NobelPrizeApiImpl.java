package com.schotanus.nobel.api;

import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.service.NobelPrizeService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;

import java.net.URI;


/**
 * Implements the generated {@link NobelprizesApi}.
 */
public class NobelPrizeApiImpl implements NobelprizesApi {

    private final NobelPrizeService service;

    NobelPrizeApiImpl(NobelPrizeService service) {
        this.service = service;
    }

    @Override
    public Response createNobelPrize(@NotNull @Valid NobelPrizeCreate nobelPrize) {
        return Response.created(URI.create(service.createNobelPrize(nobelPrize))).build();
    }

    @Override
    public Response getNobelPrizes(@Nullable Integer year, @Nullable String category) {
        return Response.ok(service.getNobelPrizes(year, category)).build();
    }

}
