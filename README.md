# nobel

This is my personal project to get hands-on experience with:
- Quarkus
- Liquibase
- jOOQ
- PostgreSQL
- Maven

In the end the code allows you to maintain Nobel Prize laureates using REST services.

## Project creation
- The project was started by using [https://code.quarkus.io/](https://code.quarkus.io/)
- The following data was entered
- Group: com.schotanus.nobel
- Artifact: nobel
- Build Tool: Maven
- Version: 1.0.0-SNAPSHOT
- Java Version: 21
- Starter Code: Yes
- Select the "REST service with database" extensions preset
- Deselect "Hibernate ORM with Panache"
- Select Quarkus JOOQ - Runtime
- Select Liquibase
- Click "Generate your application"

Now download the generated zip file and extract it into your projects folder (~/projects/nobel in my case).

Add the following property to your src/main/resources/application.properties file
```quarkus.jooq.dialect=postgresql```
provided that you are using postgresql.

You should now be able to run the application using the command:
```./mvnw compile quarkus:dev```
  
## Project configuration

Create the nobel database, for example using a tool like DBeaver.
Now update your application.properties file to look like this:
```text
# jOOQ configuration
quarkus.jooq.dialect=postgres

# Liquibase minimal config properties
quarkus.liquibase.migrate-at-start=true

# Database configuration
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=nobel
quarkus.datasource.password=nobel
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/nobel
```
Make sure to change these properties depending on the way you created the database.

### Project configuration - Create empty Liquibase changeset
Liquibase has been configured above, but it needs a src/main/resources/db/changeLog.xml file.
Create the folder and file and get the content for it from
[here](https://docs.liquibase.com/concepts/changelogs/xml-format.html).
Copy the example code and remove all changeSets.


## Project configuration - Running the project
At this point you should be able to run the application using the command:

```bash
./mvnw compile quarkus:dev
```

### Project configuration - Liquibase

The database has to be created using
[https://docs.liquibase.com/concepts/changelogs/changeset.html](Liquibase Changesets)

See [changeLog.xml](src/main/resources/db/changeLog.xml) for the result.
With the changeSets in place, run the application, so Liquibase can create the database tables.
As always, you can run the application with:
```bash
./mvnw clean compile quarkus:dev
```

After running the application, you can check the database to see if the tables have been created.
You can also check the ```DATABASECHANGELOG``` table to see if the changeSets have been applied.

### Project configuration - jOOQ code generation

Code generation by jOOQ is explained
[here](https://www.jooq.org/doc/latest/manual/code-generation/codegen-execution/codegen-maven/) and
[here](https://www.jooq.org/doc/latest/manual/code-generation/).

To generate code some changes to the pom.xml have to be made.
See the [pom.xml](pom.xml) file and look for ```<artifactId>jooq-codegen-maven</artifactId>```

As you will see, this plugin uses some properties.
These properties should be stored in a .env file that should not be committed.
Here is an example of the content of the .env file
```text
jdbc.url=jdbc:postgresql://localhost:5432/nobel
jdbc.user=nobel
jdbc.password=nobel
```

## Run the project

At this stage you should be able to run the project.
It should use Liquibase to generate the tables and it should generate jOOQ code.

To generate code and then run the project, execute:
```bash
./mvnw clean generate-sources
./mvnw quarkus:dev
```

At this stage I committed the code and reflected upon my initial setup.

# Reviewing my initial setup and configuration

I made a couple of errors.
In my changeLog.xml file I had used the plural form of database table names,
since it is most natural to "SELECT * FROM PERSONS" instead of selecting from a single person.
Unfortunately this resulted in all generated class names having a plural form, which is not done.

My second error was that I thought that jOOQ would create model files, with nested components.
So a Person model would have a nested address for example.
It turns out that this is not the case so I ended up using [OpenAPI](https://swagger.io/specification/).

I created a specs file named [nobel.yml](src/main/resources/nobel.yml).
It also required some changes to the [pom.xml](pom.xml) file.

My third error was using the Maven properties plugin.
It just did not work so I removed it for now.

Finally, I had a problem with Quarkus in combination with Liquibase.
When running Quarkus in de dev mode, the Liquibase scripts were executed twice,
but somehow differently, resulting in errors that tables already existed.
To fix this I updated my application.properties file like this:
```text
quarkus.liquibase.migrate-at-start=false
```

Since I corrected the order of the build plugin in my pom.xml file, you can now simply execute:
```bash
mvn quarkus:dev
```

You can still use the following commands:
```bash
# Run liquibase change sets, generate sources, compile
mvn clean compile
# Run Liquibase change sets
mvn liquibase:update 
```





# Below is the documentation generated by the Quarkus starter project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/nobel-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- Liquibase ([guide](https://quarkus.io/guides/liquibase)): Handle your database schema migrations with Liquibase
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
