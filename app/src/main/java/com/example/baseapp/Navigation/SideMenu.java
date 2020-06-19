package com.example.baseapp.Navigation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.baseapp.R;

import java.util.List;

public class SideMenu {

    public FrameLayout prevTabEven;
    public FrameLayout prevTabOdd;
    public FrameLayout tabCont, tabBar;
    public FrameLayout tabBG;
    public TextView tabName;
    public List<SideMenu> allBars;
    public String type;
    @ColorInt
    int color;
    private int allTabs = 0;
    private boolean atRight = true;
    private ConstraintLayout parent;
    private ConstraintSet parentConst = new ConstraintSet();
    private Context context;

    public SideMenu(Context context, ConstraintLayout parent, String Name, List<SideMenu> allBars, String type) {
        this.parent = parent;
        this.context = context;
        this.allBars = allBars;
        this.type = type;
        if (allBars != null) {
            allTabs = allBars.size();
        } else {
            allTabs = 0;
        }

        Log.i("AllTabs", "No: " + allTabs);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.textPrimaryColor, typedValue, true);
        color = typedValue.data;


        createMenuTab(Name, type);

    }


    public FrameLayout createMenuTab(String name, String type) {
        if (type == "standard") {
            allTabs++;

            //Log.i("AllTabs - ", ""+allTabs);
            if (allTabs != 1) {
                // Get Previous Bar Even
                prevTabEven = allBars.get(allTabs - 2).prevTabEven;
                // Get Previous Bar Odd
                prevTabOdd = allBars.get(allTabs - 2).prevTabOdd;
            }
        }
        //Log.i("Is Running", "Yes");

        //create container of menu tab
        tabCont = new FrameLayout(context);
        ConstraintLayout.LayoutParams lp_cont = new ConstraintLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0);
        // Create top Margin on top Bars
        if (allTabs == 1 || allTabs == 2) lp_cont.topMargin = (int) convertDpToPixel(100);
        else lp_cont.topMargin = 0;

        //Create bottom Margins on bottom Bars

        if (isEven(allTabs)) {
            if (prevTabEven != null) {
                ConstraintLayout.LayoutParams lp_prev = (ConstraintLayout.LayoutParams) prevTabEven.getLayoutParams();
                lp_prev.bottomMargin = 0;
            }
        } else {
            if (prevTabOdd != null) {
                ConstraintLayout.LayoutParams lp_prev = (ConstraintLayout.LayoutParams) prevTabOdd.getLayoutParams();
                lp_prev.bottomMargin = 0;
            }
        }

        if (type == "standard") {
            lp_cont.bottomMargin = (int) convertDpToPixel(100);
        } else {
            lp_cont.bottomMargin = (int) convertDpToPixel(0);
            lp_cont.topMargin = (int) convertDpToPixel(30);
        }
        tabCont.setLayoutParams(lp_cont);
        tabCont.setId(View.generateViewId());
        tabCont.setTag(name + "_cont");

        //create layout of menu bar
        tabBar = new FrameLayout(context);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams((int) convertDpToPixel(2), 0);
        lp.leftMargin = (int) convertDpToPixel(3);
        lp.rightMargin = (int) convertDpToPixel(3);
        lp.topMargin = (int) convertDpToPixel(3);
        lp.bottomMargin = (int) convertDpToPixel(3);
        tabBar.setLayoutParams(lp);
        tabBar.setId(View.generateViewId());
        tabBar.setBackgroundResource(R.drawable.rounded_menu_bar);
        tabBar.getBackground().setTint(color);
        tabBar.setElevation(10);
        tabBar.setTag(name + "_bar");
        tabBar.setAlpha(.3f);

        //create title of menu item
        tabName = new TextView(context);
        ConstraintLayout.LayoutParams lp_name = new ConstraintLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        int textview_margins = (int) convertDpToPixel(0);
        tabName.setPadding(textview_margins, textview_margins, textview_margins, textview_margins);
        tabName.setLayoutParams(lp_name);
        //tabName.setShadowLayer(30, 0, 0, Color.WHITE);
        tabName.setId(View.generateViewId());
        tabName.setTag(name + "_name");
        tabName.setElevation(10);
        tabName.setText(name);
        tabName.setTextColor(color);
        tabName.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        tabName.setAlpha(0f);
        tabName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

        //Create TabBG  Cardview Background
        tabBG = new FrameLayout(context);
        ConstraintLayout.LayoutParams lp_card = new ConstraintLayout.LayoutParams(0, 0);
        tabBG.setId(View.generateViewId());
        tabBG.setLayoutParams(lp_card);

        //ADD Views to Parent
        parent.addView(tabCont);
        parent.addView(tabBar);
        parent.addView(tabName);
        parent.addView(tabBG);
        //Create constraints and add to parent
        parentConst.clone(parent);
        switch (type) {
            case "standard":
                if (allTabs == 1) {
                    //First Tab so TOP and LEFT to Parent
                    parentConst.connect(tabCont.getId(), ConstraintSet.END, parent.getId(), ConstraintSet.END);
                    parentConst.connect(tabCont.getId(), ConstraintSet.TOP, parent.getId(), ConstraintSet.TOP);
                    prevTabOdd = tabCont;

                    connectTab(false);
                    // Connect the Tab Bar to

                } else if (allTabs == 2) {
                    //Second Tab so TOP and START to Parent
                    parentConst.connect(tabCont.getId(), ConstraintSet.START, parent.getId(), ConstraintSet.START);
                    parentConst.connect(tabCont.getId(), ConstraintSet.TOP, parent.getId(), ConstraintSet.TOP);
                    prevTabEven = tabCont;

                    connectTab(true);
                } else {
                    //Not the First Tab so Top to Previous Tab
                    if (!isEven(allTabs)) {
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
                    } else {
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
                break;

            case "setleft":
                parentConst.connect(tabCont.getId(), ConstraintSet.START, parent.getId(), ConstraintSet.START);
                // Connect to to left Cont;
                parentConst.connect(tabCont.getId(), ConstraintSet.BOTTOM, allBars.get(1).getTabCont().getId(), ConstraintSet.TOP);
                //Connect to parent Top
                parentConst.connect(tabCont.getId(), ConstraintSet.TOP, parent.getId(), ConstraintSet.TOP);
                connectTab(false);
                break;

            default:
                break;

        }


        parentConst.applyTo(parent);

        return tabBar;

    }

    private void connectTab(boolean isEven) {
        if (!isEven) {
            // NOT EVEN = Align RIGHT
            //Constraint Connect Tab to Container
            parentConst.connect(tabBar.getId(), ConstraintSet.END, tabCont.getId(), ConstraintSet.END);
            parentConst.connect(tabBar.getId(), ConstraintSet.TOP, tabCont.getId(), ConstraintSet.TOP);
            parentConst.connect(tabBar.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.BOTTOM);
            //parentConst.constrainHeight(tabBar.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

            // Constraint Connect Text to Bar
            parentConst.connect(tabName.getId(), ConstraintSet.START, tabBG.getId(), ConstraintSet.START, (int) convertDpToPixel(5));
            parentConst.connect(tabName.getId(), ConstraintSet.TOP, tabCont.getId(), ConstraintSet.TOP);
            parentConst.connect(tabName.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.BOTTOM);

            // Tab BG
            parentConst.connect(tabBG.getId(), ConstraintSet.END, tabBar.getId(), ConstraintSet.START);
            parentConst.connect(tabBG.getId(), ConstraintSet.TOP, tabBar.getId(), ConstraintSet.TOP);
            parentConst.connect(tabBG.getId(), ConstraintSet.BOTTOM, tabBar.getId(), ConstraintSet.BOTTOM);
            //parentConst.connect(tabBG.getId(), ConstraintSet.START, tabName.getId(), ConstraintSet.START);

            tabBG.setBackgroundResource(R.drawable.ic_pull_moon);
        } else {
            //EVEN = Align LEFT
            //Constraint Connect Tab to Container
            parentConst.connect(tabBar.getId(), ConstraintSet.START, tabCont.getId(), ConstraintSet.START);
            parentConst.connect(tabBar.getId(), ConstraintSet.TOP, tabCont.getId(), ConstraintSet.TOP);
            parentConst.connect(tabBar.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.BOTTOM);
            //parentConst.constrainHeight(tabBar.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

            // Constraint Connect Text to Bar
            parentConst.connect(tabName.getId(), ConstraintSet.END, tabBG.getId(), ConstraintSet.END, (int) convertDpToPixel(5));
            parentConst.connect(tabName.getId(), ConstraintSet.TOP, tabCont.getId(), ConstraintSet.TOP);
            parentConst.connect(tabName.getId(), ConstraintSet.BOTTOM, tabCont.getId(), ConstraintSet.BOTTOM);

            // Tab BG
            parentConst.connect(tabBG.getId(), ConstraintSet.START, tabBar.getId(), ConstraintSet.END);
            parentConst.connect(tabBG.getId(), ConstraintSet.TOP, tabBar.getId(), ConstraintSet.TOP);
            parentConst.connect(tabBG.getId(), ConstraintSet.BOTTOM, tabBar.getId(), ConstraintSet.BOTTOM);
            //parentConst.connect(tabBG.getId(), ConstraintSet.END, tabName.getId(), ConstraintSet.END);

            tabBG.setBackgroundResource(R.drawable.ic_pull_moon_left);
        }
    }

    public void connectBGToName(boolean left) {
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        //Create New Connections
        if (left) {
            //Clear Constraints
            set.clear(tabBG.getId(), ConstraintSet.START);
            set.clear(tabName.getId(), ConstraintSet.START);

            set.clear(tabBG.getId(), ConstraintSet.END);
            set.clear(tabName.getId(), ConstraintSet.END);

            set.applyTo(parent);
            set = new ConstraintSet();
            set.clone(parent);
            //Reconnect
            set.connect(tabBG.getId(), ConstraintSet.START, tabBar.getId(), ConstraintSet.END);
            set.connect(tabBG.getId(), ConstraintSet.END, tabName.getId(), ConstraintSet.END);
            set.connect(tabName.getId(), ConstraintSet.START, tabBar.getId(), ConstraintSet.END);

            set.applyTo(parent);
        } else {

            //Clear Constraints
            set.clear(tabBG.getId(), ConstraintSet.END);
            set.clear(tabName.getId(), ConstraintSet.END);

            set.clear(tabBG.getId(), ConstraintSet.START);
            set.clear(tabName.getId(), ConstraintSet.START);

            set.applyTo(parent);
            set = new ConstraintSet();
            set.clone(parent);
            //Reconnect
            set.connect(tabBG.getId(), ConstraintSet.END, tabBar.getId(), ConstraintSet.START);
            set.connect(tabBG.getId(), ConstraintSet.START, tabName.getId(), ConstraintSet.START);
            set.connect(tabName.getId(), ConstraintSet.END, tabBar.getId(), ConstraintSet.START);

            set.applyTo(parent);
        }

    }

    public void resetNameandBGConstraints(boolean left) {

        Log.i("Reset Constraints on ", "" + getTabName().getText().toString());
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        //Clear BG constraints
        set.clear(tabBG.getId(), ConstraintSet.START);
        set.clear(tabBG.getId(), ConstraintSet.END);
        //Clear Name Constraints
        set.clear(tabName.getId(), ConstraintSet.START);
        set.clear(tabName.getId(), ConstraintSet.END);
        set.applyTo(parent);

        set = new ConstraintSet();
        set.clone(parent);
        //Create New Connections
        // Constraint Connect Text to BG
        if (!left) {
            Log.i("Reset Constraints on", "Left");
            // Constraint Connect Text to Bar
            parentConst.connect(tabName.getId(), ConstraintSet.END, tabBG.getId(), ConstraintSet.END, (int) convertDpToPixel(5));
            // Tab BG
            parentConst.connect(tabBG.getId(), ConstraintSet.START, tabBar.getId(), ConstraintSet.END);
            //parentConst.connect(tabBG.getId(), ConstraintSet.END, tabName.getId(), ConstraintSet.END);
        } else {
            Log.i("Reset Constraints on", "Right");
            // Constraint Connect Text to Bar
            parentConst.connect(tabName.getId(), ConstraintSet.START, tabBG.getId(), ConstraintSet.START, (int) convertDpToPixel(5));
            // Tab BG
            parentConst.connect(tabBG.getId(), ConstraintSet.END, tabBar.getId(), ConstraintSet.START);
            //parentConst.connect(tabBG.getId(), ConstraintSet.START, tabName.getId(), ConstraintSet.START);
        }

        set.applyTo(parent);
    }

    private boolean isEven(int numb) {
        boolean isEven = true;
        isEven = numb % 2 == 0;

        return isEven;
    }

    private void openFragment() {

    }

    public float convertDpToPixel(float dp) {
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

    public FrameLayout getTabBG() {
        return tabBG;
    }
}
