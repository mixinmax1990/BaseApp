package com.example.baseapp.Data.Local.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.baseapp.Data.Local.DatabaseHelper;
import com.example.baseapp.Data.Local.Statements.TableTest;

public class DatabaseController {

    public TestDataController TestData;

    public DatabaseController(Context context) {
        this.TestData = new TestDataController(context);
    }
}
