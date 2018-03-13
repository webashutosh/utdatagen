package in.acode.utdatagen.meta;

public class DBColumnMetadata {
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
}
