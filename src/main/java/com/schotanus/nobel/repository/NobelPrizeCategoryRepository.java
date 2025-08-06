package com.schotanus.nobel.repository;

import com.schotanus.nobel.model.NobelPrizeCategory;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import org.jooq.DSLContext;

import java.util.List;
import static com.schotanus.nobel.Tables.NOBEL_PRIZE_CATEGORY;


/**
 * This NobelPrizeCategoryRepository is only responsible for selecting
 * Nobel Prize categories.
 * To add or update a category, update Liquibase's changeLog.xml file.
 */
@ApplicationScoped
public class NobelPrizeCategoryRepository {

    private final DSLContext dsl;

    NobelPrizeCategoryRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Gets a single Nobel Prize category by its unique code.
     * @param code The Nobel Prize category code.
     * @return The Nobel Prize category with the supplied code,
     *  or null when no Nobel Prize category exists with the supplied code.
     */
    public NobelPrizeCategory getNobelPrizeCategory(@Nonnull String code) {
        return dsl.select(NOBEL_PRIZE_CATEGORY.fields())
            .from(NOBEL_PRIZE_CATEGORY)
            .where(NOBEL_PRIZE_CATEGORY.CODE.eq(code))
            .fetchOneInto(NobelPrizeCategory.class);
    }

    /**
     * Gets all Nobel Prize categories.
     * @return All Nobel Prize categories.
     */
    public @Nonnull List<NobelPrizeCategory> getNobelPrizeCategories() {
        return dsl.select(NOBEL_PRIZE_CATEGORY.CODE)
            .from(NOBEL_PRIZE_CATEGORY)
            .orderBy(NOBEL_PRIZE_CATEGORY.DESCRIPTION)
            .fetchInto(NobelPrizeCategory.class);
    }
}
