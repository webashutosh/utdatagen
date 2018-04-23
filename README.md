# utdatagen

A very small java library to create test data for Unit Tests.

Steps -

1) Create an instance of a DBTableFixture pointing to a single DB table
2) Use methods on the instance to insert/fetch and delete data
3) Use the InsertionCriteria class to specify powerful/flexible ways to insert data with various patterns

Please refer to these tests for sample usage -  
https://github.com/webashutosh/utdatagen/blob/master/src/test/java/in/acode/utdatagen/DBTableFixtureMySqlTest.java
