package com.schotanus.nobel.api;

import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.service.NobelPrizeService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.core.Response;

/**
 * Implements the generated {@link NobelprizesApi}.
 */
public class NobelPrizeApiImpl implements NobelprizesApi {

    private final NobelPrizeService service;

    NobelPrizeApiImpl(NobelPrizeService service) {
        this.service = service;
    }

    @Override
    public Response createNobelPrize(@Nonnull NobelPrizeCreate nobelPrize) {
        service.createNobelPrize(nobelPrize);
        return  Response.created(null).build();
    }

    @Override
    public Response getNobelPrizes(@Nullable Integer year, @Nullable String category) {
        return Response.ok(service.getNobelPrizes(year, category)).build();
    }

}
