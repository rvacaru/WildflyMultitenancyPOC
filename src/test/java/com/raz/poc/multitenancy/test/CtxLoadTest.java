package com.raz.poc.multitenancy.test;

import com.raz.poc.multitenancy.ContextHolder;
import com.raz.poc.multitenancy.CtxPersonProcessor;
import com.raz.poc.multitenancy.Person;
import com.raz.poc.multitenancy.Tenant;
import com.raz.poc.multitenancy.entitymanager.TenantCtxEntityManager;
import com.raz.poc.multitenancy.util.FileUtils;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author r.vacaru
 */
public class CtxLoadTest extends Arquillian {

    @EJB
    CtxPersonProcessor ctxPersonProcessor;

    @Inject
    ContextHolder ctxHolder;

    private static final int NO_DATASRC = 3;
    private static final String CURR_TENANT = "currentTenant";
    private static final Tenant[] TENANTS = {new Tenant("ten1", "java:app/entitymanager/Manager1"),
                                             new Tenant("ten1", "java:app/entitymanager/Manager2"),
                                             new Tenant("ten1", "java:app/entitymanager/Manager3"),};
    private static Duration testDuration, persistDuration, mergeDuration, selectRemoveDuration;

    private static final String LOG_FILE = "C:\\Users\\r.vacaru\\Documents\\NetBeansProjects\\WildFlyMultitenancy\\TestDurationsCTX.txt";

    @Deployment(testable = true)
    public static Archive<?> createDeploymentPackage() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClasses(Person.class, Tenant.class, TenantCtxEntityManager.class, CtxPersonProcessor.class, ContextHolder.class, FileUtils.class)
                .addAsManifestResource("META-INF/beans.xml", "beans.xml") //to use for CDI beans
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml"); // to use for the EntityManager etc.
    }
    
    @Test(enabled = true, expectedExceptions = RuntimeException.class)
    public void testNoContextData(){
        //No tenant is stored in Context Data map
        ctxHolder.getContextData().remove(CURR_TENANT);
        ctxPersonProcessor.perist(null);
    }

    @Test(enabled = true)
    public void doTest() {
        FileUtils.write(LOG_FILE, "START LOAD TESTS SUITE---------------------------------------------" + LocalDateTime.now() + "\n\n");
        paramTest(5, "_1");
        FileUtils.write(LOG_FILE, "END   LOAD TESTS SUITE---------------------------------------------" + LocalDateTime.now() + "\n\n");
    }

    private void paramTest(int scale, String name) {
        FileUtils.write(LOG_FILE, "Start of Test" + name + " " + LocalDateTime.now() + "\n");
 
//        cleanUpAllDataSources();
        Person[][] persons = createPersonsArray(scale);
        Instant startTest = Instant.now();

        Instant startPersist = Instant.now();
        persistPersons(persons, scale);
        Instant endPersist = Instant.now();
        //myAssertEqualsNoOrder(persons);

        Instant startMerge = Instant.now();
        mergePersons(persons, scale);
        Instant endMerge = Instant.now();
        //myAssertEqualsNoOrder(persons);

        Instant startSelect = Instant.now();
        selectAndRemoveAllPersons();
        Instant endSelect = Instant.now();
        //myAssertDBAreEmpty();

        Instant endTest = Instant.now();
        testDuration = Duration.between(startTest, endTest);
        persistDuration = Duration.between(startPersist, endPersist);
        mergeDuration = Duration.between(startMerge, endMerge);
        selectRemoveDuration = Duration.between(startSelect, endSelect);

        FileUtils.write(LOG_FILE, String.format("Duration of PERSIST T%s:   [ %d minutes, %d secs, %d millis ]\n", name,
                persistDuration.toMinutes(), persistDuration.getSeconds(), persistDuration.toMillis()));
        FileUtils.write(LOG_FILE, String.format("Duration of MERGE T%s:     [ %d minutes, %d secs, %d millis ]\n", name,
                mergeDuration.toMinutes(), mergeDuration.getSeconds(), mergeDuration.toMillis()));
        FileUtils.write(LOG_FILE, String.format("Duration of SEL_DEL T%s:   [ %d minutes, %d secs, %d millis ]\n", name,
                selectRemoveDuration.toMinutes(), selectRemoveDuration.getSeconds(), selectRemoveDuration.toMillis()));
        FileUtils.write(LOG_FILE, String.format("Duration of TOTAL T%s:     [ %d minutes, %d secs, %d millis ]\n", name,
                testDuration.toMinutes(), testDuration.getSeconds(), testDuration.toMillis()));
        FileUtils.write(LOG_FILE, String.format("End of Test%s, Scale: %d, %s  \n\n", name, scale, LocalDateTime.now()));
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
            ctxHolder.putOrReplace(CURR_TENANT, TENANTS[j]);
            for (int i = 0; i < scale; i++) {
                ctxPersonProcessor.perist(persons[j][i]);
            }
        }
    }

    private void mergePersons(Person[][] persons, int scale) {
        String mergedName = "mergedName_";
        for (int j = 0; j < NO_DATASRC; j++) {
            ctxHolder.putOrReplace(CURR_TENANT, TENANTS[j]);
            for (int i = 0; i < scale; i++) {
                persons[j][i].setName(mergedName + j + i);
                ctxPersonProcessor.merge(persons[j][i]);
            }
        }
    }

    private void selectAndRemoveAllPersons() {
        for (Tenant t : TENANTS) {
            ctxHolder.putOrReplace(CURR_TENANT, t);
            List<Person> persons = (List<Person>) ctxPersonProcessor.selectAll();
            persons.forEach(p -> ctxPersonProcessor.remove(p)); //lamda expr! =P
        }
    }

    private void myAssertEqualsNoOrder(Person[][] persons) {
        for (int j = 0; j < NO_DATASRC; j++) {
            ctxHolder.putOrReplace(CURR_TENANT, TENANTS[j]);
            assertEqualsNoOrder(persons[j], ctxPersonProcessor.selectAll().toArray());
        }
    }

    private void myAssertDBAreEmpty() {
        for (Tenant t : TENANTS) {
            ctxHolder.putOrReplace(CURR_TENANT, t);
            assertTrue(ctxPersonProcessor.selectAll().isEmpty());
        }
    }

    /**
     * XXX @Before* methods are executed twice: both in and out of the container. See:
     * http://stackoverflow.com/questions/6817674/testng-injection-fails-when-using-any-before-annotation-but-works-without
     * https://issues.jboss.org/browse/ARQ-104
     */
    
    @BeforeClass
    private void cleanUpAllDataSources() {
        if (ctxPersonProcessor != null) { //without this check you get a NPE when it's executed out of the container
            for (Tenant t : TENANTS) {
                ctxHolder.putOrReplace(CURR_TENANT, t);
                ctxPersonProcessor.removeAll();
            }
            System.out.println("@BeforeTest -> CleanUp Performed =)");
        }
    }
}
