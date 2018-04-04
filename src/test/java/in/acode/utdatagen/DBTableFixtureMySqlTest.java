package in.acode.utdatagen;

import in.acode.utdatagen.datasources.MySQLDataSource;
import in.acode.utdatagen.models.TestTableModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
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
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(classes = {MySQLDataSource.class})
public class DBTableFixtureMySqlTest {

    @Autowired
    @Qualifier("mysql-jdbc-template")
    private JdbcTemplate jdbcTemplate;

    DBTableFixture testTableFixture;

    @Before
    public void setup() {
        testTableFixture = DBTableFixture.getInstance("test_table", jdbcTemplate);
    }

    @Test
    public void shouldCoreFunctionalityWork() {
        testTableFixture.truncateTable();

        //Test insertion
        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
                .forNumberOfRows(2)
                .withCondition("varchar_column", "test_string1", "test_string2")
                .withCondition("numeric_column", 10.02f);

        testTableFixture.insertRows(insertionCriteria);

        //Test selection
        List<Map<String, Object>> allRows = testTableFixture.getAllRows();
        assertEquals(2, allRows.size());

        //Test truncation
        testTableFixture.truncateTable();
        allRows = testTableFixture.getAllRows();
        assertEquals(0, allRows.size());
    }

    @Test
    public void shouldGetAllRowsInColumnToValueMaps() {
        testTableFixture.truncateTable();

        //Insert 2 rows
        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(2)
            .withCondition("varchar_column", "test_string1", "test_string2")
            .withCondition("numeric_column", 10.02f);

        testTableFixture.insertRows(insertionCriteria);

        //Fetch them with this order - varchar_column DESC
        List<Map<String, Object>> allRows = testTableFixture.getAllRows("varchar_column DESC");
        assertEquals(2, allRows.size());

        //Verify first row
        Map<String, Object> firstRow = allRows.get(0);
        assertEquals("test_string2", firstRow.get("varchar_column"));
        assertEquals(toScaleOfTwo(10.02f), firstRow.get("numeric_column"));

        //Verify second row
        Map<String, Object> secondRow = allRows.get(1);
        assertEquals("test_string1", secondRow.get("varchar_column"));
        assertEquals(toScaleOfTwo(10.02f), secondRow.get("numeric_column"));
    }

    @Test
    public void shouldGetAllRowsByUsingCustomMapper() {
        testTableFixture.truncateTable();

        //Insert 2 rows
        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(2)
            .withCondition("varchar_column", "test_string1", "test_string2")
            .withCondition("numeric_column", 10.02f);

        testTableFixture.insertRows(insertionCriteria);

        //Fetch them with this order - varchar_column DESC
        List<TestTableModel> allRows = testTableFixture.getAllRows(TestTableModel.getAllFieldsRowMapper(), "varchar_column DESC");
        assertEquals(2, allRows.size());

        //Verify first row
        TestTableModel firstRow = allRows.get(0);
        assertEquals("test_string2", firstRow.getVarcharColumn());
        assertEquals(toScaleOfTwo(10.02f), firstRow.getNumericColumn());

        //Verify second row
        TestTableModel secondRow = allRows.get(0);
        assertEquals("test_string2", secondRow.getVarcharColumn());
        assertEquals(toScaleOfTwo(10.02f), secondRow.getNumericColumn());
    }

    private BigDecimal toScaleOfTwo(float num) {
        return new BigDecimal(num).setScale(2, RoundingMode.FLOOR);
    }

