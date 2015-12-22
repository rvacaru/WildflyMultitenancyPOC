package com.raz.poc.multitenancy.test;

import com.raz.poc.multitenancy.util.FileUtils;
import com.raz.poc.multitenancy.Person;
import com.raz.poc.multitenancy.PersonProcessor;
import com.raz.poc.multitenancy.entitymanager.MultitenantEntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Load Test for Multitenancy solution: scales: 1.000 10.000 100.000
 * XXX Exception: DeploymentScenario contains targets not maching any defined
 * Container in the registry. _DEFAULT_ Solution: Select the maven profile
 * 'arquillian-jbossas-remote' (jboss server must be already started if using
 * this profile)
 *
 * XXX Enhance: Try to use more TestNG features, especially for cleanup and
 * parametrized tests 
 * XXX 2Days bug: To make surefire run the tests the Class
 * must be named *Test.java or Test*.java or *TestCase
 *
 * @author r.vacaru
 */
public class WildflyLoadTest extends Arquillian {

    @EJB
    PersonProcessor personProcessor;

    private static final int NO_DATASRC = 3;
    private static final String[] DATA_SOURCES = {"Manager1", "Manager2", "Manager3"};
    private static Duration testDuration, persistDuration, mergeDuration, selectRemoveDuration;

    private static final String fPath = "C:\\Users\\r.vacaru\\Documents\\NetBeansProjects\\WildFlyMultitenancy\\TestDurationsLong.txt";

    /*
     * XXX Arquillian cannot run multiple TestClasses each with its deployment. See:
     * http://stackoverflow.com/questions/27522734/unable-to-run-multiple-tests-with-arquillian
     * 
     */
//    @Deployment(testable = true)
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClasses(Person.class, MultitenantEntityManager.class, PersonProcessor.class, FileUtils.class)
                .addAsManifestResource("META-INF/beans.xml", "beans.xml") //to use for CDI beans
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml"); // to use for the EntityManager etc.
    }

    @Test(enabled = false)
    public void doTest() {
        FileUtils.write(fPath, "START LOAD TESTS SUITE---------------------------------------------" + LocalDateTime.now() + "\n\n");
        paramTest(5, "_1");
        FileUtils.write(fPath, "END   LOAD TESTS SUITE---------------------------------------------" + LocalDateTime.now() + "\n\n");
    }

    private void paramTest(int scale, String name) {
        FileUtils.write(fPath, "Start of Test" + name + " " + LocalDateTime.now() + "\n");
        Person[][] persons = createPersonsArray(scale);
        Instant startTest = Instant.now();

        Instant startPersist = Instant.now();
        persistPersons(persons, scale);
        Instant endPersist = Instant.now();
        myAssertEqualsNoOrder(persons);

        Instant startMerge = Instant.now();
        mergePersons(persons, scale);
        Instant endMerge = Instant.now();
        myAssertEqualsNoOrder(persons);

        Instant startSelect = Instant.now();
        selectAndRemoveAllPersons();
        Instant endSelect = Instant.now();
        myAssertDBAreEmpty();

        Instant endTest = Instant.now();
        testDuration = Duration.between(startTest, endTest);
        persistDuration = Duration.between(startPersist, endPersist);
        mergeDuration = Duration.between(startMerge, endMerge);
        selectRemoveDuration = Duration.between(startSelect, endSelect);

        FileUtils.write(fPath, String.format("Duration of PERSIST T%s:   [ %d minutes, %d secs, %d millis ]\n", name,
                persistDuration.toMinutes(), persistDuration.getSeconds(), persistDuration.toMillis()));
        FileUtils.write(fPath, String.format("Duration of MERGE T%s:     [ %d minutes, %d secs, %d millis ]\n", name,
                mergeDuration.toMinutes(), mergeDuration.getSeconds(), mergeDuration.toMillis()));
        FileUtils.write(fPath, String.format("Duration of SEL_DEL T%s:   [ %d minutes, %d secs, %d millis ]\n", name,
                selectRemoveDuration.toMinutes(), selectRemoveDuration.getSeconds(), selectRemoveDuration.toMillis()));
        FileUtils.write(fPath, String.format("Duration of TOTAL T%s:     [ %d minutes, %d secs, %d millis ]\n", name,
                testDuration.toMinutes(), testDuration.getSeconds(), testDuration.toMillis()));
        FileUtils.write(fPath, String.format("End of Test%s, Scale: %d, %s  \n\n", name, scale, LocalDateTime.now()));
    }

    private Person[][] createPersonsArray(int scale) {
        Person[][] persons = new Person[NO_DATASRC][scale];
        for (int j = 0; j < NO_DATASRC; j++) {
            for (int i = 0; i < scale; i++) {
                persons[j][i] = new Person("persistPersonName_" + j + i, Integer.valueOf("" + j + i));
            }
        }
        return persons;
    }

    private void persistPersons(Person[][] persons, int scale) {
        for (int j = 0; j < NO_DATASRC; j++) {
            for (int i = 0; i < scale; i++) {
                personProcessor.perist(DATA_SOURCES[j], persons[j][i]);
            }
        }
    }

    private void mergePersons(Person[][] persons, int scale) {
        String mergedName = "mergedName_";
        for (int j = 0; j < NO_DATASRC; j++) {
            for (int i = 0; i < scale; i++) {
                persons[j][i].setName(mergedName + j + i);
                personProcessor.merge(DATA_SOURCES[j], persons[j][i]);
            }
        }
    }

    private void selectAndRemoveAllPersons() {
        for (String datasrc : DATA_SOURCES) {
            List<Person> persons = (List<Person>) personProcessor.selectAll(datasrc);
            persons.forEach(p -> personProcessor.remove(datasrc, p)); //lamda expr! =P
        }
    }

    private void myAssertEqualsNoOrder(Person[][] persons) {
        for (int j = 0; j < NO_DATASRC; j++) {
            assertEqualsNoOrder(persons[j], personProcessor.selectAll(DATA_SOURCES[j]).toArray());
        }
    }

    private void myAssertDBAreEmpty() {
        for (int j = 0; j < NO_DATASRC; j++) {
            assertTrue(personProcessor.selectAll(DATA_SOURCES[j]).isEmpty());
        }
    }

    /**
     * XXX @Before* methods are executed twice: both in and out of the container. See:
     * http://stackoverflow.com/questions/6817674/testng-injection-fails-when-using-any-before-annotation-but-works-without
     * https://issues.jboss.org/browse/ARQ-104
     */
    @BeforeTest
    private void cleanUpAllDataSources() {
        if (personProcessor != null) { //without this u get a NPE when it's executed out of the container
            for (String datasrc : DATA_SOURCES) {
                personProcessor.removeAll(datasrc);
            }
            System.out.println("@BeforeTest -> CleanUp Performed =)");
        }
    }
}
