package in.acode.utdatagen.models;

import in.acode.utdatagen.utils.JdbcUtils;
import java.math.BigDecimal;
import org.springframework.jdbc.core.RowMapper;

public class TestTableModel {

  private String varcharColumn;
  private BigDecimal numericColumn;

  public String getVarcharColumn() {
    return varcharColumn;
  }

  public void setVarcharColumn(String varcharColumn) {
    this.varcharColumn = varcharColumn;
  }

  public BigDecimal getNumericColumn() {
    return numericColumn;
  }

  public void setNumericColumn(BigDecimal numericColumn) {
    this.numericColumn = numericColumn;
  }

  public static RowMapper<TestTableModel> getAllFieldsRowMapper() {
    return (rs, idx) -> {
      TestTableModel model = new TestTableModel();

      if (JdbcUtils.hasColumn(rs, "varchar_column")) {
        model.setVarcharColumn(rs.getString("varchar_column"));
      }

      if (JdbcUtils.hasColumn(rs, "numeric_column")) {
        model.setNumericColumn(rs.getBigDecimal("numeric_column"));
      }

      return model;
    };
  }

}
