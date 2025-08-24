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
 * - the laurates are unique.
 * - all laureates are persons (except for the Nobel Peace Prize)
 * - the number of laureates is either 1, 2 or 3
 * - the sum of the fractions is 1.
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
        return validateLaureates(nobelPrize, context) && validateFractions(nobelPrize, context);
    }

    /**
     * Validate that all laureates are unique, are of the proper type and have proper cardinality
     *
     * @param nobelPrize The Nobel Prize to validate.
     * @param context Context in which the constraint is evaluated.
     * @return True when valid, otherwise false.
     */
    private boolean validateLaureates(final NobelPrizeCreate nobelPrize, final ConstraintValidatorContext context) {
        final List<NobelPrizeLaureateCreate> laureates = nobelPrize.getLaureates();
        // Use a set to filter duplicates
        Set<String> uniqueLaureates = HashSet.newHashSet(laureates.size());

        for (NobelPrizeLaureateCreate laureate : laureates) {
            if (!validateLaureate((ConstraintValidatorContextImpl)context, nobelPrize.getCategory(),
                    laureate)) {
                return false;
            }

            String personIdentifier = laureate.getType().getPersonIdentifier();
            uniqueLaureates.add(personIdentifier != null ? personIdentifier : laureate.getType().getOrganizationIdentifier());
        }

        if (laureates.size() != uniqueLaureates.size()) {
            ((ConstraintValidatorContextImpl)context).addMessageParameter(
                    MESSAGE, "Duplicate laureates found");
            return false;
        }

        if (uniqueLaureates.isEmpty() || uniqueLaureates.size() > 3) {
            ((ConstraintValidatorContextImpl)context).addMessageParameter(MESSAGE, "# of laureates must be 1 to 3");
            return false;
        }

        return true;
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
     * Validates a single laureate.
     * A single laureate should have a single type and only organizations can win the Nobel Peace Prize.
     *
     * @param context Context in which the constraint is evaluated.
     * @param category The Nobel Prize category.
     * @param laureate The Nobel Prize laureate.
     * @return True when the laureate is valid, otherwise false.
     */
    private static boolean validateLaureate(ConstraintValidatorContextImpl context,
            NobelPrizeCategoryEnum category, NobelPrizeLaureateCreate laureate) {
        String personIdentifier = laureate.getType().getPersonIdentifier();
        String organizationIdentifier = laureate.getType().getOrganizationIdentifier();

        if (personIdentifier == null && organizationIdentifier == null) {
            context.addMessageParameter(MESSAGE, "Nobel Prize has no laureate");
            return false;
        }

        if (organizationIdentifier != null && NobelPrizeCategoryEnum.PC != category) {
            context.addMessageParameter(MESSAGE, "Organizations can only win the Nobel Peace Prize");
            return false;
        }

        return true;
    }

}
