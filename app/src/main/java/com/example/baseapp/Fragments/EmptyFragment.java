package com.example.baseapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.fragment.app.Fragment;

import com.example.baseapp.R;

public class EmptyFragment extends Fragment {

    public EmptyFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.empty, container, false);
        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(getContext());

        asyncLayoutInflater.inflate(R.layout.fragment_one, container, new AsyncLayoutInflater.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, @Nullable ViewGroup parent) {
                // Layout Asynchronously Inflated
                parent.addView(view);
                onViewCreated(view, savedInstanceState);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
