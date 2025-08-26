package com.schotanus.nobel.service;

import com.schotanus.nobel.model.NobelPrize;
import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.repository.NobelPrizeRepository;
import com.schotanus.nobel.validation.NobelPrizeValidator;
import io.quarkus.logging.Log;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

import java.util.List;


/**
 * This service is responsible for maintaining Nobel Prizes, including any Nobel Prize laureates.
 */
@ApplicationScoped
public class NobelPrizeService extends AbstractService {
    private final NobelPrizeRepository repository;

    NobelPrizeService(NobelPrizeRepository nobelPrizeRepository) {
        this.repository = nobelPrizeRepository;
    }

    /**
     * Creates a Nobel Prize in the database.
     *
     * @param nobelPrize Model to create the Nobel Prize from.
     * @return URL to access the created Nobel Prize.
     */
    @Nonnull
    public String createNobelPrize(@Nonnull @NobelPrizeValidator final NobelPrizeCreate nobelPrize) {
        try {
            Integer id = repository.createNobelPrize(nobelPrize);
            Log.info("Nobel Prize created with id:" + id);
            return getBaseUrl() + "nobelprizes/" + nobelPrize.getYear()  + "/" + nobelPrize.getCategory();
        } catch(EntityExistsException exception) {
            throw new ClientErrorException("This Nobel Prize already exists", Response.Status.CONFLICT);
        }
    }

    /**
     * Gets all Nobel Prizes matching the supplied selection criteria
     *
     * @param year Year the Nobel Prize was awarded.
     * @param categoryCode Category in which the Nobel Prize was awarded.
     * @return All Nobel Prizes matching the supplied selection criteria.
     */
    @Nonnull
    public List<NobelPrize> getNobelPrizes(@Nullable Integer year, @Nullable String categoryCode) {
        return repository.getNobelPrizes(year, categoryCode);
    }
}
