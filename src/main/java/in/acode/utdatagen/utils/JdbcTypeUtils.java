package in.acode.utdatagen.utils;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class JdbcTypeUtils {

    /**
     * Converts an object from a java-type to an appropriate SQL type
     * Spring's JDBCTemplate handles a lot of these conversions
     * However, there are some additional common java-types in use, which get handled by this method
     */
    public static Object toSQLType(Object javaTypeObject) {
        if (javaTypeObject == null) return null;

        if(javaTypeObject instanceof LocalDate) {
            return Date.valueOf((LocalDate)javaTypeObject);
        }

        if(javaTypeObject instanceof LocalTime) {
            return Time.valueOf((LocalTime)javaTypeObject);
        }

        if(javaTypeObject instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime)javaTypeObject);
        }

        return javaTypeObject;
    }



}
