package com.example.baseapp;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.baseapp.Fragments.FragmentOne;
import com.example.baseapp.Navigation.SideMenu;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SideMenu menu;
    ConstraintLayout parent;
    Toolbar toolbar;
    //FrameLayout main_content;
    //FrameLayout main_content_L;
    //FrameLayout main_content_R;
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
        //main_content = findViewById(R.id.maincontent);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        DisplayWidth = displayMetrics.widthPixels;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        parent = findViewById(R.id.parent);

        //Create Frames and Sliders
        CreateBaseFrames();


        MenuBars.add(new SideMenu(this, parent, "Contacts", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Camera", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Calendar", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Calculator", MenuBars));
        MenuBars.add(new SideMenu(this, parent, "Community", MenuBars));

        //openFragmentOne();
        setStatusbarspace();
        transparentStatus();

    }
    @ColorInt
    int color;
    ConstraintLayout.LayoutParams LP;
    ConstraintLayout.LayoutParams sliderLeft_LP;
    ConstraintLayout.LayoutParams sliderRight_LP;
    ConstraintLayout.LayoutParams sliderContainer_LP;

    int DisplayWidth;

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
    private int touchDownNav;

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                //Log.i("Mouse Down", "Yes");
                x1 = motionEvent.getX();
                generateNavigationPositions();


               // Log.i("Widths", "Center - "+centerFrame.getWidth()+" ; Left - "+leftFrame.getWidth()+" ; Right - "+ rightFrame.getWidth());

                break;
            case MotionEvent.ACTION_MOVE:
                //Log.i("Move", "Yes");
                x2 = motionEvent.getX();
                float deltaX = x2 - x1;
                int absoluteDeltaX = (int)Math.abs(deltaX);
                if (absoluteDeltaX > MIN_DISTANCE){
                    if(!moveTD){
                        showAllTabNames();
                        //Log.i("Move Left", "Yes");
                        touchDownNav = (int)motionEvent.getX();
                        moveTD = true;
                        if(deltaX > 0){
                            Log.i("Move Right", "Yes");
                            leftDir = false;
                            sliderToParent_Right();
                            setNavigationPositions((int)motionEvent.getY(), true);
                        }
                        else{
                            Log.i("Move Left", "Yes");
                            leftDir = true;
                            sliderToParent_Left();
                            setNavigationPositions((int)motionEvent.getY(), false);
                        }
                        //Fade In Menu Desc & Fade Out Main Window
                    }

                    navigateTo((int)motionEvent.getX());
                }
                return super.dispatchTouchEvent(motionEvent);

            case MotionEvent.ACTION_UP:
                //Log.i("Mouse Up", "Yes");
                if(moveTD) {
                    try {
                        resetLines(selectedBar, 300);
                        snapFrag();
                    } catch (Exception e) {
                    }
                }
                moveTD = false;
                break;
        }

        return super.dispatchTouchEvent(motionEvent);
    }

    boolean hasSnappedOpen = false;
    private void snapFrag() {
        float ratio = (float)moveCont / (float)DisplayWidth;

        //Check if the fragment is more than Half
        if(ratio > 0.5f){
         //Snap Open
            animateSliderWidth(200, moveCont, DisplayWidth, true);
            animateAlpha(200, alpha, 0.8f, selectedBar);
            if (previousSelectedBar != null){animateAlpha(100, 0.8f, 0.3f, previousSelectedBar);
            }
            animateAlpha(100, .8f, 0f, selectedSideMenu.tabName);
            hasSnappedOpen = true;
        }
        else{
            //Snap Close
            animateSliderWidth(200, moveCont, 0, false);
            animateAlpha(200, alpha, 0.3f, selectedBar);
            animateAlpha(100, .8f, 0f, selectedSideMenu.tabName);
            hasSnappedOpen = false;
        }

    }

    private void animateSliderWidth(int duration, float start, float end, final boolean open){
        final View layout = sliderContainer;
        final ConstraintLayout.LayoutParams LP = (ConstraintLayout.LayoutParams) layout.getLayoutParams();
        ValueAnimator va = ValueAnimator.ofFloat(start, end);
        va.setDuration(duration);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = Math.round((float) animation.getAnimatedValue());
               // Log.i("AnimVal", "= "+animValue);

                if(!leftDir){

                    LP.width = DisplayWidth - (int)animValue;
                    layout.setLayoutParams(LP);
                    //Log.i("In Left", "= True");
                }
                else{
                    LP.width = DisplayWidth - (int)animValue;
                    layout.setLayoutParams(LP);
                    //Log.i("In Right", "= True");
                }
            }

        });

            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(open) {
                        Log.i("Animation End", "True");
                        if (leftDir) {
                            removeSliderConnections_Left();
                        } else {
                            removeSliderConnections_Right();
                        }
                    }

                    hideAllTabNames();
                    resetSliderConstraint();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        va.start();
    }

    public void showAllTabNames(){
        for(SideMenu bar : MenuBars){
            bar.getTabName().setVisibility(View.VISIBLE);
        }
    }

    public void hideAllTabNames(){
        for(SideMenu bar : MenuBars){
            bar.getTabName().setVisibility(View.GONE);
            bar.getTabName().setAlpha(.3f);
        }
    }

    private void animateAlpha(int duration, float start, float end, final View layout){
        final ConstraintLayout.LayoutParams LP = (ConstraintLayout.LayoutParams) layout.getLayoutParams();
        ValueAnimator va = ValueAnimator.ofFloat(start, end);
        va.setDuration(duration);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = (float) animation.getAnimatedValue();
                //LP.leftMargin = (int)animValue;
                //Log.i("animateValue", ""+ animValue);
                layout.setAlpha(animValue);
            }
        });
        va.start();
    }
    int move, moveCont;
    float alpha;
    boolean leftDir;
    ConstraintLayout.LayoutParams sideBar_LP;
    SideMenu selectedSideMenu;
    FrameLayout selectedBar;
    FrameLayout previousSelectedBar;

    public void navigateTo(int x){
        move = (x - touchDownNav)/30;
        moveCont = x - touchDownNav;
        alpha = Math.abs(roundFloats((x - touchDownNav) * 0.004f, 2));
        if(alpha > .8f){
            alpha = .8f;
        }
        if(alpha < .3f){
            alpha = .3f;
        }
        selectedSideMenu.getTabBar().setAlpha(alpha);
        selectedSideMenu.getTabName().setAlpha(alpha);
        //Log.i("Left", ""+ moveCont);

        if(move < 0){
            //Moving Left
            move = -move;
            moveCont = -moveCont;
            sideBar_LP.rightMargin = move + (int) convertDpToPixel(3);
            selectedSideMenu.getTabBar().setLayoutParams(sideBar_LP);

            /*sliderRight_LP.rightMargin = moveCont;
            rightSlider.setLayoutParams(sliderRight_LP);
            //Change the Width of Slider Container*/
            sliderContainer_LP.width = DisplayWidth - moveCont;
            sliderContainer.setLayoutParams(sliderContainer_LP);


        }
        else{
            //Moving Right
            sideBar_LP.leftMargin = move + (int) convertDpToPixel(3);
            selectedSideMenu.getTabBar().setLayoutParams(sideBar_LP);

            /*sliderLeft_LP.leftMargin = moveCont;
            leftSlider.setLayoutParams(sliderLeft_LP);*/

            sliderContainer_LP.width = DisplayWidth - moveCont;
            sliderContainer.setLayoutParams(sliderContainer_LP);
        }

    }
    public void resetLines(final FrameLayout bar, int duration){
        //Log.i("Move", "= "+move);
        //Log.i("LeftDir", "= "+leftDir);
        //Log.i("barID", "= "+bar.getTag());
        final ConstraintLayout.LayoutParams LP = (ConstraintLayout.LayoutParams) bar.getLayoutParams();
        ValueAnimator va = ValueAnimator.ofFloat(move, convertDpToPixel(3));
        va.setDuration(duration);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = Math.round((float) animation.getAnimatedValue());
                //Log.i("AnimVal", "= "+animValue);

                if(!leftDir){
                    LP.leftMargin = (int)animValue;
                    bar.setLayoutParams(LP);
                    //Log.i("In Left", "= True");
                }
                else{
                    LP.rightMargin = (int)animValue;
                    bar.setLayoutParams(LP);
                    //Log.i("In Right", "= True");
                }
            }
        });
        va.start();
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
                        //Log.i("Open: ", ""+MenuBars.get(i).getTabBar().getTag());
                        selectedSideMenu = MenuBars.get(i);
                        sideBar_LP = (ConstraintLayout.LayoutParams) selectedSideMenu.getTabBar().getLayoutParams();
                        if (selectedBar != null) previousSelectedBar = selectedBar;
                        selectedBar = MenuBars.get(i).getTabBar();
                        break;
                    }
                }
            }
            else{
                if(!moveRight){
                    // check if y is within the range of bar
                    if(y > MenuBarTops.get(i) & y < MenuBarBottoms.get(i)){
                        //Log.i("Open: ", ""+ MenuBars.get(i).getTabBar().getTag());
                        selectedSideMenu = MenuBars.get(i);
                        sideBar_LP = (ConstraintLayout.LayoutParams) selectedSideMenu.getTabBar().getLayoutParams();
                        if (selectedBar != null) previousSelectedBar = selectedBar;
                        selectedBar = MenuBars.get(i).getTabBar();
                        break;
                    }
                }
            }
        }
    }


    //--------------------------- Dynamic Layout Code----------------------

    private FrameLayout leftFrame;
    private FrameLayout centerFrame;
    private FrameLayout rightFrame;
    private FrameLayout sliderContainer;

    int LayoutCount = 0;
    private void createCenterFrame(){
        LayoutCount++;
        centerFrame = new FrameLayout(this);
        ConstraintLayout.LayoutParams LP = new ConstraintLayout.LayoutParams(DisplayWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        centerFrame.setId(R.id.frame1);
        centerFrame.setLayoutParams(LP);
        centerFrame.addView(placeExampleText("Window "+LayoutCount));
        parent.addView(centerFrame);

    }
    private void createLeftFrame(){
        //Create FrameLayout for Left Fragment and Constraint Connect it to Left Slider
        //Create FameLayout
        LayoutCount++;
        leftFrame = new FrameLayout(this);
        ConstraintLayout.LayoutParams LP = new ConstraintLayout.LayoutParams(DisplayWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        leftFrame.setId(R.id.frame2);
        leftFrame.setLayoutParams(LP);
        leftFrame.setBackgroundColor(ContextCompat.getColor(this, R.color.themePrimaryLight));
        leftFrame.addView(placeExampleText("Window "+LayoutCount));
        parent.addView(leftFrame);

    }
    private void createRightFrame(){
        LayoutCount++;
        rightFrame = new FrameLayout(this);
        ConstraintLayout.LayoutParams LP = new ConstraintLayout.LayoutParams(DisplayWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        rightFrame.setId(R.id.frame3);
        rightFrame.setLayoutParams(LP);
        rightFrame.setBackgroundColor(ContextCompat.getColor(this, R.color.baseColorTwo));
        rightFrame.addView(placeExampleText("Window "+LayoutCount));
        parent.addView(rightFrame);

    }

    private void createSliderContainer(){
        sliderContainer= new FrameLayout(this);
        sliderContainer_LP = new ConstraintLayout.LayoutParams(DisplayWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        sliderContainer.setId(View.generateViewId());
        sliderContainer.setLayoutParams(sliderContainer_LP);
        parent.addView(sliderContainer);
    }

    private TextView placeExampleText(String text){
        TextView exmpl = new TextView(this);
        FrameLayout.LayoutParams LP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LP.gravity = Gravity.CENTER;
        exmpl.setText(text);
        exmpl.setTextColor(Color.BLACK);
        exmpl.setLayoutParams(LP);
        return exmpl;
    }

    private void BaseConnectLayouts(){
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        //-------Base Connection of all Frames
        set.connect(centerFrame.getId(), ConstraintSet.START, parent.getId(), ConstraintSet.START);
        //Connect left Frame to center Frame
        set.connect(leftFrame.getId(), ConstraintSet.END, centerFrame.getId(), ConstraintSet.START);
        //Connect right Frame to Center Frame
        set.connect(rightFrame.getId(), ConstraintSet.START, centerFrame.getId(), ConstraintSet.END);
        set.applyTo(parent);
    }

    private void removeAllConnections(){
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        //first remove all connections

        set.clear(leftFrame.getId(), ConstraintSet.START);
        set.clear(leftFrame.getId(), ConstraintSet.END);

        set.clear(centerFrame.getId(), ConstraintSet.START);
        set.clear(centerFrame.getId(), ConstraintSet.END);

        set.clear(rightFrame.getId(), ConstraintSet.START);
        set.clear(rightFrame.getId(), ConstraintSet.END);

        set.clear(sliderContainer.getId(), ConstraintSet.START);
        set.clear(sliderContainer.getId(), ConstraintSet.END );

        set.applyTo(parent);
    }

    private void sliderToParent_Left(){
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        set.clear(centerFrame.getId(), ConstraintSet.START);
        set.clear(centerFrame.getId(), ConstraintSet.END);
        set.connect(sliderContainer.getId(), ConstraintSet.START, parent.getId(), ConstraintSet.START);
        set.connect(centerFrame.getId(), ConstraintSet.END, sliderContainer.getId(), ConstraintSet.END);
        set.applyTo(parent);
    }

    private void sliderToParent_Right(){
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        set.clear(centerFrame.getId(), ConstraintSet.START);
        set.clear(centerFrame.getId(), ConstraintSet.END);
        set.connect(sliderContainer.getId(), ConstraintSet.END, parent.getId(), ConstraintSet.END);
        set.connect(centerFrame.getId(), ConstraintSet.START, sliderContainer.getId(), ConstraintSet.START);
        set.applyTo(parent);
    }

    private void resetSliderConstraint(){
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        set.clear(sliderContainer.getId(), ConstraintSet.START);
        set.clear(sliderContainer.getId(), ConstraintSet.END);
        set.clear(centerFrame.getId(), ConstraintSet.START);
        set.clear(centerFrame.getId(), ConstraintSet.END);
        set.applyTo(parent);
    }

    private void removeSliderConnections_Left(){
        removeAllConnections();
        FrameLayout puffer;

        //resize Slider Cont to Display Size
        sliderContainer_LP.width = DisplayWidth;
        sliderContainer.setLayoutParams(sliderContainer_LP);
        Log.i("Moving", "- Right");
        //first remove all connections

        puffer = leftFrame;
        leftFrame = centerFrame;
        centerFrame = rightFrame;
        rightFrame = puffer;

        //Reconnect All Frames
        BaseConnectLayouts();
        //sliderToParent_Left();

    }

    private void removeSliderConnections_Right(){
        removeAllConnections();
        FrameLayout puffer;

        //resize Slider Cont to Display Size
        sliderContainer_LP.width = DisplayWidth;
        sliderContainer.setLayoutParams(sliderContainer_LP);
        //Remove Fragment from leftFrame
        Log.i("Moving", "- Left");
        //first remove all connections

        puffer = rightFrame;
        rightFrame = centerFrame;
        centerFrame = leftFrame;
        leftFrame = puffer;

        //Reconnect All Frames
        BaseConnectLayouts();
        //sliderToParent_Right();

    }

    private void CreateBaseFrames(){
        createCenterFrame();
        createLeftFrame();
        createRightFrame();
        createSliderContainer();
        BaseConnectLayouts();
    }

    //-------------------------  Evaluation and System Settings Code -----------------------
    public static float roundFloats(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
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
        centerFrame.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentOne();
        // Begin the transaction
        //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace the contents of the container with the new fragment
        ft.replace(centerFrame.getId(), fragment);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        //showMainWindow();
    }

    private void openPreferences(){
        fragmentNavigation("pref");
        centerFrame.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Begin the transaction
        fragment = new PreferenceFragment();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace the contents of the container with the new fragment
        ft.replace(centerFrame.getId(), fragment);
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

    public void setDarkSysBar(){
        //Log.i("Dark","True");
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
    public  float convertDpToPixel(float dp){
        return dp * ((float) this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


}
