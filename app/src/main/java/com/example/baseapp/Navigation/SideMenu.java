package com.example.baseapp.Navigation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import com.example.baseapp.R;

import java.util.List;

public class SideMenu {

    private int allTabs = 0;
    private boolean atRight = true;
    public FrameLayout prevTabEven;
    public FrameLayout prevTabOdd;
    private ConstraintLayout parent;
    private ConstraintSet parentConst = new ConstraintSet();
    private Context context;
    public FrameLayout tabCont, tabBar;
    public TextView tabName;
    public List<SideMenu> allBars;
    @ColorInt int color;
    public SideMenu(Context context, ConstraintLayout parent, String Name, List<SideMenu> allBars) {
        this.parent = parent;
        this.context = context;
        this.allBars = allBars;
        if(allBars != null){
            allTabs = allBars.size();
        }
        else{
            allTabs = 0;
        }

        Log.i("AllTabs", "No: "+ allTabs);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.textPrimaryColor, typedValue, true);
        color = typedValue.data;

        createMenuTab(Name);
    }

    public FrameLayout createMenuTab(String name){
        allTabs++;
        //Log.i("AllTabs - ", ""+allTabs);
        if(allTabs != 1){
                // Get Previous Bar Even
                prevTabEven = allBars.get(allTabs - 2).prevTabEven;
                // Get Previous Bar Odd
                prevTabOdd = allBars.get(allTabs - 2).prevTabOdd;
        }

        //Log.i("Is Running", "Yes");

        //create container of menu tab
        tabCont = new FrameLayout(context);
        ConstraintLayout.LayoutParams lp_cont = new ConstraintLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0);
        // Create top Margin on top Bars
        if(allTabs == 1 || allTabs == 2) lp_cont.topMargin = (int) convertDpToPixel(100);
        else lp_cont.topMargin = 0;

        //Create bottom Margins on bottom Bars

        if(isEven(allTabs)){
            if(prevTabEven != null) {
                ConstraintLayout.LayoutParams lp_prev = (ConstraintLayout.LayoutParams) prevTabEven.getLayoutParams();
                lp_prev.bottomMargin = 0;
            }
        }
        else{
            if(prevTabOdd != null) {
                ConstraintLayout.LayoutParams lp_prev = (ConstraintLayout.LayoutParams) prevTabOdd.getLayoutParams();
                lp_prev.bottomMargin = 0;
            }
        }

        lp_cont.bottomMargin = (int) convertDpToPixel(100);

        tabCont.setLayoutParams(lp_cont);
        tabCont.setId(View.generateViewId());
        tabCont.setTag(name+"_cont");

        //create layout of menu bar
        tabBar = new FrameLayout(context);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams((int)convertDpToPixel(2), 0);
        lp.leftMargin = (int)convertDpToPixel(3);
        lp.rightMargin = (int)convertDpToPixel(3);
        lp.topMargin = (int)convertDpToPixel(3);
        lp.bottomMargin = (int) convertDpToPixel(3);
        tabBar.setLayoutParams(lp);
        tabBar.setId(View.generateViewId());
        tabBar.setBackgroundResource(R.drawable.rounded_menu_bar);
        tabBar.getBackground().setTint(color);
        tabBar.setTag(name+"_bar");
        tabBar.setAlpha(.3f);

        //create title of menu item
        tabName = new TextView(context);
        ConstraintLayout.LayoutParams lp_name = new ConstraintLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        int textview_margins = (int) convertDpToPixel(20);
        lp_name.setMargins(textview_margins, textview_margins, textview_margins, textview_margins);
        tabName.setLayoutParams(lp_name);
        tabName.setId(View.generateViewId());
        tabName.setTag(name+"_name");
        tabName.setText(name);
        tabName.setTextColor(color);
        tabName.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        tabName.setAlpha(0.3f);
        tabName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        //ADD Views to Parent
        parent.addView(tabCont);
        parent.addView(tabBar);
        parent.addView(tabName);

        //Create constraints and add to parent
        parentConst.clone(parent);
        if(allTabs == 1){
            //First Tab so TOP and LEFT to Parent
            parentConst.connect(tabCont.getId(), ConstraintSet.END, parent.getId(), ConstraintSet.END);
            parentConst.connect(tabCont.getId(), ConstraintSet.TOP, parent.getId(), ConstraintSet.TOP);
            prevTabOdd = tabCont;

            connectTab(false);
            // Connect the Tab Bar to


        }
        else if(allTabs == 2){
            //Second Tab so TOP and START to Parent
            parentConst.connect(tabCont.getId(), ConstraintSet.START, parent.getId(), ConstraintSet.START);
            parentConst.connect(tabCont.getId(), ConstraintSet.TOP, parent.getId(), ConstraintSet.TOP);
            prevTabEven = tabCont;

            connectTab(true);
        }
        else{
            //Not the First Tab so Top to Previous Tab
            if(!isEven(allTabs)){
                //number is Even so Tab goes Left
                parentConst.connect(tabCont.getId(), ConstraintSet.END, parent.getId(), ConstraintSet.END);
                // And Tab connects to Even Previous Tab
                parentConst.connect(tabCont.getId(), ConstraintSet.TOP, prevTabOdd.getId(), ConstraintSet.BOTTOM);
                //Clear the Previous connection of Previous Cont to Parent Bottom
                parentConst.clear(prevTabOdd.getId(), ConstraintSet.BOTTOM);
                //Create a new connection to current Tab Container
                parentConst.connect(prevTabOdd.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.TOP);
                prevTabOdd = tabCont;

                connectTab(false);
            }
            else{
                //number is Odd so Tab goes Right
                parentConst.connect(tabCont.getId(), ConstraintSet.START, parent.getId(), ConstraintSet.START);
                // And Tab connects to Odd Previous Tab
                parentConst.connect(tabCont.getId(), ConstraintSet.TOP, prevTabEven.getId(), ConstraintSet.BOTTOM);
                //Clear the Previus connection of Previous Cont to Parent Bottom
                parentConst.clear(prevTabEven.getId(), ConstraintSet.BOTTOM);
                //Create a new connection to current Tab Container
                parentConst.connect(prevTabEven.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.TOP);
                prevTabEven = tabCont;

                connectTab(true);
            }
        }

        //Bottom Always connects to Parent
        parentConst.connect(tabCont.getId(), ConstraintSet.BOTTOM, parent.getId(), ConstraintSet.BOTTOM);

        parentConst.applyTo(parent);

        return tabBar;

    }

    private void connectTab(boolean isEven){
        if(!isEven){
            // NOT EVEN = Align RIGHT
            //Constraint Connect Tab to Container
            parentConst.connect(tabBar.getId(), ConstraintSet.END, tabCont.getId(), ConstraintSet.END);
            parentConst.connect(tabBar.getId(), ConstraintSet.TOP, tabCont.getId(), ConstraintSet.TOP);
            parentConst.connect(tabBar.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.BOTTOM);
            //parentConst.constrainHeight(tabBar.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

            // Constraint Connect Text to Bar
            parentConst.connect(tabName.getId(), ConstraintSet.END, tabBar.getId(), ConstraintSet.START);
            parentConst.connect(tabName.getId(), ConstraintSet.TOP, tabCont.getId(), ConstraintSet.TOP);
            parentConst.connect(tabName.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.BOTTOM);
        }
        else{
            //EVEN = Align LEFT
            //Constraint Connect Tab to Container
            parentConst.connect(tabBar.getId(), ConstraintSet.START, tabCont.getId(), ConstraintSet.START);
            parentConst.connect(tabBar.getId(), ConstraintSet.TOP, tabCont.getId(), ConstraintSet.TOP);
            parentConst.connect(tabBar.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.BOTTOM);
            //parentConst.constrainHeight(tabBar.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

            // Constraint Connect Text to Bar
            parentConst.connect(tabName.getId(), ConstraintSet.START, tabBar.getId(), ConstraintSet.END);
            parentConst.connect(tabName.getId(), ConstraintSet.TOP, tabCont.getId(), ConstraintSet.TOP);
            parentConst.connect(tabName.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.BOTTOM);
        }

    }

    private boolean isEven(int numb){
        boolean isEven = true;
        if (numb % 2 == 0)
            isEven = true;
        else
            isEven = false;

        return isEven;
    }

    private void openFragment(){

    }

    public  float convertDpToPixel(float dp){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public FrameLayout getTabCont() {
        return tabCont;
    }

    public void setTabCont(FrameLayout tabCont) {
        this.tabCont = tabCont;
    }

    public FrameLayout getTabBar() {
        return tabBar;
    }

    public void setTabBar(FrameLayout tabBar) {
        this.tabBar = tabBar;
    }

    public TextView getTabName() {
        return tabName;
    }

    public void setTabName(TextView tabName) {
        this.tabName = tabName;
    }
}