    @Test
    public void shouldInsertRowsWithValuesOfAllMajorDataTypes() {
        LocalDate today = LocalDate.now();

        //Insert 2 rows with all columns set to some specific values
        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(2)
            .withCondition("varchar_column", "text1", "text2")
            .withCondition("numeric_column", 10.5f, 20.2f)
            .withCondition("timestamp_column", today.atStartOfDay(), today.atStartOfDay().plusYears(1))
            .withCondition("date_column", today, today.plusDays(10))
            .withCondition("int_column", 10, 50)
            .withCondition("char_column", "char1", "char2")
            .withCondition("boolean_column", true, false)
            .withCondition("float_column", 10.25f, 20.55f);

        testTableFixture.truncateTable();
        testTableFixture.insertRows(insertionCriteria);

        List<TestTableModel> allRows = testTableFixture.getAllRows(TestTableModel.getAllFieldsRowMapper(), "id");
        assertEquals(2, allRows.size());

        //Verify the contents of the first row
        TestTableModel firstRow = allRows.get(0);
        assertEquals("text1", firstRow.getVarcharColumn());
        assertEquals(new BigDecimal(10.5).setScale(2, RoundingMode.HALF_DOWN), firstRow.getNumericColumn());
        assertTrue(Timestamp.valueOf(today.atStartOfDay()).equals(firstRow.getTimestampColumn()));
        assertTrue(Date.valueOf(today).equals(firstRow.getDateColumn()));
        assertEquals(10, (int)firstRow.getIntColumn());
        assertEquals("char1", firstRow.getCharColumn());
        assertEquals(true, firstRow.isBooleanColumn());
        assertEquals(10.25f, firstRow.getFloatColumn(), 0.009);

        //Verify the contents of the second row
        TestTableModel secondRow = allRows.get(1);
        assertEquals("text2", secondRow.getVarcharColumn());
        assertEquals(new BigDecimal(20.2).setScale(2, RoundingMode.HALF_DOWN), secondRow.getNumericColumn());
        assertTrue(Timestamp.valueOf(today.atStartOfDay().plusYears(1)).equals(secondRow.getTimestampColumn()));
        assertTrue(Date.valueOf(today.plusDays(10)).equals(secondRow.getDateColumn()));
        assertEquals(50, (int)secondRow.getIntColumn());
        assertEquals("char2", secondRow.getCharColumn());
        assertEquals(false, secondRow.isBooleanColumn());
        assertEquals(20.55f, secondRow.getFloatColumn(), 0.009);
    }

    @Test
    public void shouldNotInsertValuesForNullableColumnsByDefault() {
        //Insert 1 rows without specifying values of any column (all columns in the table are nullable)
        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(1);

        testTableFixture.truncateTable();
        testTableFixture.insertRows(insertionCriteria);

        List<TestTableModel> allRows = testTableFixture.getAllRows(TestTableModel.getAllFieldsRowMapper(), null);
        assertEquals(1, allRows.size());

        //Verify the contents of the first row
        TestTableModel firstRow = allRows.get(0);
        assertNull(firstRow.getVarcharColumn());
        assertNull(firstRow.getNumericColumn());
        assertNull(firstRow.getTimestampColumn());
        assertNull(firstRow.getDateColumn());
        assertNull(firstRow.getIntColumn());
        assertNull(firstRow.getCharColumn());
        assertNull(firstRow.isBooleanColumn());
        assertNull(firstRow.getFloatColumn());
    }

    @Test
    public void shouldInsertValuesForNullableColumnsIfDesired() {
        //Insert 1 rows without specifying values of any column (all columns in the table are nullable)
        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(1)
            .insertDefaultsForNullableColumns(true);

        testTableFixture.truncateTable();
        testTableFixture.insertRows(insertionCriteria);

        List<TestTableModel> allRows = testTableFixture.getAllRows(TestTableModel.getAllFieldsRowMapper(), null);
        assertEquals(1, allRows.size());

        //Verify the contents of the first row
        TestTableModel firstRow = allRows.get(0);
        assertTrue(firstRow.getVarcharColumn().length() > 0);
        assertNotNull(firstRow.getNumericColumn());
        assertNotNull(firstRow.getTimestampColumn());
        assertNotNull(firstRow.getDateColumn());
        assertNotNull(firstRow.getIntColumn());
        assertTrue(firstRow.getCharColumn().length() > 0);
        assertNotNull(firstRow.isBooleanColumn());
        assertNotNull(firstRow.getFloatColumn());
    }
}