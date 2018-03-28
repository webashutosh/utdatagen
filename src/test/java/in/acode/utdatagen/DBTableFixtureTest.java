package in.acode.utdatagen;

import in.acode.utdatagen.datasources.MySQLDataSources;
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
@ContextConfiguration(classes = {MySQLDataSources.class})
public class DBTableFixtureTest {

    @Autowired
    @Qualifier("mysql-jdbc-template")
    private JdbcTemplate jdbcTemplate;

    @Test
    public void shouldCoreFunctionalityWork() {
        DBTableFixture testTableFixture = DBTableFixture.getInstance("test_table", jdbcTemplate);

        testTableFixture.truncateTable();
        List<Map<String, Object>> allRows = testTableFixture.getAllRows();
        assertEquals(0, allRows.size());

        InsertionCriteria insertionCriteria = InsertionCriteria.newInstance()
                .forNumberOfRows(2)
                .withCondition("varchar_column", "test_string")
                .withCondition("numeric_column", 10.02f);

        testTableFixture.insertRows(insertionCriteria);

        allRows = testTableFixture.getAllRows();
        assertEquals(2, allRows.size());
    }

}