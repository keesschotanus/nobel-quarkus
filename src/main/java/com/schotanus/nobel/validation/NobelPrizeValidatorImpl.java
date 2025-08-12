package com.schotanus.nobel.validation;

import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;


/**
 * Validator for Nobel Prizes.
 * A Nobel Prize is valid when:<br>
 * - the sum of the fractions is 1.
 * Other criteria exist like:
 * - The laurate needs to be unique.
 * - In science only persons can win
 * These criteria are not checked here yet.
 */
@ApplicationScoped
public class NobelPrizeValidatorImpl implements ConstraintValidator<NobelPrizeValidator, NobelPrizeCreate>
{

    /**
     * Determines if the supplied Nobel Prize is valid (see documentation above).
     * @param nobelPrize The Nobel Prize to validate.
     * @param context Context in which the constraint is evaluated.
     *
     * @return True when valid, otherwise false.
     */
    @Override
    public boolean isValid(final NobelPrizeCreate nobelPrize, final ConstraintValidatorContext context)
    {
        int nominatorTotal = 0;
        int denominatorTotal = 1;

        for (NobelPrizeLaureateCreate nobelPrizeLaureate : nobelPrize.getLaureates()) {
            int nominator = nobelPrizeLaureate.getFractionNominator();
            int denominator = nobelPrizeLaureate.getFractionDenominator();

            nominatorTotal = nominatorTotal * denominator + denominatorTotal * nominator;
            denominatorTotal *= denominator;
        }

        if (nominatorTotal != denominatorTotal) {
            ((ConstraintValidatorContextImpl)context).addMessageParameter("message", "Sum of fractions does not add up to 1");
            return false;
        }

        return true;
    }

}
