package com.schotanus.nobel.validation;

import com.schotanus.nobel.model.NobelPrizeCategoryEnum;
import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Validator for Nobel Prizes.
 * A Nobel Prize is valid when:<br>
 * - the sum of the fractions is 1.
 * - the laurates are unique.
 * - all laureates are persons (except for the Nobel Peace Prize)
 * - the number of laureates is either 1, 2 or 3
 * After the above checks are passed, it is still possible that this valid Nobel Prize can't be stored:
 * - personIdentifier may not refer to existing person
 * - organizationIdentifier may not refer to existing organization
 * - the Nobel Prize may already exist.
 * Most of these situations will be handled by the repository class.
 */
@ApplicationScoped
public class NobelPrizeValidatorImpl implements ConstraintValidator<NobelPrizeValidator, NobelPrizeCreate>
{
    private static final String MESSAGE = "message";

    /**
     * Determines if the supplied Nobel Prize is valid (see documentation above).
     *
     * @param nobelPrize The Nobel Prize to validate.
     * @param context Context in which the constraint is evaluated.
     *
     * @return True when valid, otherwise false.
     */
    @Override
    public boolean isValid(final NobelPrizeCreate nobelPrize, final ConstraintValidatorContext context)
    {
        return validateFractions(nobelPrize, context)
            && validateLaureatesAreUniquePersons(nobelPrize, context)
            && validateNumberOfLaureates(nobelPrize, context);
    }

    /**
     * Validate that the sum of the Nobel Prize fractions is 1.
     *
     * @param nobelPrize The Nobel Prize to validate.
     * @param context Context in which the constraint is evaluated.
     * @return True when valid, otherwise false.
     */
    private boolean validateFractions(final NobelPrizeCreate nobelPrize, final ConstraintValidatorContext context) {
        boolean result = true;

        int nominatorTotal = 0;
        int denominatorTotal = 1;

        for (NobelPrizeLaureateCreate laureate : nobelPrize.getLaureates()) {
            int nominator = laureate.getFractionNominator();
            int denominator = laureate.getFractionDenominator();

            nominatorTotal = nominatorTotal * denominator + denominatorTotal * nominator;
            denominatorTotal *= denominator;
        }

        if (nominatorTotal != denominatorTotal) {
            ((ConstraintValidatorContextImpl)context).addMessageParameter(MESSAGE, "Sum of fractions does not add up to 1");
            result = false;
        }

        return result;
    }

    /**
     * Validate that all laureates are persons and each person is unique.
     * The check is not performed for Nobel Peace Prizes since they can be awarded to organizations.
     *
     * @param nobelPrize The Nobel Prize to validate.
     * @param context Context in which the constraint is evaluated.
     * @return True when valid, otherwise false.
     */
    private boolean validateLaureatesAreUniquePersons(final NobelPrizeCreate nobelPrize,
            final ConstraintValidatorContext context) {

        if (nobelPrize.getCategory() == NobelPrizeCategoryEnum.PC) {
            return true;
        }

        final List<NobelPrizeLaureateCreate> laureates = nobelPrize.getLaureates();
        // USe a set to filter duplicates
        Set<String> uniqueLaureates = HashSet.newHashSet(laureates.size());

        for (NobelPrizeLaureateCreate laureate : laureates) {
            String personIdentifier = laureate.getType().getPersonIdentifier();
            if (personIdentifier == null) {
                ((ConstraintValidatorContextImpl)context).addMessageParameter(
                        MESSAGE, "Organizations can't win this Nobel Prize");
                return false;
            }
            uniqueLaureates.add(laureate.getType().getPersonIdentifier());
        }

        if (laureates.size() != uniqueLaureates.size()) {
            ((ConstraintValidatorContextImpl)context).addMessageParameter(
                    MESSAGE, "Duplicate laureate found");
            return false;
        }

        return true;
    }

    /**
     * Validate that the number of laureates is 1,2 or 3.
     *
     * @param nobelPrize The Nobel Prize to validate.
     * @param context Context in which the constraint is evaluated.
     * @return True when valid, otherwise false.
     */
    private boolean validateNumberOfLaureates(final NobelPrizeCreate nobelPrize,
            ConstraintValidatorContext context) {
        int numberOfLaureates = 0;

        for (NobelPrizeLaureateCreate laureate : nobelPrize.getLaureates()) {
            if (laureate.getType().getPersonIdentifier() != null) {
                ++numberOfLaureates;
            }
            if (laureate.getType().getOrganizationIdentifier() != null) {
                ++numberOfLaureates;
            }
        }

        if (numberOfLaureates < 1 || numberOfLaureates > 3) {
            ((ConstraintValidatorContextImpl)context).addMessageParameter(
                    MESSAGE, "Number of laureates must be either 1, 2 or 3");
            return false;
        }

        return true;

    }


}
