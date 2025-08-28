package com.schotanus.nobel.repository;

import com.schotanus.nobel.model.NobelPrizeCategory;
import com.schotanus.nobel.model.NobelPrizeCategoryEnum;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import org.jooq.DSLContext;

import java.util.List;
import static com.schotanus.nobel.Tables.NOBEL_PRIZE_CATEGORY;


/**
 * This repository is only responsible for selecting Nobel Prize categories.
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
     *
     * @param code The Nobel Prize category code.
     * @return The Nobel Prize category with the supplied code,
     *  or null when no Nobel Prize category exists with the supplied code.
     */
    @Nullable
    public NobelPrizeCategory getNobelPrizeCategory(@Nonnull String code) {
        return dsl.select(NOBEL_PRIZE_CATEGORY.fields())
            .from(NOBEL_PRIZE_CATEGORY)
            .where(NOBEL_PRIZE_CATEGORY.CODE.eq(code))
            .fetchOneInto(NobelPrizeCategory.class);
    }

    /**
     * Gets the primary key of a Nobel Prize category.
     *
     * @param nobelPrizeCategory Category for which the primary key must be fetched.
     * @return The primary key of the Nobel Prize category or null when not found.
     */
    @Nullable
    public Integer getPrimaryKey(@Nonnull NobelPrizeCategoryEnum nobelPrizeCategory) {
        return dsl.select(NOBEL_PRIZE_CATEGORY.ID)
            .from(NOBEL_PRIZE_CATEGORY)
            .where(NOBEL_PRIZE_CATEGORY.CODE.eq(nobelPrizeCategory.name()))
            .fetchOneInto(Integer.class);
    }

    /**
     * Gets all Nobel Prize categories.
     * @return All Nobel Prize categories.
     */
    @Nonnull
    public List<NobelPrizeCategory> getNobelPrizeCategories() {
        return dsl.select(NOBEL_PRIZE_CATEGORY.CODE, NOBEL_PRIZE_CATEGORY.DESCRIPTION)
            .from(NOBEL_PRIZE_CATEGORY)
            .orderBy(NOBEL_PRIZE_CATEGORY.DESCRIPTION)
            .fetchInto(NobelPrizeCategory.class);
    }
}
