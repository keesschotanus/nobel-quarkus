package com.schotanus.nobel.service;

import com.schotanus.nobel.model.NobelPrizeCategory;
import com.schotanus.nobel.repository.NobelPrizeCategoryRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

/**
 * This NobelPrizeCategoryService is responsible for selecting Nobel Prize categories.
 * Since it is not expected that these categories will chang often, updates have to be done
 * on the database.
 */
@ApplicationScoped
public class NobelPrizeCategoryService {

    private final NobelPrizeCategoryRepository repository;

    NobelPrizeCategoryService(NobelPrizeCategoryRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets a Nobel Prize category by its unique code.
     * @param code The code of the Nobel Prize category.
     * @return The Nobel Prize category by code.
     * @throws NotFoundException when no such Nobel Prize category exists.
     */
    public NobelPrizeCategory getNobelPrizeCategory(@Nonnull final String code) {
        NobelPrizeCategory category = repository.getNobelPrizeCategory(code);
        if (category == null) {
            throw new NotFoundException("Category: " + code + ", not found");
        }
        
        return category;
    }

    /**
     * Gets all Nobel Prize categories.
     * @return A complete list of Nobel Prize categories.
     */
    public @Nonnull List<NobelPrizeCategory> getNobelPrizeCategories() {
        return repository.getNobelPrizeCategories();
    }

}
