package in.acode.utdatagen;

import in.acode.utdatagen.datasources.MySQLDataSource;
import in.acode.utdatagen.models.TestTableModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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


}