package in.acode.utdatagen.models;

import java.math.BigDecimal;

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
}
