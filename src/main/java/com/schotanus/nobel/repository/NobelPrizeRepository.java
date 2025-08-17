package com.schotanus.nobel.repository;

import com.schotanus.nobel.model.NobelPrize;
import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.model.NobelPrizeLaureate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreate;
import com.schotanus.nobel.model.Person;
import com.schotanus.nobel.service.OrganizationService;
import com.schotanus.nobel.service.PersonService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.jooq.impl.DSL;

import java.util.List;

import static com.schotanus.nobel.Tables.ORGANIZATION;
import static com.schotanus.nobel.tables.NobelPrize.NOBEL_PRIZE;
import static com.schotanus.nobel.tables.NobelPrizeCategory.NOBEL_PRIZE_CATEGORY;
import static com.schotanus.nobel.tables.NobelPrizeLaureate.NOBEL_PRIZE_LAUREATE;
import static com.schotanus.nobel.tables.Person.PERSON;
import static org.jooq.impl.DSL.trueCondition;

/**
 * This repository is responsible for maintaining and selecting Nobel Prizes.
 * These include any laureates.
 */
@ApplicationScoped
public class NobelPrizeRepository {

    private final DSLContext dsl;
    private final PersonService personService;
    private final OrganizationService organizationService;

    NobelPrizeRepository(DSLContext dsl, PersonService personService, OrganizationService organizationService) {
        this.dsl = dsl;
        this.personService = personService;
        this.organizationService = organizationService;
    }

    /**
     * Creates a Nobel Prize, including the Nobel Prize laureates.
     *
     * @param nobelPrize Nobel Prize model.
     * @return Primary key of the created Nobel Prize.
     * @throws EntityExistsException When the Nobel Prize already existed.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @Nonnull
    public Integer createNobelPrize(final @Nonnull NobelPrizeCreate nobelPrize) {
        // Insert the Nobel Prize
        Integer nobelPrizeId;
        try {
            nobelPrizeId = dsl.insertInto(NOBEL_PRIZE)
                .columns(
                    NOBEL_PRIZE.CATEGORYID,
                    NOBEL_PRIZE.YEAR,
                    NOBEL_PRIZE.URL,
                    NOBEL_PRIZE.CREATEDBYID,
                    NOBEL_PRIZE.LASTMODIFIEDBYID)
                .values(1, nobelPrize.getYear(), nobelPrize.getUrl(), 1, 1)
                .returningResult(NOBEL_PRIZE.ID)
                .fetch().getFirst().value1();

        } catch (IntegrityConstraintViolationException exception) {
            throw new EntityExistsException("This Nobel Prize already exists");
        }

        createNobelPrizeLaureates(nobelPrizeId, nobelPrize.getLaureates());

        return nobelPrizeId;
    }

    /**
     * Gets all Nobel Prizes, optionally filtered by year and category.
     *
     * @param year Year the Nobel Prize was awarded.
     * @param category Category in which the Nobel Prize was awarded.
     * @return All Nobel Prizes matching the supplied selection criteria.
     */
    @Nonnull
    public List<NobelPrize> getNobelPrizes(@Nullable final Integer year, @Nullable final String category) {
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

    private void createNobelPrizeLaureates(
            final @Nonnull Integer nobelPrizeId,
            final List<NobelPrizeLaureateCreate> laureates) {
        for (NobelPrizeLaureateCreate laureate : laureates) {
            Integer primaryKeyOfPerson = null;
            Integer primaryKeyOfOrganization = null;
            final String personIdentifier = laureate.getType().getPersonIdentifier();
            if (personIdentifier != null) {
                primaryKeyOfPerson = personService.getPrimaryKey(personIdentifier);
            }
            final String organizationIdentifier = laureate.getType().getOrganizationIdentifier();
            if (organizationIdentifier != null) {
                primaryKeyOfOrganization = organizationService.getPrimaryKey(organizationIdentifier);
            }
            createLaureate(laureate, nobelPrizeId, primaryKeyOfPerson, primaryKeyOfOrganization);
        }
    }

    /**
     * Creates a Nobel Prize laureate.
     *
     * @param laureate Nobel Prize laureate model.
     * @param nobelPrizeId Primary key of the corresponding Nobel Prize.
     * @param personId Primary key of the corresponding person.
     * @param organizationId Primary key of the corresponding organization.
     */
    private void createLaureate(
            @Nonnull final NobelPrizeLaureateCreate laureate,
            @Nonnull final Integer nobelPrizeId,
            @Nullable final Integer personId,
            @Nullable final Integer organizationId) {
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
                laureate.getDescription(),
                laureate.getFractionNominator(),
                laureate.getFractionDenominator(),
                1,
                1)
            .execute();
    }

}
