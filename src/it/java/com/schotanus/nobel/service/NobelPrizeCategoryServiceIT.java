package com.schotanus.nobel.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.schotanus.nobel.api.NobelPrizeCategoryApiImpl;
import com.schotanus.nobel.model.NobelPrizeCategoryEnum;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link NobelPrizeCategoryService} methods that are not part of {@link NobelPrizeCategoryApiImpl}.
 */
@QuarkusTest
class NobelPrizeCategoryServiceIT {

    private final NobelPrizeCategoryService service;

    NobelPrizeCategoryServiceIT(NobelPrizeCategoryService service) {
            this.service = service;
        }

    /**
     * Tests {@link NobelPrizeCategoryService#getPrimaryKey(NobelPrizeCategoryEnum)}.
     */
    @Test
    void getPrimaryKeyOfExistingCategoryShouldPass() {
        assertNotNull(service.getPrimaryKey(NobelPrizeCategoryEnum.P));
    }

}
