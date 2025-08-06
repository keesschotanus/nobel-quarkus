package com.schotanus.nobel.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Nobel Prize validator annotation.
 * @see NobelPrizeValidatorImpl
 */
@Target({ElementType.PARAMETER, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = NobelPrizeValidatorImpl.class)
public @interface NobelPrizeValidator {

    String message() default "Invalid Nobel Prize: {message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
