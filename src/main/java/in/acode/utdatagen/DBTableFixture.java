package in.acode.utdatagen;

import in.acode.utdatagen.meta.DBColumnMetadata;
import in.acode.utdatagen.meta.DBColumnMetadataBuilder;
import in.acode.utdatagen.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBTableFixture {

    private static final Log LOG = LogFactory.getLog(DBTableFixture.class);

    private String tableName;
    private JdbcTemplate jdbcTemplate;
    private List<DBColumnMetadata> columns;

    private DBTableFixture(String tableName, JdbcTemplate jdbcTemplate) {
        this.tableName = tableName;
        this.jdbcTemplate = jdbcTemplate;
        this.columns = new ArrayList<>();
    }

    public static DBTableFixture getInstance(String tableName, JdbcTemplate jdbcTemplate) {
        return new DBTableFixture(tableName, jdbcTemplate);
    }

    public int truncateTable() {
        LOG.info("Started truncating table [" + this.tableName + "]");
        int rowCount = jdbcTemplate.update("DELETE FROM " + this.tableName);
        LOG.info("Finished truncating table [" + this.tableName + "]");
        return rowCount;
    }

    /**
     * Fetches all rows and returns them in a list
     * Each item in the list is a Map, with column-name as the key and corresponding data as the value
     * @param orderByClause columns to order the data e.g. "EMPLOYEE_ID" or "EMPLOYEE_ID DESC"
     * @return list with an item per row
     */
    public List<Map<String, Object>> getAllRows(String... orderByClause) {
        String sql = getSelectSQL(orderByClause);
        List<Map<String, Object>> listOfDataMaps = new ArrayList<>();
        fillInternalStateWithDBMetadata();

        jdbcTemplate.query(sql, resultSet -> {
            HashMap<String, Object> rowData = new HashMap<>();
            for(DBColumnMetadata columnMetadata : DBTableFixture.this.columns) {
                rowData.put(columnMetadata.getColumnName(), resultSet.getObject(columnMetadata.getColumnName()));
            }
            listOfDataMaps.add(rowData);
        });

        return listOfDataMaps;
    }

    /**
     * Fetches all rows and returns them in a list
     * Each item in the list is an object created by the supplied <code>rowMapper</code>
     * @param orderByClause columns to order the data e.g. "EMPLOYEE_ID" or "EMPLOYEE_ID DESC"
     * @return list with an object per row
     */
    public <T> List<T> getAllRows(RowMapper<T> rowMapper, String... orderByClause) {
        String sql = getSelectSQL(orderByClause);
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * Returns the SQL to fetch all columns of the table
     */
    protected String getSelectSQL(String[] orderByClause) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM " + tableName);

        if (orderByClause != null && orderByClause.length > 0) {
            Arrays.stream(orderByClause).forEach(obc -> sqlBuilder.append(obc).append(", "));
            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
        }

        return sqlBuilder.toString();
    }

    /**
     * Fetches column metadata from the DB and caches it for use by other methods
     */
    protected void fillInternalStateWithDBMetadata() {
        if (this.columns != null && this.columns.size() > 0) return;

        try {
            DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
            ResultSet columns = metaData.getColumns(null, null, this.tableName, null);

            while(columns.next()) {
                DBColumnMetadata dbColumnMetadata = DBColumnMetadataBuilder.getInstance()
                        .withColumnName(columns.getString("COLUMN_NAME"))
                        .withDataType(columns.getInt("DATA_TYPE"))
                        .withDataTypeName(columns.getString("TYPE_NAME"))
                        .withPrecision(columns.getInt("COLUMN_SIZE"))
                        .withScale(columns.getInt("DECIMAL_DIGITS"))
                        .withIsNullable(columns.getInt("NULLABLE") == 1)
                        .withHasDefaultValue(!StringUtils.isEmpty(columns.getString("COLUMN_DEF")))
                        .withMaxSize(columns.getInt("CHAR_OCTET_LENGTH"))
                        .withOrdinalPos(columns.getInt("ORDINAL_POSITION"))
                        .withIsAutoInc(columns.getString("IS_AUTOINCREMENT").equals("YES"))
                        .withIsGenerated(columns.getString("IS_GENERATEDCOLUMN").equals("YES"))
                        .createDBColumnMetadata();
                this.columns.add(dbColumnMetadata);
            }
        } catch (SQLException e) {
            throw new DataRetrievalFailureException("Failed to fetch DB metadata", e);
        }
    }

}
