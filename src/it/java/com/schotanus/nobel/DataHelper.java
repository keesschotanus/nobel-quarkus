package com.schotanus.nobel;


import static com.schotanus.nobel.tables.Organization.ORGANIZATION;
import static com.schotanus.nobel.tables.Person.PERSON;

import jakarta.enterprise.context.ApplicationScoped;
import org.jooq.DSLContext;


/**
 * Data helper class to clean up test data.
 */
@ApplicationScoped
public class DataHelper {
    private final DSLContext dsl;

    public DataHelper(DSLContext dsl) {
        this.dsl = dsl;
    }

    public void deletePersonsWithTestIdentifiers() {
        dsl.delete(Tables.PERSON).where(PERSON.PERSONIDENTIFIER.like("test%")).execute();
    }

    public void deleteOrganizationsWithTestIdentifiers() {
        dsl.delete(Tables.ORGANIZATION).where(ORGANIZATION.ORGANIZATIONIDENTIFIER.like("test%")).execute();
    }

}
