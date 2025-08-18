package com.schotanus.nobel.util;

import com.schotanus.nobel.model.NobelPrizeLaureateCreate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreateType;


public class NobelPrizeLaureateCreateBuilder {
    // Required fields
    private final NobelPrizeLaureateCreateType type;
    private final Integer fractionNominator;
    private final Integer fractionDenominator;

    // Optional fields
    private String description;

    public NobelPrizeLaureateCreateBuilder(NobelPrizeLaureateCreateType type, Integer fractionNominator, Integer fractionDenominator) {
        this.type = type;
        this.fractionNominator = fractionNominator;
        this.fractionDenominator = fractionDenominator;
    }

    public NobelPrizeLaureateCreateBuilder description(String description) {
        this.description = description;
        return this;
    }

    public NobelPrizeLaureateCreate build() {
        NobelPrizeLaureateCreate laureate = new NobelPrizeLaureateCreate();
        laureate.setType(this.type);
        laureate.setFractionNominator(this.fractionNominator);
        laureate.setFractionDenominator(this.fractionDenominator);
        laureate.setDescription(this.description);
        return laureate;
    }

    public static NobelPrizeLaureateCreateType createLaureatePerson(final String identifier) {
        NobelPrizeLaureateCreateType laureateOfTypePerson = new NobelPrizeLaureateCreateType();
        laureateOfTypePerson.setPersonIdentifier(identifier);

        return laureateOfTypePerson;

    }

    public static NobelPrizeLaureateCreateType createLaureateOrganization(final String identifier) {
        NobelPrizeLaureateCreateType laureateOfTypeOrganization = new NobelPrizeLaureateCreateType();
        laureateOfTypeOrganization.setOrganizationIdentifier(identifier);

        return laureateOfTypeOrganization;

    }

}
