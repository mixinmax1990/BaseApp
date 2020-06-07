package com.example.baseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.baseapp.Fragments.FragmentOne;
import com.example.baseapp.Navigation.SideMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SideMenu menu;
    ConstraintLayout parent;
    Toolbar toolbar;
    FrameLayout main_content;

    //Menu Bars
    private List<SideMenu> MenuBars = new ArrayList<SideMenu>();
    private List<Integer> MenuBarTops = new ArrayList<Integer>();
    private List<Integer> MenuBarBottoms = new ArrayList<Integer>();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkPref = prefs.contains("dark_mode");
        if(darkPref){
            boolean dark = prefs.getBoolean("dark_mode",false);

            if(dark){
                dark = true;
                //set the Theme to Dark
                setTheme(R.style.AppThemeDark);
                setDarkSysBar();

            }
            else{
                dark = false;
                //set the Theme To Light
                setTheme(R.style.AppThemeLight);
                setWhiteSysBar();
            }
        }

        setContentView(R.layout.activity_main);
        main_content = findViewById(R.id.maincontent);
        openFragmentOne();
        setStatusbarspace();
        transparentStatus();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        parent = findViewById(R.id.parent);
        MenuBars.add(new SideMenu(this, parent, "Contacts", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Camera", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Calendar", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Calculator", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Community", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Collaborations", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Contacts", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Camera", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Calendar", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Calculator", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Community", MenuBars));
    }

    private void generateNavigationPositions() {

        for(SideMenu bar : MenuBars){
            MenuBarTops.add(bar.getTabBar().getTop());
            MenuBarBottoms.add(bar.getTabBar().getBottom());
        }

    }

    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private boolean windowDimmed = false;
    private boolean moveTD = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                //Log.i("Mouse Down", "Yes");
                x1 = motionEvent.getX();
                generateNavigationPositions();
                break;

            case MotionEvent.ACTION_MOVE:
                //Log.i("Move", "Yes");
                x2 = motionEvent.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE){
                    if(!moveTD){
                        //Log.i("Move Left", "Yes");
                        moveTD = true;
                        if(deltaX > 0){
                            Log.i("Move Right", "Yes");
                            setNavigationPositions((int)motionEvent.getY(), true);
                        }
                        else{
                            Log.i("Move Left", "Yes");
                            setNavigationPositions((int)motionEvent.getY(), false);
                        }
                    }

                }
                return super.dispatchTouchEvent(motionEvent);

            case MotionEvent.ACTION_UP:
                //Log.i("Mouse Up", "Yes");
                moveTD = false;
                break;
        }

        return super.dispatchTouchEvent(motionEvent);
    }

    public void setNavigationPositions(int y, boolean moveRight){
        int humanPosition;
        for(int i = 0;i < MenuBars.size(); i++){

            //Log.i("Menu Bar Top", ""+MenuBarTops.get(i)+" -- MenuBar Tag"+ MenuBars.get(i).getTag());

            humanPosition = i + 1;
            //Log.i("Position", ""+humanPosition);
            if(isLeft(humanPosition)){
                if(moveRight){
                    // check if y is within the range of bar
                    if(y > MenuBarTops.get(i) & y < MenuBarBottoms.get(i)){
                        Log.i("Open: ", ""+MenuBars.get(i).getTabBar().getTag());
                        break;
                    }
                }
            }
            else{
                if(!moveRight){
                    // check if y is within the range of bar
                    if(y > MenuBarTops.get(i) & y < MenuBarBottoms.get(i)){
                        Log.i("Open: ", ""+ MenuBars.get(i).getTabBar().getTag());
                        break;
                    }
                }
            }
        }
    }

    private boolean isLeft(int numb){
        boolean isEven;
        if (numb % 2 == 0)
            isEven = true;
        else
            isEven = false;

        return isEven;
    }

    private Fragment fragment;
    private String curFragment;
    private String lastFragment;
    //Open Fragments
    private void openFragmentOne(){
        fragmentNavigation("one");
        main_content.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentOne();
        // Begin the transaction
        //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.maincontent, fragment);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        //showMainWindow();
    }

    private void openPreferences(){
        fragmentNavigation("pref");
        main_content.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Begin the transaction
        fragment = new PreferenceFragment();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.maincontent, fragment);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        //getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.main_content, new PreferenceFragment()).commit();

    }

    private void fragmentNavigation(String cur){
        if(curFragment == null){
            curFragment = cur;
        }
        else{
            lastFragment = curFragment;
            curFragment = cur;
        }

    }
    @Override
    public void onBackPressed() {

        //Load Previos Fragment
        if(lastFragment.isEmpty()){
            super.onBackPressed();
        }
        else{
            switch(lastFragment){
                case "one":
                    openFragmentOne();
                    break;
                case "pref":
                    openPreferences();
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
        switch (item.getItemId()){
            case R.id.item1:
                openPreferences();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void setWhiteSysBar(){
Log.i("Dark","False");
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

    public void setDarkSysBar(){
        Log.i("Dark","True");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = getWindow().getDecorView();
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            //flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.cardPrimaryDark));

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
    FrameLayout statusbarspace;
    private int statusBarHeight;

    public void setStatusbarspace(){
        statusbarspace = findViewById(R.id.statusspace);
        statusbarspace.setVisibility(View.VISIBLE);
        statusBarHeight = getStatusBarHeight();

        ConstraintLayout.LayoutParams sbsLP = (ConstraintLayout.LayoutParams) statusbarspace.getLayoutParams();
        sbsLP.height = statusBarHeight;
        statusbarspace.setLayoutParams(sbsLP);
    }
    public void transparentStatus(){
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


}
