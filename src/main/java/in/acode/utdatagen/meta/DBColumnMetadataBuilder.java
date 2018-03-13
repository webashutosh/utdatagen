package in.acode.utdatagen.meta;

public class DBColumnMetadataBuilder {

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

    public static DBColumnMetadataBuilder getInstance() {
        return new DBColumnMetadataBuilder();
    }

    public DBColumnMetadataBuilder withColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public DBColumnMetadataBuilder withDataType(int dataType) {
        this.dataType = dataType;
        return this;
    }

    public DBColumnMetadataBuilder withDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
        return this;
    }

    public DBColumnMetadataBuilder withPrecision(int precision) {
        this.precision = precision;
        return this;
    }

    public DBColumnMetadataBuilder withScale(int scale) {
        this.scale = scale;
        return this;
    }

    public DBColumnMetadataBuilder withIsNullable(boolean isNullable) {
        this.isNullable = isNullable;
        return this;
    }

    public DBColumnMetadataBuilder withHasDefaultValue(boolean hasDefaultValue) {
        this.hasDefaultValue = hasDefaultValue;
        return this;
    }

    public DBColumnMetadataBuilder withMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public DBColumnMetadataBuilder withOrdinalPos(int ordinalPos) {
        this.ordinalPos = ordinalPos;
        return this;
    }

    public DBColumnMetadataBuilder withIsAutoInc(boolean isAutoInc) {
        this.isAutoInc = isAutoInc;
        return this;
    }

    public DBColumnMetadataBuilder withIsGenerated(boolean isGenerated) {
        this.isGenerated = isGenerated;
        return this;
    }

    public DBColumnMetadata createDBColumnMetadata() {
        return new DBColumnMetadata(columnName, dataType, dataTypeName, precision, scale, isNullable, hasDefaultValue, maxSize, ordinalPos, isAutoInc, isGenerated);
    }
}