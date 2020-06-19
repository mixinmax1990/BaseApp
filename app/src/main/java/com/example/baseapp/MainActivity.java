package com.example.baseapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.baseapp.Fragments.FragmentFour;
import com.example.baseapp.Fragments.FragmentOne;
import com.example.baseapp.Fragments.FragmentThree;
import com.example.baseapp.Fragments.FragmentTwo;
import com.example.baseapp.Interfaces.FinishedInflating;
import com.example.baseapp.Interfaces.NavigationPosition;
import com.example.baseapp.Interfaces.SwitchFrames;
import com.example.baseapp.Navigation.NavigationFrames;
import com.example.baseapp.Navigation.SideMenu;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationPosition, SwitchFrames, FinishedInflating {

    public FrameLayout leftFrame;
    public FrameLayout centerFrame;
    public FrameLayout rightFrame;
    SideMenu menu;
    ConstraintLayout parent;
    Toolbar toolbar;
    int DisplayWidth;
    FrameLayout statusbarspace;
    //FrameLayout main_content;
    //FrameLayout main_content_L;
    //FrameLayout main_content_R;
    //Menu Bars
    private List<SideMenu> MenuBars = new ArrayList<SideMenu>();
    private List<Integer> MenuBarTops = new ArrayList<Integer>();
    private List<Integer> MenuBarBottoms = new ArrayList<Integer>();
    private NavigationFrames NavigationFrames;
    private Fragment fragment;
    private String curFragment;
    private String lastFragment;
    private int statusBarHeight;

    //-------------------------  Evaluation and System Settings Code -----------------------
    public static float roundFloats(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeMode();
        setContentView(R.layout.activity_main);
        //main_content = findViewById(R.id.maincontent);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        DisplayWidth = displayMetrics.widthPixels;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        parent = findViewById(R.id.parent);

        //Create Frames and Sliders
        NavigationFrames = new NavigationFrames(parent, this, DisplayWidth, this, this);

        MenuBars.add(new SideMenu(this, parent, "News", MenuBars, "standard"));
        MenuBars.add(new SideMenu(this, parent, "Videos", MenuBars, "standard"));
        MenuBars.add(new SideMenu(this, parent, "Maps", MenuBars, "standard"));
        MenuBars.add(new SideMenu(this, parent, "Friends", MenuBars, "standard"));
        MenuBars.add(new SideMenu(this, parent, "Settings", MenuBars, "setleft"));


        NavigationFrames.setMenuBars(MenuBars);
        leftFrame = NavigationFrames.leftFrame;
        centerFrame = NavigationFrames.centerFrame;
        rightFrame = NavigationFrames.rightFrame;

        //openFragmentOne();
        setStatusbarspace();
        transparentStatus();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                NavigationFrames.ActionDown(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:

                NavigationFrames.ActionMove(motionEvent);
                return super.dispatchTouchEvent(motionEvent);

            case MotionEvent.ACTION_UP:
                NavigationFrames.ActionUp(motionEvent);
                break;
        }

        return super.dispatchTouchEvent(motionEvent);
    }

    private void setThemeMode() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkPref = prefs.contains("dark_mode");
        if (darkPref) {
            boolean dark = prefs.getBoolean("dark_mode", false);

            if (dark) {
                dark = true;
                //set the Theme to Dark
                setTheme(R.style.AppThemeDark);
                setDarkSysBar();

            } else {
                dark = false;
                //set the Theme To Light
                setTheme(R.style.AppThemeLight);
                setWhiteSysBar();
            }
        }
    }

    private boolean isLeft(int numb) {
        boolean isEven;
        isEven = numb % 2 == 0;

        return isEven;
    }

    //Open Fragments
    private void openFragmentOne(FrameLayout frame) {
        fragmentNavigation("one");
        frame.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentOne();
        // Begin the transaction
        //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace the contents of the container with the new fragment
        ft.replace(frame.getId(), fragment);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        //showMainWindow();
    }

    private void openFragmentTwo(FrameLayout frame) {
        fragmentNavigation("two");
        frame.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentTwo();
        // Begin the transaction
        //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace the contents of the container with the new fragment
        ft.replace(frame.getId(), fragment);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        //showMainWindow();
    }

    private void openFragmentThree(FrameLayout frame) {
        fragmentNavigation("three");
        frame.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentThree();
        // Begin the transaction
        //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace the contents of the container with the new fragment
        ft.replace(frame.getId(), fragment);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        //showMainWindow();
    }

    private void openFragmentFour(FrameLayout frame) {
        fragmentNavigation("four");
        frame.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentFour();
        // Begin the transaction
        //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace the contents of the container with the new fragment
        ft.replace(frame.getId(), fragment);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        //showMainWindow();
    }

    private void openPreferences(FrameLayout Frame) {
        fragmentNavigation("pref");
        Frame.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Begin the transaction
        fragment = new PreferenceFragment();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace the contents of the container with the new fragment
        ft.replace(Frame.getId(), fragment);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        //getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.main_content, new PreferenceFragment()).commit();

    }

    private void fragmentNavigation(String cur) {
        if (curFragment == null) {
            curFragment = cur;
        } else {
            lastFragment = curFragment;
            curFragment = cur;
        }

    }

    @Override
    public void onBackPressed() {

        //Load Previos Fragment
        if (lastFragment.isEmpty()) {
            super.onBackPressed();
        } else {
            switch (lastFragment) {
                case "one":
                    //openFragmentOne();
                    break;
                case "pref":
                    // openPreferences();
                    break;
                default:
                    super.onBackPressed();
                    break;
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.moreitems, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                //openPreferences();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void setWhiteSysBar() {
//Log.i("Dark","False");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = getWindow().getDecorView();
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
            //ContextCompat.getColor(this,R.color.white)
        }
    }

    public void setDarkSysBar() {
        //Log.i("Dark","True");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = getWindow().getDecorView();
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            //flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.cardPrimaryDark));

            //ContextCompat.getColor(this,R.color.white)

        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setStatusbarspace() {
        statusbarspace = findViewById(R.id.statusspace);
        statusbarspace.setVisibility(View.VISIBLE);
        statusBarHeight = getStatusBarHeight();

        ConstraintLayout.LayoutParams sbsLP = (ConstraintLayout.LayoutParams) statusbarspace.getLayoutParams();
        sbsLP.height = statusBarHeight;
        statusbarspace.setLayoutParams(sbsLP);
    }

    public void transparentStatus() {
        statusbarspace = findViewById(R.id.statusspace);

        //statusbarspace.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = getWindow().getDecorView();
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            //ContextCompat.getColor(this,R.color.white)

        }
    }

    public float convertDpToPixel(float dp) {
        return dp * ((float) this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    @Override
    public void gotPosition(boolean isLeft, String tab) {
        Log.i("Got Position", "" + tab);
        Log.i("Left DIr", "" + isLeft);
        FrameLayout frame;
        if (isLeft) {
            frame = rightFrame;
        } else {
            frame = leftFrame;
        }
        switch (tab) {
            case "Settings":
                //Open Settings fragement
                openPreferences(frame);
                break;
            case "News":
                openFragmentOne(frame);
                break;
            case "Maps":
                openFragmentTwo(frame);
                break;
            case "Friends":
                openFragmentThree(frame);
                break;
            case "Videos":
                openFragmentFour(frame);
                break;
            default:
                break;
        }
    }

    @Override
    public void switchFrames(FrameLayout leftFrame, FrameLayout centerFrame, FrameLayout rightFrame) {

        this.leftFrame = leftFrame;
        this.centerFrame = centerFrame;
        this.rightFrame = rightFrame;
    }

    @Override
    public void inflated(boolean done) {
        NavigationFrames.progressBar.setVisibility(View.GONE);

    }
}
