package com.schotanus.nobel.service;

import com.schotanus.nobel.model.NobelPrizeCategory;
import com.schotanus.nobel.model.NobelPrizeCategoryEnum;
import com.schotanus.nobel.repository.NobelPrizeCategoryRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

/**
 * This service is responsible for selecting Nobel Prize categories.
 * Since it is not expected that these categories will chang often, updates have to be done on the database.
 */
@ApplicationScoped
public class NobelPrizeCategoryService {

    private final NobelPrizeCategoryRepository repository;

    NobelPrizeCategoryService(NobelPrizeCategoryRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets a Nobel Prize category by its unique code.
     *
     * @param code The code of the Nobel Prize category.
     * @return The Nobel Prize category by code.
     * @throws NotFoundException When no such Nobel Prize category exists.
     */
    @Nonnull
    public NobelPrizeCategory getNobelPrizeCategory(@Nonnull final String code) {
        NobelPrizeCategory category = repository.getNobelPrizeCategory(code);
        if (category == null) {
            throw new NotFoundException("Nobel Prize category: " + code + ", not found");
        }
        
        return category;
    }

    /**
     * Gets the primary key of a Nobel Prize category.
     *
     * @param nobelPrizeCategory Category for which the primary key must be fetched.
     * @return The primary key of the Nobel Prize category.
     * @throws NotFoundException When the supplied category does not exist.
     */
    @Nonnull
    public Integer getPrimaryKey(@Nonnull final NobelPrizeCategoryEnum nobelPrizeCategory) {
        final Integer primaryKey = repository.getPrimaryKey(nobelPrizeCategory);
        if (primaryKey == null) {
            throw new NotFoundException("Nobel prize category: " + nobelPrizeCategory + ", not found");
        }

        return primaryKey;
    }

    /**
     * Gets all Nobel Prize categories.
     * @return A complete list of Nobel Prize categories.
     */
    @Nonnull
    public List<NobelPrizeCategory> getNobelPrizeCategories() {
        return repository.getNobelPrizeCategories();
    }

}
