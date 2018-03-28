package in.acode.utdatagen.meta;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

/**
 * Encapsulates the meta-data about a single database column
 * We need the metadata for generating INSERT queries and for getting default-values for columns
 */
public class DBColumnMetadata {

    /**
     * A map of jdbc-types and their default-value-supplier
     * Used for getting default values of columns
     */
    private final static HashMap<JDBCType, BiFunction<Integer, Object, Object>> DEFAULT_VALUE_SUPPLIERS;

    /**
     * A map of jdbc-types and a function that can generate their default-value-supplier
     * This extra-level of redirection is needed so that the default-value will be according to the max-size/precision/scale
     */
    private final static HashMap<JDBCType, BiFunction<Integer, Integer, BiFunction<Integer, Object, Object>>> DEFAULT_VALUE_SUPPLIER_GENERATORS;

    private final static Set<JDBCType> CHARACTER_TYPES;
    private final static Set<JDBCType> DECIMAL_TYPES;

    static {

        //Initialize default value suppliers
        DEFAULT_VALUE_SUPPLIERS = new HashMap<>();

        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.INTEGER, (idx, prevValue) -> ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.BIGINT, (idx, prevValue) -> ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE));
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.SMALLINT, (idx, prevValue) -> ThreadLocalRandom.current().nextInt(0, Short.MAX_VALUE));
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.TINYINT, (idx, prevValue) -> ThreadLocalRandom.current().nextInt(0, Byte.MAX_VALUE));
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.BIT, (idx, prevValue) -> ThreadLocalRandom.current().nextInt(0, 2));
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.BOOLEAN, (idx, prevValue) -> ThreadLocalRandom.current().nextBoolean());
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.REAL, (idx, prevValue) -> ThreadLocalRandom.current().nextFloat());
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.DOUBLE, (idx, prevValue) -> ThreadLocalRandom.current().nextDouble());
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.TIMESTAMP_WITH_TIMEZONE, (idx, prevValue) -> Timestamp.valueOf(LocalDateTime.now()));
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.TIMESTAMP, (idx, prevValue) -> Timestamp.valueOf(LocalDateTime.now()));
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.DATE, (idx, prevValue) -> Date.valueOf(LocalDate.now()));
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.TIME_WITH_TIMEZONE, (idx, prevValue) -> Time.valueOf(LocalTime.now()));
        DEFAULT_VALUE_SUPPLIERS.put(JDBCType.TIME, (idx, prevValue) -> Time.valueOf(LocalTime.now()));

        //Initialize generators of default value suppliers
        DEFAULT_VALUE_SUPPLIER_GENERATORS = new HashMap<>();
        BiFunction<Integer, Integer, BiFunction<Integer, Object, Object>> boundedSizeStringSupplierGenerator = (maxSize, ignore) -> (idx, prevValue) -> {
            String s = UUID.randomUUID().toString();
            return s.substring(0, Math.min(maxSize, s.length()));
        };
        DEFAULT_VALUE_SUPPLIER_GENERATORS.put(JDBCType.CHAR, boundedSizeStringSupplierGenerator);
        DEFAULT_VALUE_SUPPLIER_GENERATORS.put(JDBCType.VARCHAR, boundedSizeStringSupplierGenerator);
        DEFAULT_VALUE_SUPPLIER_GENERATORS.put(JDBCType.LONGNVARCHAR, boundedSizeStringSupplierGenerator);
        DEFAULT_VALUE_SUPPLIER_GENERATORS.put(JDBCType.NCHAR, boundedSizeStringSupplierGenerator);
        DEFAULT_VALUE_SUPPLIER_GENERATORS.put(JDBCType.NVARCHAR, boundedSizeStringSupplierGenerator);

        CHARACTER_TYPES = new HashSet<>(Arrays.asList(JDBCType.CHAR, JDBCType.VARCHAR, JDBCType.LONGNVARCHAR));

        BiFunction<Integer, Integer, BiFunction<Integer, Object, Object>> boundedDecimalSupplierGenerator = (precision, scale) -> (idx, prevValue) -> {
            int leftSide = ThreadLocalRandom.current().nextInt(0, (int)Math.pow(10, precision - scale));
            int rightSide = ThreadLocalRandom.current().nextInt(0, (int)Math.pow(10, scale));
            return new BigDecimal(leftSide + "." + rightSide);
        };
        DEFAULT_VALUE_SUPPLIER_GENERATORS.put(JDBCType.DECIMAL, boundedDecimalSupplierGenerator);
        DEFAULT_VALUE_SUPPLIER_GENERATORS.put(JDBCType.NUMERIC, boundedDecimalSupplierGenerator);

        DECIMAL_TYPES = new HashSet<>(Arrays.asList(JDBCType.DECIMAL, JDBCType.NUMERIC));
    }

    private String columnName;
    private int dataType;
    private String dataTypeName;
    private int precision;
    private int scale;
    private boolean isNullable;
    private boolean hasDefaultValue;
    private int maxSize;
    private int ordinalPos;
    private boolean isAutoInc;
    private boolean isGenerated;

    protected DBColumnMetadata(String columnName, int dataType, String dataTypeName, int precision, int scale, boolean isNullable, boolean hasDefaultValue, int maxSize, int ordinalPos, boolean isAutoInc, boolean isGenerated) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.dataTypeName = dataTypeName;
        this.precision = precision;
        this.scale = scale;
        this.isNullable = isNullable;
        this.hasDefaultValue = hasDefaultValue;
        this.maxSize = maxSize;
        this.ordinalPos = ordinalPos;
        this.isAutoInc = isAutoInc;
        this.isGenerated = isGenerated;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getDataType() {
        return dataType;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public boolean isHasDefaultValue() {
        return hasDefaultValue;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getOrdinalPos() {
        return ordinalPos;
    }

    public boolean isAutoInc() {
        return isAutoInc;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public boolean isValueEditable() {
        return !(this.isAutoInc || this.isGenerated);
    }

    /**
     * Gets the default value supplier based on the column's data-type
     */
    public BiFunction<Integer, Object, Object> getDefaultValueSupplier() {
        JDBCType jdbcType = JDBCType.valueOf(this.dataType);

        BiFunction<Integer, Object, Object> supplier = DEFAULT_VALUE_SUPPLIERS.get(jdbcType);
        if (supplier != null) {
            return supplier;
        }

        BiFunction<Integer, Integer, BiFunction<Integer, Object, Object>> supplierGenerator = DEFAULT_VALUE_SUPPLIER_GENERATORS.get(jdbcType);
        if (supplierGenerator != null) {
            if (CHARACTER_TYPES.contains(jdbcType)) {
                return supplierGenerator.apply(this.maxSize, null);
            }
            else if (DECIMAL_TYPES.contains(jdbcType)) {
                return supplierGenerator.apply(this.precision, this.scale);
            }
        }

        throw new IllegalStateException("No default value supplier defined for data-type : [" + jdbcType + "]");
    }
}
