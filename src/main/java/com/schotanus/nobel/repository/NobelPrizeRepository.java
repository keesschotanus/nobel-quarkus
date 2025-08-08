package com.schotanus.nobel.repository;

import com.schotanus.nobel.model.NobelPrize;
import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.model.NobelPrizeLaureate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreate;
import com.schotanus.nobel.model.Person;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;

import static com.schotanus.nobel.Tables.ORGANIZATION;
import static com.schotanus.nobel.tables.NobelPrize.NOBEL_PRIZE;
import static com.schotanus.nobel.tables.NobelPrizeCategory.NOBEL_PRIZE_CATEGORY;
import static com.schotanus.nobel.tables.NobelPrizeLaureate.NOBEL_PRIZE_LAUREATE;
import static com.schotanus.nobel.tables.Person.PERSON;
import static org.jooq.impl.DSL.trueCondition;

/**
 * This NobelPrizeRepository is responsible for maintaining and selecting Nobel Prizes.
 * These normally include any laureates.
 */
@ApplicationScoped
public class NobelPrizeRepository {

    private final DSLContext dsl;

    NobelPrizeRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Gets all Nobel Prizes, optionally filtered by year and category.
     * @param year Year the Nobel Prize was awarded.
     * @param category Category in which the Nobel Prize was awarded.
     * @return All Nobel Prizes matching the supplied selection criteria.
     */
    public @Nonnull List<NobelPrize> getNobelPrizes(@Nullable final Integer year, @Nullable final String category) {
        Condition condition = trueCondition();
        if (year != null) {
            condition = condition.and(NOBEL_PRIZE.YEAR.eq(year));
        }
        if (category != null && !category.isBlank()) {
            condition = condition.and(NOBEL_PRIZE_CATEGORY.CODE.eq(category));
        }

        return dsl.select(
            NOBEL_PRIZE_CATEGORY.CODE.as("category"),
            NOBEL_PRIZE.YEAR,
            NOBEL_PRIZE.URL,
            DSL.multiset(
                dsl.select(
                    NOBEL_PRIZE_LAUREATE.DESCRIPTION,
                    NOBEL_PRIZE_LAUREATE.FRACTIONNOMINATOR,
                    NOBEL_PRIZE_LAUREATE.FRACTIONDENOMINATOR,
                    DSL.multiset(
                        dsl.selectFrom(PERSON).where(PERSON.ID.eq(NOBEL_PRIZE_LAUREATE.PERSONID))
                    ).convertFrom(personRecords -> personRecords.stream()
                        .findFirst()
                        .map(personRecord -> personRecord.into(Person.class))
                        .orElse(null)
                    ).as("person"),
                    DSL.multiset(
                        dsl.selectFrom(ORGANIZATION).where(ORGANIZATION.ID.eq(NOBEL_PRIZE_LAUREATE.ORGANIZATIONID))
                    ).convertFrom(organizationRecords -> organizationRecords.stream()
                        .findFirst()
                        .map(organizationRecord -> organizationRecord.into(Person.class))
                        .orElse(null)
                    ).as("organization")
                )
                .from(NOBEL_PRIZE_LAUREATE)
                .where(NOBEL_PRIZE_LAUREATE.NOBELPRIZEID.eq(NOBEL_PRIZE.ID))
            ).convertFrom(laureateRecords -> laureateRecords.into(NobelPrizeLaureate.class)).as("laureates")
        )
        .from(NOBEL_PRIZE)
        .join(NOBEL_PRIZE_CATEGORY).on(NOBEL_PRIZE_CATEGORY.ID.eq(NOBEL_PRIZE.CATEGORYID))
        .where(condition)
        .orderBy(NOBEL_PRIZE.YEAR, NOBEL_PRIZE.CATEGORYID)
        .fetchInto(NobelPrize.class);
    }

    /**
     * Creates a Nobel Prize, including the Nobel Prize laureates.
     * @param nobelPrize Nobel Prize model.
     * @return Primary key of the created Nobel Prize.
     */
    public @Nonnull Integer createNobelPrize(NobelPrizeCreate nobelPrize) {
        Integer nobelPrizeId = dsl.insertInto(NOBEL_PRIZE)
            .columns(
                NOBEL_PRIZE.CATEGORYID,
                NOBEL_PRIZE.YEAR,
                NOBEL_PRIZE.URL,
                NOBEL_PRIZE.CREATEDBYID,
                NOBEL_PRIZE.LASTMODIFIEDBYID)
            .values(1, nobelPrize.getYear(), nobelPrize.getUrl(), 1, 1)
            .returningResult(NOBEL_PRIZE.ID)
            .fetchSingleInto(Integer.class);

        List<NobelPrizeLaureateCreate> nobelPrizeLaureates = nobelPrize.getLaureates();
        for (NobelPrizeLaureateCreate nobelPrizeLaureate : nobelPrizeLaureates ) {
            Integer personId = null;
            if (nobelPrizeLaureate.getPersonIdentifier() != null && !nobelPrizeLaureate.getPersonIdentifier().isBlank()) {
                personId = dsl.select(PERSON.ID)
                    .from(PERSON)
                    .where(PERSON.PERSONIDENTIFIER.eq(nobelPrizeLaureate.getPersonIdentifier()))
                    .fetchOneInto(Integer.class);
                // Todo error handling
            }

            Integer organizationId = null;
            if (nobelPrizeLaureate.getOrganizationIdentifier() != null
                    && !nobelPrizeLaureate.getOrganizationIdentifier().isBlank()) {
                organizationId = dsl.select(ORGANIZATION.ID)
                        .from(ORGANIZATION)
                        .where(ORGANIZATION.ORGANIZATIONIDENTIFIER.eq(nobelPrizeLaureate.getOrganizationIdentifier()))
                        .fetchOneInto(Integer.class);
                // Todo error handling
            }
            // Todo check not both id's can be non null
            dsl.insertInto(NOBEL_PRIZE_LAUREATE)
                .columns(
                    NOBEL_PRIZE_LAUREATE.NOBELPRIZEID,
                    NOBEL_PRIZE_LAUREATE.PERSONID,
                    NOBEL_PRIZE_LAUREATE.ORGANIZATIONID,
                    NOBEL_PRIZE_LAUREATE.DESCRIPTION,
                    NOBEL_PRIZE_LAUREATE.FRACTIONNOMINATOR,
                    NOBEL_PRIZE_LAUREATE.FRACTIONDENOMINATOR,
                    NOBEL_PRIZE_LAUREATE.CREATEDBYID,
                    NOBEL_PRIZE_LAUREATE.LASTMODIFIEDBYID)
                .values(
                    nobelPrizeId,
                    personId,
                    organizationId,
                    nobelPrizeLaureate.getDescription(),
                    nobelPrizeLaureate.getFractionNominator(),
                    nobelPrizeLaureate.getFractionDenominator(),
                    1,
                    1)
                .execute();
        }

        return nobelPrizeId;
    }
}
