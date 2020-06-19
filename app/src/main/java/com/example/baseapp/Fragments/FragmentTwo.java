package com.example.baseapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.fragment.app.Fragment;

import com.example.baseapp.Data.Local.Controller.DatabaseController;
import com.example.baseapp.Data.Local.DatabaseHelper;
import com.example.baseapp.Data.Local.Models.TestModel;
import com.example.baseapp.R;

import java.util.List;

public class FragmentTwo extends Fragment {
    public FragmentTwo() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.empty, container, false);
        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(getContext());

        asyncLayoutInflater.inflate(R.layout.fragment_two, container, new AsyncLayoutInflater.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, @Nullable ViewGroup parent) {
                // Layout Asynchronously Inflated
                Log.i("LAyoutInflated", "True");
                parent.addView(view);
                onViewCreated(view, savedInstanceState);

                //Test Local SQL Database
                DatabaseController DB = new DatabaseController(getContext());


                //Create Model Of Data

                TestModel data = new TestModel();
                data.setTest_data_id("1");
                data.setTest_data_one("Daniel Hahn");
                data.setTest_data_two("Something");
                data.setTest_data_three("Something Else");
                data.setTest_data_four("Finale");

                //Store Data into Database
                DB.TestData.enterData(data);


                //Read Thrue all of the Data
                //Loop thrue all Data Entries if they exists
                List<TestModel> allData = DB.TestData.getAllData();

                int count = 0;
                for(TestModel dat : allData){
                    count++;
                    Log.i("Data", "Count = "+count+" - Entry = "+dat.getTest_data_two());
                }



            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
