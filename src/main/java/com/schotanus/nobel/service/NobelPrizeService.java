package com.schotanus.nobel.service;

import com.schotanus.nobel.model.NobelPrize;
import com.schotanus.nobel.repository.NobelPrizeRepository;
import com.schotanus.nobel.validation.NobelPrizeValidator;
import io.quarkus.logging.Log;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;


/**
 * This NobelPrizeService is responsible for maintaining Nobel Prizes,
 * including any Nobel Prize laureates.
 */
@ApplicationScoped
public class NobelPrizeService extends AbstractService {
    private final NobelPrizeRepository nobelPrizeRepository;

    NobelPrizeService(NobelPrizeRepository nobelPrizeRepository) {
        this.nobelPrizeRepository = nobelPrizeRepository;
    }

    /**
     * Creates a Nobel Prize in the database.
     *
     * @param nobelPrize Model to create the Nobel Prize from.
     * @return URL to access the created Nobel Prize.
     */
    public String createNobelPrize(@Nonnull @NobelPrizeValidator final NobelPrize nobelPrize) {
        Integer id = nobelPrizeRepository.createNobelPrize(nobelPrize);
        Log.info("Nobel Prize created with id:" + id);
        return getBaseUrl() + "nobelprizes/" + nobelPrize.getYear()  + "/" + nobelPrize.getCategory();
    }

    /**
     * Gets all {@link NobelPrize} objects matching the supplied selection criteria
     *
     * @param year         Year the Nobel Prize was awarded.
     * @param categoryCode Category in which the Nobel Prize was awarded.
     * @return All NobelPrizes matching the supplied selection criteria.
     */
    public @Nonnull List<NobelPrize> getNobelPrizes(@Nullable Integer year, @Nullable String categoryCode) {
        return nobelPrizeRepository.getNobelPrizes(year, categoryCode);
    }
}
