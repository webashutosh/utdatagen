# utdatagen

A very small java library to create test data for Unit Tests.

[![Build Status](https://travis-ci.org/webashutosh/utdatagen.svg?branch=master)](https://travis-ci.org/webashutosh/utdatagen)

### Usage instructions -

1) Create an instance of a DBTableFixture pointing to a single DB table
2) Use the truncateTable() method on the instance to delete existing data (*optional*)
3) Use the InsertionCriteria class to specify powerful/flexible plans to insert some data with various patterns
4) Test your data-access/manipulation code on the generated data
5) If needed, use the methods on the DBTableFixture instance to retrieve data in the table and assert against it

**Please refer to these tests for basic sample usage -**  
https://github.com/webashutosh/utdatagen/blob/master/src/test/java/in/acode/utdatagen/DBTableFixtureMySqlTest.java

**Demo for using built-in value suppliers AND for building your own column value suppliers-**
https://github.com/webashutosh/utdatagen/blob/master/src/test/java/in/acode/utdatagen/CustomValueSuppliersTest.java


### Crux of how it all works -
* When creating an InsertionCriteria, you have to specify two main things -
  * the number of rows to be inserted (n)
  * optionally, a value-supplier for some/all columns
* When you start inserting rows, the DBTableFixture generates n rows in a loop, one at a time
* For generating a single row, it has to get values for all the columns. It does so as following -
  * **If the user specified a value-supplier,** it calls that to get the value
    * Value-suppliers have access to the index of the row being inserted AND the value inserted for the previous row - this info can be used while generating the value for the current row
  * **Else,** it checks if the column is nullable 
  * If it is, it leaves it as null
  * Else, it generates a random default value (it tries best to make sure that the value fits in the DB column restrictions). For details - please refer to DBColumnMetadata.java
  * Note - users can indicate that they want nullable columns to have values, in which case, a random default value is used
* Once all rows are generated in memory, they are inserted to the DB
