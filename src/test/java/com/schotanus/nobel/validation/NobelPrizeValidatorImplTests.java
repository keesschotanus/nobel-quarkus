package com.schotanus.nobel.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.schotanus.nobel.model.NobelPrizeCategoryEnum;
import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreateType;
import com.schotanus.nobel.util.NobelPrizeCreateBuilder;
import com.schotanus.nobel.util.NobelPrizeLaureateCreateBuilder;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;


/**
 * Tests {@link NobelPrizeValidatorImpl}.
 */
@QuarkusTest
class NobelPrizeValidatorImplTests {

    private static final NobelPrizeCategoryEnum PHYSICS = NobelPrizeCategoryEnum.P;
    private static final NobelPrizeCategoryEnum PEACE = NobelPrizeCategoryEnum.PC;
    private static final Integer YEAR = 1902;

    private final NobelPrizeValidatorImpl sut = new NobelPrizeValidatorImpl();

    private final ConstraintValidatorContext context = mock(ConstraintValidatorContextImpl.class);

    @Test
    void validLaureatesShouldPass() {
        final NobelPrizeLaureateCreateType type = NobelPrizeLaureateCreateBuilder.createLaureatePerson("1");
        final NobelPrizeLaureateCreate laureate = new NobelPrizeLaureateCreateBuilder(type, 1, 1).build();

        final NobelPrizeCreate nobelPrize = new NobelPrizeCreateBuilder(PHYSICS, YEAR, List.of(laureate)).build();

        assertTrue(sut.isValid(nobelPrize, null));

        // Test with three laureates
        final NobelPrizeLaureateCreateType type1 = NobelPrizeLaureateCreateBuilder.createLaureatePerson("1");
        final NobelPrizeLaureateCreateType type2 = NobelPrizeLaureateCreateBuilder.createLaureatePerson("2");
        final NobelPrizeLaureateCreateType type3 = NobelPrizeLaureateCreateBuilder.createLaureatePerson("3");
        final NobelPrizeLaureateCreate laureate1 = new NobelPrizeLaureateCreateBuilder(type1, 1, 3).build();
        final NobelPrizeLaureateCreate laureate2 = new NobelPrizeLaureateCreateBuilder(type2, 1, 3).build();
        final NobelPrizeLaureateCreate laureate3 = new NobelPrizeLaureateCreateBuilder(type3, 1, 3).build();

        nobelPrize.setLaureates(List.of(laureate1, laureate2, laureate3));
        assertTrue(sut.isValid(nobelPrize, null));
    }

    @Test
    void invalidFractionsShouldFail() {
        final NobelPrizeLaureateCreateType type = NobelPrizeLaureateCreateBuilder.createLaureatePerson("1");
        final NobelPrizeLaureateCreate laureate = new NobelPrizeLaureateCreateBuilder(type, 2, 1).build();

        final NobelPrizeCreate nobelPrize = new NobelPrizeCreateBuilder(PHYSICS, YEAR, List.of(laureate)).build();
        assertFalse(sut.isValid(nobelPrize, context));
    }

    @Test
    void nonUniqueLaureatesOfTypePersonShouldFail() {
        final NobelPrizeLaureateCreateType type = NobelPrizeLaureateCreateBuilder.createLaureatePerson("1");
        final NobelPrizeLaureateCreate laureate = new NobelPrizeLaureateCreateBuilder(type, 1, 2).build();

        final NobelPrizeCreate nobelPrize = new NobelPrizeCreateBuilder(PHYSICS, YEAR, List.of(laureate,
                laureate)).build();

        assertFalse(sut.isValid(nobelPrize, context));
    }

    @Test
    void nonUniqueLaureatesOfTypeOrganizationShouldFail() {
        final NobelPrizeLaureateCreateType type = NobelPrizeLaureateCreateBuilder.createLaureateOrganization("1");
        final NobelPrizeLaureateCreate laureate = new NobelPrizeLaureateCreateBuilder(type, 1, 2).build();

        final NobelPrizeCreate nobelPrize = new NobelPrizeCreateBuilder(PEACE, YEAR, List.of(laureate, laureate)).build();

        assertFalse(sut.isValid(nobelPrize, context));
    }

    @Test
    void laureateWithoutIdentifierShouldFail() {
        final NobelPrizeLaureateCreateType personType = NobelPrizeLaureateCreateBuilder.createLaureatePerson(null);
        final NobelPrizeLaureateCreate laureateOfTypePerson  = new NobelPrizeLaureateCreateBuilder(
                personType, 1, 1).build();

        final NobelPrizeCreate nobelPrizeOne = new NobelPrizeCreateBuilder(
                NobelPrizeCategoryEnum.P, YEAR, List.of(laureateOfTypePerson)).build();

        assertFalse(sut.isValid(nobelPrizeOne, context));

        final NobelPrizeLaureateCreateType organizationType = NobelPrizeLaureateCreateBuilder.createLaureateOrganization(null);
        final NobelPrizeLaureateCreate laureateOfTypeOrganization  = new NobelPrizeLaureateCreateBuilder(
                organizationType, 1, 1).build();

        final NobelPrizeCreate nobelPrizeTwo = new NobelPrizeCreateBuilder(
                NobelPrizeCategoryEnum.P, YEAR, List.of(laureateOfTypeOrganization)).build();
        assertFalse(sut.isValid(nobelPrizeTwo, context));
    }


    @Test
    void invalidNumberOfLaureatesShouldFail() {
        // Test with empty list of laureates
        final NobelPrizeCreate nobelPrize = new NobelPrizeCreateBuilder(PHYSICS, YEAR, Collections.emptyList()).build();
        assertFalse(sut.isValid(nobelPrize, context));

        // Too many
        final NobelPrizeLaureateCreateType type1 = NobelPrizeLaureateCreateBuilder.createLaureatePerson("1");
        final NobelPrizeLaureateCreateType type2 = NobelPrizeLaureateCreateBuilder.createLaureatePerson("2");
        final NobelPrizeLaureateCreateType type3 = NobelPrizeLaureateCreateBuilder.createLaureatePerson("3");
        final NobelPrizeLaureateCreateType type4 = NobelPrizeLaureateCreateBuilder.createLaureatePerson("4");

        final NobelPrizeLaureateCreate laureate1 = new NobelPrizeLaureateCreateBuilder(type1, 1, 4).build();
        final NobelPrizeLaureateCreate laureate2 = new NobelPrizeLaureateCreateBuilder(type2, 1, 4).build();
        final NobelPrizeLaureateCreate laureate3 = new NobelPrizeLaureateCreateBuilder(type3, 1, 4).build();
        final NobelPrizeLaureateCreate laureate4 = new NobelPrizeLaureateCreateBuilder(type4, 1, 4).build();

        nobelPrize.setLaureates(List.of(laureate1, laureate2, laureate3, laureate4));
        assertFalse(sut.isValid(nobelPrize, context));
    }

    @Test
    void organizationWinningPhysicsPrizeShouldFail() {
        final NobelPrizeLaureateCreateType type = NobelPrizeLaureateCreateBuilder.createLaureateOrganization("org");
        final NobelPrizeLaureateCreate laureate = new NobelPrizeLaureateCreateBuilder(type, 1, 1).build();
        final NobelPrizeCreate nobelPrize = new NobelPrizeCreateBuilder(PHYSICS, YEAR, List.of(laureate)).build();

        assertFalse(sut.isValid(nobelPrize, context));
    }

}
