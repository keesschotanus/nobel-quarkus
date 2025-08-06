package com.schotanus.nobel.api;

import com.schotanus.nobel.service.NobelPrizeCategoryService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;

/**
 * Implements the generated {@link NobelprizecategoriesApi}.
 */
public class NobelPrizeCategoryApiImpl implements NobelprizecategoriesApi {

    private final NobelPrizeCategoryService service;

    NobelPrizeCategoryApiImpl(NobelPrizeCategoryService service) {
        this.service = service;
    }

    @Override
    @GET
    public Response getNobelPrizeCategories() {
        return Response.ok(service.getNobelPrizeCategories()).build();
    }

    @Override
    public Response getNobelPrizeCategory(String code) {
        return Response.ok(service.getNobelPrizeCategory(code)).build();
    }
}
