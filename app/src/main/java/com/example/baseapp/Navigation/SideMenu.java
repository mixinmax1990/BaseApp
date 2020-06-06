package com.example.baseapp.Navigation;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class SideMenu {

    private int LeftTabNo = 0;
    private int RightTabNo = 0;
    private int allTabs = 0;
    private boolean atRight = true;
    private ConstraintLayout parent;
    private ConstraintSet parentConst;
    private Context context;

    public SideMenu(Context context, ConstraintLayout parent) {
        this.parent = parent;
        this.context = context;
    }

    public void createMenuTab(String name){
        allTabs++;
        if(atRight){RightTabNo++; atRight = false;}
        else{LeftTabNo++; atRight = true;}
        // if number is Prime tab comes Left else Right

        FrameLayout tabCont, tabBar;
        TextView tabName;
        tabCont = new FrameLayout(context);
        tabCont.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        tabCont.setId(allTabs);


    }

    private boolean isPrime(int numb){
        boolean isPrime = true;
        for(int p = 2; p < numb; p++) {
            if(numb % p == 0){
                isPrime = false;
                break;
            }
        }
        return isPrime;
    }

    private void openFragment(){

    }
}
