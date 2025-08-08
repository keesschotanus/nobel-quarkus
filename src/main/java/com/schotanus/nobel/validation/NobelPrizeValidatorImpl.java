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
 * - each fraction of the Nobel Prize the laureate gets is valid.<br>
 * - the sum of the fractions is 1.
 *
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

        for (NobelPrizeLaureateCreate nobelPrizeLaureate : nobelPrize.getLaureates()) {
            int nominator = nobelPrizeLaureate.getFractionNominator();
            int denominator = nobelPrizeLaureate.getFractionDenominator();

            if (!isFractionValid(nominator, denominator)) {
                ((ConstraintValidatorContextImpl)context).addMessageParameter(
                        "message", "Illegal fraction: " + nominator + "/" + denominator);
                return false;
            }

            // Compute total as fractions of 1/6th. 1/1=6/6, 1/2=3/6, 1/3=2/6, 2/3=4/6
            nominatorTotal += nominator * 6 / denominator;
        }

        // Since we use fractions of 1/6th, the total numerator value must be 6
        if (nominatorTotal != 6) {
            ((ConstraintValidatorContextImpl)context).addMessageParameter("message", "Sum of fractions does not add up to 1");
            return false;
        }

        return true;
    }

   /**
    * Check if the nominator and denominator are valid.
    * The following combinations are valid: 1/1, 1/2, 1/3 and 2/3.
    * @param nominator Nominator of the fraction of the Nobel Prize.
    * @param denominator Denominator of the fraction of the Nobel Prize.
    * @return True when nominator and denominator represent a valid fraction.
    */
    private boolean isFractionValid(final int nominator, final int denominator) {
        return (nominator == 1 && denominator >= 1 && denominator <= 3) || (nominator == 2 && denominator == 3);
    }

}
