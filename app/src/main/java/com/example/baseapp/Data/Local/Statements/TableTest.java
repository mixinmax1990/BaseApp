package com.example.baseapp.Data.Local.Statements;

public class TableTest {

    //Create Structure of Data Table

    //Table Name
    public static final String TABLE_NAME= "user";

    //Table Content
    private static final String DATA_ID = "data_id";
    private static final String DATA_ONE = "data_one";
    private static final String DATA_TWO = "data_two";
    private static final String DATA_THREE = "data_three";
    private static final String DATA_FOUR = "data_four";

    //Query to Create Table
    public static final String CREATE_TABLE_TEST = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + DATA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DATA_ONE + " TEXT,"
            + DATA_TWO + " TEXT,"
            + DATA_THREE + " TEXT,"
            + DATA_FOUR + " TEXT"+
            ")";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getDataId() {
        return DATA_ID;
    }

    public static String getDataOne() {
        return DATA_ONE;
    }

    public static String getDataTwo() {
        return DATA_TWO;
    }

    public static String getDataThree() {
        return DATA_THREE;
    }

    public static String getDataFour() {
        return DATA_FOUR;
    }

    public static String getCreateTableTest() {
        return CREATE_TABLE_TEST;
    }
}
