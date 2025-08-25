package com.schotanus.nobel.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.schotanus.nobel.api.PersonApiImpl;
import com.schotanus.nobel.model.Person;
import com.schotanus.nobel.util.PersonBuilder;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link PersonService} methods that are not part of {@link PersonApiImpl}.
 */
@QuarkusTest
class PersonServiceIT {

    private final PersonService service;

    PersonServiceIT(PersonService service) {
            this.service = service;
        }

    /**
     * Tests {@link PersonService#getPrimaryKey(String)}
     */
    @Test
    void getPrimaryKeyOfExistingPersonShouldPass() {
        final Person person = new PersonBuilder().build();
        service.createPerson(person);

        assertNotNull(service.getPrimaryKey(person.getPersonIdentifier()));
    }

    /**
     * Tests {@link PersonService#getPrimaryKey(String)}
     */
    @Test
    void getPrimaryKeyOfNonExistingShouldFail() {
        assertThrows(NotFoundException.class, () -> service.getPrimaryKey("unknown primary key"));
    }
}
