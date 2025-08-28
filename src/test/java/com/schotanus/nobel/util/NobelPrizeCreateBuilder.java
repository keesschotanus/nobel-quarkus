package com.schotanus.nobel.util;

import com.schotanus.nobel.model.NobelPrizeCategoryEnum;
import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreate;
import jakarta.validation.Valid;

import java.util.List;


public class NobelPrizeCreateBuilder {
    // Required fields
    private final NobelPrizeCategoryEnum category;
    private final Integer year;
    private final @Valid List<@Valid NobelPrizeLaureateCreate> laureates;

    public NobelPrizeCreateBuilder(NobelPrizeCategoryEnum category, Integer year,
            List<@Valid NobelPrizeLaureateCreate> laureates) {
        this.category = category;
        this.year = year;
        this.laureates = List.copyOf(laureates);
    }

    public NobelPrizeCreate build() {
        NobelPrizeCreate nobelPrize = new NobelPrizeCreate();
        nobelPrize.setCategory(category);
        nobelPrize.setYear(year);
        nobelPrize.setLaureates(laureates);

        return nobelPrize;
    }

}
