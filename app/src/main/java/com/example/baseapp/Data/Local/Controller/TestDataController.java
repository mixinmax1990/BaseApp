package com.example.baseapp.Data.Local.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.Nullable;
import com.example.baseapp.Data.Local.DatabaseHelper;
import com.example.baseapp.Data.Local.Models.TestModel;
import com.example.baseapp.Data.Local.Statements.TableTest;

import java.util.ArrayList;
import java.util.List;

public class TestDataController extends DatabaseHelper {

    private TableTest testTable;
    private SQLiteDatabase db;
    private ContentValues values;
    public TestDataController(Context context) {
        super(context);
        //check if I can pass Database to this controller instead of creating new
        db = this.getWritableDatabase();
        testTable = new TableTest();
        db.execSQL(testTable.getCreateTableTest());
    }

    //Define Data Manipulation

    public void enterData(TestModel model){
        db = this.getWritableDatabase();

        values = new ContentValues();
        //values.put(testTable.getDataId(), model.getTest_data_id());
        values.put(testTable.getDataOne(), model.getTest_data_one());
        values.put(testTable.getDataTwo(), model.getTest_data_two());
        values.put(testTable.getDataThree(), model.getTest_data_three());
        values.put(testTable.getDataFour(), model.getTest_data_four());

        long insert = db.insert(testTable.getTableName(), null, values);
    }

    public List<TestModel> getAllData(){
        db = this.getWritableDatabase();

        List<TestModel> datalist = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + testTable.TABLE_NAME;

        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                TestModel data = new TestModel();

                data.setTest_data_id(c.getString(c.getColumnIndex(testTable.getDataId())));
                data.setTest_data_one(c.getString(c.getColumnIndex(testTable.getDataOne())));
                data.setTest_data_two(c.getString(c.getColumnIndex(testTable.getDataTwo())));
                data.setTest_data_three(c.getString(c.getColumnIndex(testTable.getDataThree())));
                data.setTest_data_four(c.getString(c.getColumnIndex(testTable.getDataFour())));

                datalist.add(data);
            }
            while(c.moveToNext());
        }
        return datalist;
    }
}
