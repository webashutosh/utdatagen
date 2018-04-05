package in.acode.utdatagen.models;

import in.acode.utdatagen.utils.JdbcUtils;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;

public class TestTableModel {

  private String varcharColumn;
  private BigDecimal numericColumn;
  private Timestamp timestampColumn;
  private Date dateColumn;
  private Integer intColumn;
  private String charColumn;
  private Boolean booleanColumn;
  private Float floatColumn;

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

  public Timestamp getTimestampColumn() {
    return timestampColumn;
  }

  public void setTimestampColumn(Timestamp timestampColumn) {
    this.timestampColumn = timestampColumn;
  }

  public Date getDateColumn() {
    return dateColumn;
  }

  public void setDateColumn(Date dateColumn) {
    this.dateColumn = dateColumn;
  }

  public Integer getIntColumn() {
    return intColumn;
  }

  public void setIntColumn(Integer intColumn) {
    this.intColumn = intColumn;
  }

  public String getCharColumn() {
    return charColumn;
  }

  public void setCharColumn(String charColumn) {
    this.charColumn = charColumn;
  }

  public Boolean isBooleanColumn() {
    return booleanColumn;
  }

  public void setBooleanColumn(Boolean booleanColumn) {
    this.booleanColumn = booleanColumn;
  }

  public Float getFloatColumn() {
    return floatColumn;
  }

  public void setFloatColumn(Float floatColumn) {
    this.floatColumn = floatColumn;
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

      if (JdbcUtils.hasColumn(rs, "timestamp_column")) {
        model.setTimestampColumn(rs.getTimestamp("timestamp_column"));
      }

      if (JdbcUtils.hasColumn(rs, "date_column")) {
        model.setDateColumn(rs.getDate("date_column"));
      }

      if (JdbcUtils.hasColumn(rs, "int_column")) {
        model.setIntColumn((Integer) rs.getObject("int_column"));
      }

      if (JdbcUtils.hasColumn(rs, "char_column")) {
        model.setCharColumn(rs.getString("char_column"));
      }

      if (JdbcUtils.hasColumn(rs, "boolean_column")) {
        if (rs.getObject("boolean_column") instanceof Integer) {
          model.setBooleanColumn((Integer) rs.getObject("boolean_column") == 1 ? Boolean.TRUE : Boolean.FALSE);
        } else {
          model.setBooleanColumn((Boolean) rs.getObject("boolean_column"));
        }
      }

      if (JdbcUtils.hasColumn(rs, "float_column")) {
        model.setFloatColumn((Float) rs.getObject("float_column"));
      }

      return model;
    };
  }

}
