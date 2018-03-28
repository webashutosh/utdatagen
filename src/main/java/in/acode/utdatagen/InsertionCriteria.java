package in.acode.utdatagen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An object of this class represents a criteria/plan for inserting some rows into a table.
 * <br></p>Use the <code>forNumberOfRows()</code> method to specify the number of rows to be inserted
 * <br></p>Use the <code>withCondition()</code> methods to specify a custom value or a value-supplier for a column
 * <br>Users can define their own value supplier functions, and can use the following values in their function -
 * <br>&emsp;1. Index of the row being inserted
 * <br>&emsp;2. Value inserted in the previous row
 * <br>If you do not explicitly specify a custom value, then a default value appropriate to the column's data-type is used
 * <p>Note that OBJECTS of this class ARE MUTABLE.
 * You can create an object and insert some rows using it.
 * Then you can make some changes to the same object and insert some more rows using the modified version</p>
 */
public class InsertionCriteria {

    private int numOfRows;

    /**
     * A map of column-names and their value-suppliers
     */
    private HashMap<String, BiFunction<Integer, Object, Object>> columnValueSuppliers;

    public InsertionCriteria(int numOfRows) {
        this.forNumberOfRows(numOfRows);
        columnValueSuppliers = new HashMap<>();
    }

    public static InsertionCriteria newInstance() {
        return new InsertionCriteria(1);
    }

    public InsertionCriteria forNumberOfRows(int numOfRows) {
        if (numOfRows <= 0) {
            throw new IllegalArgumentException("Number of rows must be more than 0");
        }

        this.numOfRows = numOfRows;
        return this;
    }

    public InsertionCriteria withCondition(String columnName, BiFunction<Integer, Object, Object> valueSupplier) {
        columnValueSuppliers.put(columnName, valueSupplier);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, Function<Integer, Object> valueSupplier) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> valueSupplier.apply(idx);
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, Supplier valueSupplier) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> valueSupplier.get();
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, String value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> value;
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, String... value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> (value.length == 0 ? null : value[idx % value.length]);
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, int value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> value;
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, int... value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> (value.length == 0 ? null : value[idx % value.length]);
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, float value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> value;
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, float... value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> (value.length == 0 ? null : value[idx % value.length]);
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, LocalDate value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> value;
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, LocalDate... value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> (value.length == 0 ? null : value[idx % value.length]);
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, LocalDateTime value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> value;
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public InsertionCriteria withCondition(String columnName, LocalDateTime... value) {
        BiFunction<Integer, Object, Object> biFunction = (idx, prevVal) -> (value.length == 0 ? null : value[idx % value.length]);
        columnValueSuppliers.put(columnName, biFunction);
        return this;
    }

    public BiFunction<Integer, Object, Object> getSupplierForColumn(String columnName) {
        return this.columnValueSuppliers.get(columnName);
    }

    public int getNumOfRows() {
        return numOfRows;
    }

}
