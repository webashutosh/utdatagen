package in.acode.utdatagen;

import in.acode.utdatagen.datasources.MySQLDataSource;
import in.acode.utdatagen.meta.DBColumnMetadata;
import in.acode.utdatagen.models.TestTableModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Comparator;
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
        assertEquals(toScaleOfTwo(10.5f), firstRow.getNumericColumn());
        assertTrue(Timestamp.valueOf(today.atStartOfDay()).equals(firstRow.getTimestampColumn()));
        assertTrue(Date.valueOf(today).equals(firstRow.getDateColumn()));
        assertEquals(10, (int)firstRow.getIntColumn());
        assertEquals("char1", firstRow.getCharColumn());
        assertEquals(true, firstRow.isBooleanColumn());
        assertEquals(10.25f, firstRow.getFloatColumn(), 0.009);

        //Verify the contents of the second row
        TestTableModel secondRow = allRows.get(1);
        assertEquals("text2", secondRow.getVarcharColumn());
        assertEquals(toScaleOfTwo(20.2f), secondRow.getNumericColumn());
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

    @Test
    public void shouldTruncateTable() {
        //Create a row
        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
            .forNumberOfRows(1);
        testTableFixture.insertRows(insertionCriteria);

        //Assert that table does have rows
        List<TestTableModel> allRows = testTableFixture.getAllRows(TestTableModel.getAllFieldsRowMapper(), null);
        assertTrue(allRows.size() > 0);

        //Truncate
        testTableFixture.truncateTable();

        //Assert that table is empty
        allRows = testTableFixture.getAllRows(TestTableModel.getAllFieldsRowMapper(), null);
        assertTrue(allRows.size() == 0);
    }

    @Test
    public void shouldFetchCorrectDbMetadata() {
        testTableFixture.fillInternalStateWithDBMetadata();
        List<DBColumnMetadata> columns = testTableFixture.getColumns();

        assertEquals(9, columns.size());

        //Sort by ordinal position and assert meta-data values for each column
        columns.sort(Comparator.comparingInt(DBColumnMetadata::getOrdinalPos));

        verifyColumnMetadata(columns.get(0), "id", 4, "INT", 10, 0, false, false, 0, 1, true, false);
        verifyColumnMetadata(columns.get(1), "varchar_column", 12, "VARCHAR", 45, 0, true, false, 45, 2, false, false);
        verifyColumnMetadata(columns.get(2), "numeric_column", 3, "DECIMAL", 5, 2, true, false, 0, 3, false, false);
        verifyColumnMetadata(columns.get(3), "timestamp_column", 93, "DATETIME", 19, 0, true, false, 0, 4, false, false);
        verifyColumnMetadata(columns.get(4), "date_column", 91, "DATE", 10, 0, true, false, 0, 5, false, false);
        verifyColumnMetadata(columns.get(5), "int_column", 4, "INT", 10, 0, true, false, 0, 6, false, false);
        verifyColumnMetadata(columns.get(6), "char_column", 1, "CHAR", 20, 0, true, false, 20, 7, false, false);
        verifyColumnMetadata(columns.get(7), "boolean_column", -6, "TINYINT", 3, 0, true, false, 0, 8, false, false);
        verifyColumnMetadata(columns.get(8), "float_column", 7, "FLOAT", 12, 0, true, false, 0, 9, false, false);
    }

    private void verifyColumnMetadata(DBColumnMetadata metadata,
        String columnName,
        int dataType,
        String datatypeName,
        int precision,
        int scale,
        boolean isNullable,
        boolean hasDefaultValue,
        int maxSize,
        int ordinalPosition,
        boolean isAutoInc,
        boolean isGenerated) {

        assertEquals(columnName, metadata.getColumnName());
        assertEquals(dataType, metadata.getDataType());
        assertEquals(datatypeName, metadata.getDataTypeName());
        assertEquals(precision, metadata.getPrecision());
        assertEquals(scale, metadata.getScale());
        assertEquals(isNullable, metadata.isNullable());
        assertEquals(hasDefaultValue, metadata.isHasDefaultValue());
        assertEquals(maxSize, metadata.getMaxSize());
        assertEquals(ordinalPosition, metadata.getOrdinalPos());
        assertEquals(isAutoInc, metadata.isAutoInc());
        assertEquals(isGenerated, metadata.isGenerated());
    }

    private BigDecimal toScaleOfTwo(float num) {
        return new BigDecimal(num).setScale(2, RoundingMode.HALF_DOWN);
    }
}