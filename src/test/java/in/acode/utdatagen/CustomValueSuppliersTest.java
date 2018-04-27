package in.acode.utdatagen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import in.acode.utdatagen.datasources.MySQLDataSource;
import in.acode.utdatagen.suppliers.DateTimeSuppliers;
import in.acode.utdatagen.suppliers.IntSuppliers;
import in.acode.utdatagen.suppliers.StringSuppliers;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(classes = {MySQLDataSource.class})
public class CustomValueSuppliersTest {

    @Autowired
    @Qualifier("mysql-jdbc-template")
    private JdbcTemplate jdbcTemplate;

    DBTableFixture testTableFixture;

    @Before
    public void setup() {
        testTableFixture = DBTableFixture.getInstance("test_table", jdbcTemplate);
    }

    /**
     * Demo for using standard built-in value suppliers
     */
    @Test
    public void shouldBeAbleToUseBuiltInColumnValueSuppliers() {
        testTableFixture.truncateTable();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime aYearAgo = now.minusYears(1);

        //Specify insertion criteria using built-in value suppliers
        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(2)
            .withCondition("varchar_column", StringSuppliers.random(5, 10))
            .withCondition("timestamp_column", DateTimeSuppliers.random(aYearAgo, now))
            .withCondition("int_column", IntSuppliers.multipleOf(5, 10, 50));

        testTableFixture.insertRows(insertionCriteria);

        List<Map<String, Object>> allRows = testTableFixture.getAllRows("int_column ASC");
        assertEquals(2, allRows.size());

        //Make sure that the values satisfy the criteria we set
        for (Map<String, Object> row : allRows) {
            String varcharColValue = (String) row.get("varchar_column");
            assertTrue(varcharColValue.length() >= 5);
            assertTrue(varcharColValue.length() <= 10);

            LocalDateTime timestampColValue = ((Timestamp) row.get("timestamp_column")).toLocalDateTime();
            assertTrue(timestampColValue.compareTo(aYearAgo) >= 0);
            assertTrue(timestampColValue.compareTo(now) <= 0);

            int intColValue = (int) row.get("int_column");
            assertTrue(intColValue % 5 == 0);
            assertTrue(intColValue >= 10);
            assertTrue(intColValue <= 50);
        }
    }

    /**
     * Demo for building a simple custom value supplier
     */
    @Test
    public void shouldBeAbleToDefineSimpleCustomValueSuppliers() {
        testTableFixture.truncateTable();

        //Build an integer supplier which supplies a random value between 0 and 100
        Supplier<Integer> randomValueSupplier = () -> new Random().nextInt(100);

        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(2)
            .withCondition("int_column", randomValueSupplier);

        testTableFixture.insertRows(insertionCriteria);

        List<Map<String, Object>> allRows = testTableFixture.getAllRows();
        assertEquals(2, allRows.size());

        //Make sure that the values satisfy the criteria we set
        for (Map<String, Object> row : allRows) {
            int intColValue = (int) row.get("int_column");
            assertTrue(intColValue >= 0);
            assertTrue(intColValue < 100);
        }
    }

    /**
     * Demo for building a simple custom value supplier that uses the index of the row being inserted
     */
    @Test
    public void shouldBeAbleToDefineCustomValueSuppliersWhichUseRowIndex() {
        testTableFixture.truncateTable();

        //Build a function that returns a negative value followed by a positive value
        Function<Integer, Object> alternatingValueSupplier = (rowIndex) -> {
            int i = new Random().nextInt(100);
            if (rowIndex % 2 == 0) {
                return i;
            } else {
                return i * -1;
            }
        };

        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(4)
            .withCondition("int_column", alternatingValueSupplier);

        testTableFixture.insertRows(insertionCriteria);

        List<Map<String, Object>> allRows = testTableFixture.getAllRows("id ASC");
        assertEquals(4, allRows.size());

        //Make sure that the values satisfy the criteria we set
        for (int i = 0; i < allRows.size(); i++) {
            Map<String, Object> row = allRows.get(i);
            int intColValue = (int) row.get("int_column");

            if (i % 2 == 0) {
                assertTrue(intColValue >= 0);
            } else {
                assertTrue(intColValue <= 0);
            }
        }
    }

    /**
     * Demo for building a simple custom value supplier that uses the value of the previous row
     */
    @Test
    public void shouldBeAbleToDefineCustomValueSuppliersWhichUsePreviousRowsValue() {
        testTableFixture.truncateTable();

        //Build a function that returns previous value * 2
        BiFunction<Integer, Object, Object> doublingValueSupplier = (rowIndex, prevValue) -> {
            if (prevValue == null) { //previous value would be null if we are supplying value for the first row
                return 10;
            }
            return (int) prevValue * 2;
        };

        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(4)
            .withCondition("int_column", doublingValueSupplier);

        testTableFixture.insertRows(insertionCriteria);

        List<Map<String, Object>> allRows = testTableFixture.getAllRows("id ASC");
        assertEquals(4, allRows.size());

        //Make sure that the values satisfy the criteria we set
        int expectedValue = 10;
        for (int i = 0; i < allRows.size(); i++) {
            Map<String, Object> row = allRows.get(i);
            int intColValue = (int) row.get("int_column");
            assertEquals(expectedValue, intColValue);
            expectedValue *= 2;
        }
    }
}
