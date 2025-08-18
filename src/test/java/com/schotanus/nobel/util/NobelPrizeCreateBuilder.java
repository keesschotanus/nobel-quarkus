package com.schotanus.nobel.util;

import com.schotanus.nobel.model.NobelPrizeCategoryEnum;
import com.schotanus.nobel.model.NobelPrizeCreate;
import com.schotanus.nobel.model.NobelPrizeLaureateCreate;

import java.util.List;


public class NobelPrizeCreateBuilder {
    // Required fields
    private final NobelPrizeCategoryEnum category;
    private final Integer year;
    private final List<NobelPrizeLaureateCreate> laureates;

    // Optional fields
    private String url;

    public NobelPrizeCreateBuilder(NobelPrizeCategoryEnum category, Integer year, List<NobelPrizeLaureateCreate> laureates) {
        this.category = category;
        this.year = year;
        this.laureates = List.copyOf(laureates);
    }

    public NobelPrizeCreateBuilder url(String url) {
        this.url = url;
        return this;
    }

    public NobelPrizeCreate build() {
        NobelPrizeCreate nobelPrize = new NobelPrizeCreate();
        nobelPrize.setCategory(category);
        nobelPrize.setYear(year);
        nobelPrize.setLaureates(laureates);

        return nobelPrize;
    }

}
