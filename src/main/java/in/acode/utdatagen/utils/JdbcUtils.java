package in.acode.utdatagen.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JdbcUtils {

  public static boolean hasColumn(ResultSet rs, String columnName) {
    ResultSetMetaData metaData = null;
    try {
      metaData = rs.getMetaData();
      int columns = metaData.getColumnCount();
      for (int x = 1; x <= columns; x++) {
        if (columnName.equals(metaData.getColumnName(x))) {
          return true;
        }
      }
      return false;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


}
