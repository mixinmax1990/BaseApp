package com.example.baseapp.Navigation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.baseapp.Interfaces.NavigationPosition;
import com.example.baseapp.Interfaces.SwitchFrames;
import com.example.baseapp.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NavigationFrames {
    static final int MIN_DISTANCE = 50;
    //Menu Bar Lists
    public List<SideMenu> MenuBars = new ArrayList<SideMenu>();
    public List<Integer> MenuBarTops = new ArrayList<Integer>();
    public List<Integer> MenuBarBottoms = new ArrayList<Integer>();
    //Condition Params
    public float x1, x2, y1, y2;
    public boolean windowDimmed = false;
    public boolean moveTD = false;
    public int touchDownNav;
    public boolean verticalScroll = false;
    public boolean horizontalScroll = false;
    //Layout Definitions
    public ConstraintLayout.LayoutParams sideBar_LP;
    public FrameLayout selectedBar;
    public FrameLayout previousSelectedBar;
    public FrameLayout leftFrame;
    public FrameLayout centerFrame;
    public FrameLayout rightFrame;
    public FrameLayout sliderContainer;
    public ProgressBar progressBar;
    public CardView slideCard;
    //---------------Algorythmic
    //Class Objects
    public SideMenu selectedSideMenu;
    float x, y;
    ConstraintLayout parent;
    Context context;
    ConstraintLayout.LayoutParams sliderContainer_LP;
    int DisplayWidth;
    float movePercentage;
    int tabNameWidth;
    boolean hasSnappedOpen = false;
    boolean snapedName = false;
    int move, moveCont;
    float alpha;
    boolean leftDir;
    boolean namesHidden = true;
    //Value Animator
    ValueAnimator va;
    int LayoutCount = 0;
    //Interfaces
    private NavigationPosition navigationPosition;
    private SwitchFrames switchFrames;

    public NavigationFrames(ConstraintLayout parent, Context context, int DisplayWidth, NavigationPosition navigationPosition, SwitchFrames switchFrames) {
        this.parent = parent;
        this.context = context;
        this.DisplayWidth = DisplayWidth;

        CreateBaseFrames();

        setNavigationPosition(navigationPosition);
        setSwitchFrames(switchFrames);

    }

    //Setting Functions
    public static float roundFloats(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public void setNavigationPosition(NavigationPosition navigationPosition) {
        this.navigationPosition = navigationPosition;
    }

    public void setSwitchFrames(SwitchFrames switchFrames) {
        this.switchFrames = switchFrames;
    }

    public void setMenuBars(List<SideMenu> menuBars) {
        MenuBars = menuBars;
    }

    //Touchscreen Function
    public void ActionDown(MotionEvent motionEvent) {
//Log.i("Mouse Down", "Yes");
        x1 = motionEvent.getX();
        y1 = motionEvent.getY();
        generateNavigationPositions();
        // Log.i("Widths", "Center - "+centerFrame.getWidth()+" ; Left - "+leftFrame.getWidth()+" ; Right - "+ rightFrame.getWidth());
    }

    public void ActionMove(MotionEvent motionEvent) {
        x2 = motionEvent.getX();
        y2 = motionEvent.getY();
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;
        int absoluteDeltaX = (int) Math.abs(deltaX);
        int absoluteDeltaY = (int) Math.abs(deltaY);
        if (absoluteDeltaY > MIN_DISTANCE & !horizontalScroll) {
            //up-down Scrolling happening
            verticalScroll = true;
        }
        if (absoluteDeltaX > MIN_DISTANCE & !verticalScroll) {
            horizontalScroll = true;
            if (!moveTD) {

                //Log.i("Move Left", "Yes");
                touchDownNav = (int) motionEvent.getX();
                moveTD = true;
                if (deltaX > 0) {
                    Log.i("Move Right", "Yes");
                    leftDir = false;
                    sliderToParent_Right();
                    setNavigationPositions((int) motionEvent.getY(), true);
                    tabNameWidth = selectedSideMenu.tabBar.getWidth() + (selectedSideMenu.tabName.getWidth() + MIN_DISTANCE);
                    //Log.i("TabNameWIdth", ""+tabNameWidth);

                    //Connect ProgressBar to new Frame
                    clearProgressConstraints();
                    connectProgressToFrame(leftFrame);
                } else {
                    Log.i("Move Left", "Yes");
                    leftDir = true;
                    sliderToParent_Left();
                    setNavigationPositions((int) motionEvent.getY(), false);
                    tabNameWidth = selectedSideMenu.tabBar.getWidth() + (selectedSideMenu.tabName.getWidth() + MIN_DISTANCE);
                    //Log.i("TabNameWIdth", ""+tabNameWidth);

                    //Connect ProgressBar to new Frame
                    clearProgressConstraints();
                    connectProgressToFrame(rightFrame);
                }
                navigationPosition.gotPosition(leftDir, selectedSideMenu.getTabName().getText().toString());
            }

            navigateTo((int) motionEvent.getX());
        }

    }

    public void ActionUp(MotionEvent motionEvent) {
        if (moveTD) {
            try {
                resetLines(selectedBar, 300);
                snapFrag();
            } catch (Exception e) {
            }
        }
        verticalScroll = false;
        horizontalScroll = false;
        moveTD = false;

    }

    //Object Essential Functions
    public void generateNavigationPositions() {
        for (SideMenu bar : MenuBars) {
            MenuBarTops.add(bar.getTabBar().getTop());
            MenuBarBottoms.add(bar.getTabBar().getBottom());
        }
    }

    public void animateSliderWidth(int duration, float start, float end, final boolean open) {
        final View layout = sliderContainer;
        final ConstraintLayout.LayoutParams LP = (ConstraintLayout.LayoutParams) layout.getLayoutParams();
        ValueAnimator va = ValueAnimator.ofFloat(start, end);
        va.setDuration(duration);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = Math.round((float) animation.getAnimatedValue());
                // Log.i("AnimVal", "= "+animValue);

                if (!leftDir) {
                    LP.width = DisplayWidth - (int) animValue;
                    layout.setLayoutParams(LP);
                    //Log.i("In Left", "= True");
                } else {
                    LP.width = DisplayWidth - (int) animValue;
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
                if (open) {
                    Log.i("Animation End", "True");
                    if (leftDir) {
                        removeSliderConnections_Left();
                    } else {
                        removeSliderConnections_Right();
                    }
                    //unsnapNameToFrame(true);
                } else {
                    //unsnapNameToFrame(false);

                }

                //selectedSideMenu.resetNameandBGConstraints(leftDir);
                //hideAllTabNamesExcSelect();
                namesHidden = true;
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

    public void setNavigationPositions(int y, boolean moveRight) {
        int humanPosition;
        //slideCard.setVisibility(View.VISIBLE);
        for (int i = 0; i < MenuBars.size(); i++) {
            //connectSlideCard();

            //Log.i("Menu Bar Top", ""+MenuBarTops.get(i)+" -- MenuBar Tag"+ MenuBars.get(i).getTag());

            humanPosition = i + 1;
            //Log.i("Position", ""+humanPosition);
            if (isLeft(humanPosition)) {
                if (moveRight) {
                    // check if y is within the range of bar
                    if (y > MenuBarTops.get(i) & y < MenuBarBottoms.get(i)) {
                        //Log.i("Open: ", ""+MenuBars.get(i).getTabBar().getTag());
                        if (selectedSideMenu != null) {
                            selectedSideMenu.resetNameandBGConstraints(true);
                        }
                        selectedSideMenu = MenuBars.get(i);
                        //COnnect BG and Name
                        selectedSideMenu.connectBGToName(true);
                        //connectSlideCard();
                        sideBar_LP = (ConstraintLayout.LayoutParams) selectedSideMenu.getTabBar().getLayoutParams();
                        if (selectedBar != null) previousSelectedBar = selectedBar;
                        selectedBar = MenuBars.get(i).getTabBar();
                        break;
                    }
                }
            } else {
                if (!moveRight) {
                    // check if y is within the range of bar
                    if (y > MenuBarTops.get(i) & y < MenuBarBottoms.get(i)) {
                        if (selectedSideMenu != null) {
                            selectedSideMenu.resetNameandBGConstraints(false);
                        }
                        //Log.i("Open: ", ""+ MenuBars.get(i).getTabBar().getTag());
                        selectedSideMenu = MenuBars.get(i);
                        //COnnect BG to NAme
                        selectedSideMenu.connectBGToName(false);
                        sideBar_LP = (ConstraintLayout.LayoutParams) selectedSideMenu.getTabBar().getLayoutParams();
                        if (selectedBar != null) previousSelectedBar = selectedBar;
                        selectedBar = MenuBars.get(i).getTabBar();
                        break;
                    }
                }
            }
        }
    }

    public void CreateBaseFrames() {
        createCenterFrame();
        createLeftFrame();
        createRightFrame();
        createSlideCard();
        createProgressBar();
        createSliderContainer();

        BaseConnectLayouts();
        connectProgressToFrame(centerFrame);
    }

    public void navigateTo(int x) {
        move = (x - touchDownNav) / 40;
        moveCont = x - touchDownNav;
        alpha = Math.abs(roundFloats((x - touchDownNav) * 0.004f, 2));
        if (alpha > .8f) {
            alpha = .8f;
        }
        if (alpha < .5f) {
            alpha = .5f;
        }

        selectedSideMenu.getTabBar().setAlpha(alpha);
        selectedSideMenu.getTabName().setAlpha(alpha);
        //Log.i("Left", ""+ moveCont);

        if (move < 0) {
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


        } else {
            //Moving Right
            sideBar_LP.leftMargin = move + (int) convertDpToPixel(3);
            selectedSideMenu.getTabBar().setLayoutParams(sideBar_LP);

            /*sliderLeft_LP.leftMargin = moveCont;
            leftSlider.setLayoutParams(sliderLeft_LP);*/

            sliderContainer_LP.width = DisplayWidth - moveCont;
            sliderContainer.setLayoutParams(sliderContainer_LP);
        }
        //openBarBY_X(moveCont);

        if (!snapedName) {
            //check if move frame has reached the edge of name
            if (moveCont > tabNameWidth) {
                snapNameToFrame();
            }
            selectedSideMenu.getTabName().setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

        } else {
            //Check if has been slided back
            if (moveCont < tabNameWidth) {
                unsnapNameToFrame(false);
            }
            int size = 11 + ((moveCont - tabNameWidth) / 80);
            selectedSideMenu.getTabName().setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        }

        movePercentage = (float) moveCont / (float) DisplayWidth;


        //hideAllTabNames_By(movePercentage);

        //margin set Names
        slideInNames_BY_X(movePercentage, 0f, 0.1f);
        //fade in Card at 0 till 0.1f
        float sc_x_in = distanceFade(true, movePercentage, 0f, .1f) * .5f;
        if (sc_x_in <= .5f) {
            //slideCard.setAlpha(sc_x_in);
        }
        //Log.i("DISTANCE One", " - "+sc_x_in);

        //fade out Card from 0.3 to 0.4
        float sc_x_out = distanceFade(false, movePercentage, .5f, .6f) * .5f;
        if (sc_x_out <= .5f) {
            //slideCard.setAlpha(sc_x_out);
        }
        //Log.i("DISTANCE Two", " - "+sc_x_out);

        if (movePercentage > 0.4f) {
            if (!namesHidden) {
                hideAllTabNamesExcSelect();
                namesHidden = true;
            }
        } else {
            if (namesHidden) {
                showAllTabNames();
                namesHidden = false;
            }
        }

    }

    public void showAllTabNames() {
        for (SideMenu bar : MenuBars) {
            //fadeViewAnimation(50, 0f, 0.5f, bar.getTabName());
            bar.getTabName().setAlpha(.0f);
        }
    }

    //Functions with Move Parameters
    public void hideAllTabNames_By(float x) {
        //fadebetween the two values
        float start = .3f;
        float end = .4f;

        //determin alpha
        float alpha = ((x - start) * 10);

        Log.i("MobveALPHA", "" + alpha);

        if (x > start & x < end) {
            // SLide is in fade range
            for (SideMenu bar : MenuBars) {
                bar.getTabName().setAlpha(alpha);
            }
        }
    }

    public void slideInNames_BY_X(float x, float start, float end) {
        ConstraintLayout.LayoutParams LP;
        float margin = (1f - (end - x) / (end - start)) * 20;
        float alpha = (1f - (end - x) / (end - start)) / 7;


        for (SideMenu bar : MenuBars) {

            LP = (ConstraintLayout.LayoutParams) bar.getTabBG().getLayoutParams();

            if (bar != selectedSideMenu) {

                LP.width = (int) margin;
                bar.getTabBG().setLayoutParams(LP);

                if (alpha > .3f) {
                    alpha = .3f;

                }
            }
            bar.getTabName().setAlpha(alpha);
        }
    }

    public float distanceFade(boolean show, float dist, float rangeStart, float rangeEnd) {
        float alpha;
        if (dist > rangeStart & dist < rangeEnd) {
            if (show) {
                alpha = 1f - (rangeEnd - dist) / (rangeEnd - rangeStart);
                if (dist < rangeStart) alpha = 0f;
                else if (dist > rangeEnd) alpha = 1f;
            } else {
                alpha = (rangeEnd - dist) / (rangeEnd - rangeStart);
                if (dist < rangeStart) alpha = 1f;
                else if (dist > rangeEnd) alpha = 0f;
            }
        } else if (dist > rangeEnd) {
            if (show) {
                alpha = 1f;
            } else {
                alpha = 0f;
            }
        } else {
            if (show) {
                alpha = 0f;
            } else {
                alpha = 1f;
            }
        }
        return alpha;
    }

    //Hide Functions
    public void hideAllTabNamesExcSelect() {
        for (SideMenu bar : MenuBars) {
            if (bar.getTabName() != selectedSideMenu.getTabName()) {
                //fadeViewAnimation(200, .5f, 0f, bar.getTabName())
                bar.getTabName().setAlpha(0f);
            }
        }
    }

    public void animateAlpha(int duration, float start, float end, final View layout) {
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

    //Snap Functions
    public void snapNameToFrame() {

        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        if (leftDir) {
            //remove all connections and connect BG to Name
            //create new name Connections
            set.connect(selectedSideMenu.getTabName().getId(), ConstraintSet.START, rightFrame.getId(), ConstraintSet.START);
            set.connect(selectedSideMenu.getTabName().getId(), ConstraintSet.END, selectedSideMenu.getTabBar().getId(), ConstraintSet.START);
        } else {
            //remove all connections and connect BG to Name
            //Create new Name Connections
            set.connect(selectedSideMenu.getTabName().getId(), ConstraintSet.END, leftFrame.getId(), ConstraintSet.END);
            set.connect(selectedSideMenu.getTabName().getId(), ConstraintSet.START, selectedSideMenu.getTabBar().getId(), ConstraintSet.END);
        }
        set.applyTo(parent);
        snapedName = true;
    }

    public void unsnapNameToFrame(boolean open) {

        selectedSideMenu.getTabName().setAlpha(0f);
        selectedSideMenu.getTabName().setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        selectedSideMenu.getTabName().setAlpha(0f);

        snapedName = false;

        slideCard.setVisibility(View.GONE);
        //fadeViewAnimation(50, 1f, 0f, slideCard);
        //clearSlideCardConnections();
    }

    public void snapFrag() {
        //Check if the fragment is more than Half
        if (movePercentage > 0.5f) {
            //Snap Open
            animateSliderWidth(200, moveCont, DisplayWidth, true);
            animateAlpha(200, alpha, 0.8f, selectedBar);
            if (previousSelectedBar != null) {
                animateAlpha(100, 0.8f, 0.5f, previousSelectedBar);
            }
            //animateAlpha(100, .8f, 0f, selectedSideMenu.tabName);
            hasSnappedOpen = true;
        } else {
            //Snap Close
            animateSliderWidth(200, moveCont, 0, false);
            animateAlpha(200, alpha, 0.5f, selectedBar);
            animateAlpha(100, .8f, 0f, selectedSideMenu.tabName);
            hasSnappedOpen = false;
        }

    }
    //--------------------------- Dynamic Layout Code----------------------

    //Animator Functions
    public void resetLines(final FrameLayout bar, int duration) {
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

                if (!leftDir) {
                    LP.leftMargin = (int) animValue;
                    bar.setLayoutParams(LP);
                    //Log.i("In Left", "= True");
                } else {
                    LP.rightMargin = (int) animValue;
                    bar.setLayoutParams(LP);
                    //Log.i("In Right", "= True");
                }
            }
        });
        va.start();
    }

    public void fadeViewAnimation(int duration, final float start, final float end, final View view) {

        va = ValueAnimator.ofFloat(start, end);
        va.setDuration(duration);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = (float) animation.getAnimatedValue();
                //Log.i("AnimVal", "= "+animValue);
                view.setAlpha(animValue);
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (start == 0f) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (end == 0f) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        va.start();

    }

    public void createCenterFrame() {
        LayoutCount++;
        centerFrame = new FrameLayout(context);
        ConstraintLayout.LayoutParams LP = new ConstraintLayout.LayoutParams(DisplayWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        centerFrame.setId(R.id.frame1);
        centerFrame.setLayoutParams(LP);
        //centerFrame.addView(placeProgressBar());
        parent.addView(centerFrame);

    }

    public void createLeftFrame() {
        //Create FrameLayout for Left Fragment and Constraint Connect it to Left Slider
        //Create FameLayout
        LayoutCount++;
        leftFrame = new FrameLayout(context);
        ConstraintLayout.LayoutParams LP = new ConstraintLayout.LayoutParams(DisplayWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        leftFrame.setId(R.id.frame2);
        leftFrame.setLayoutParams(LP);
        leftFrame.setBackgroundColor(ContextCompat.getColor(context, R.color.chatBaseBG));
        //leftFrame.addView(placeProgressBar());
        parent.addView(leftFrame);

    }

    public void createRightFrame() {
        LayoutCount++;
        rightFrame = new FrameLayout(context);
        ConstraintLayout.LayoutParams LP = new ConstraintLayout.LayoutParams(DisplayWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        rightFrame.setId(R.id.frame3);
        rightFrame.setLayoutParams(LP);
        rightFrame.setBackgroundColor(ContextCompat.getColor(context, R.color.baseColorTwo));
        //rightFrame.addView(placeProgressBar());
        parent.addView(rightFrame);

    }

    public void createSliderContainer() {
        sliderContainer = new FrameLayout(context);
        sliderContainer_LP = new ConstraintLayout.LayoutParams(DisplayWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        sliderContainer.setId(View.generateViewId());
        sliderContainer.setLayoutParams(sliderContainer_LP);
        parent.addView(sliderContainer);
    }

    public void createProgressBar() {
        progressBar = new ProgressBar(context);
        ConstraintLayout.LayoutParams LP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        progressBar.setLayoutParams(LP);
        progressBar.setVisibility(View.GONE);
        progressBar.setId(R.id.progressBar);
        parent.addView(progressBar);

    }

    public void createSlideCard() {
        slideCard = new CardView(context);
        ConstraintLayout.LayoutParams LP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //LP.leftMargin = (int)convertDpToPixel(10);
        //LP.rightMargin = (int)convertDpToPixel(10);
        slideCard.setLayoutParams(LP);
        slideCard.setCardBackgroundColor(Color.WHITE);
        slideCard.setId(R.id.slideCard);
        slideCard.setAlpha(.0f);
        parent.addView(slideCard);
        connectSlideCard();
    }

    //Constraint Connection Functions
    //Side Bars Connections
    public void connectSlideCard() {
        ConstraintSet set = new ConstraintSet();
        int slideID = slideCard.getId();
        set.clone(parent);
        set.connect(slideID, ConstraintSet.TOP, parent.getId(), ConstraintSet.TOP);
        set.connect(slideID, ConstraintSet.BOTTOM, parent.getId(), ConstraintSet.BOTTOM);
        set.connect(slideID, ConstraintSet.END, parent.getId(), ConstraintSet.START);
        set.connect(slideID, ConstraintSet.START, parent.getId(), ConstraintSet.START);
        set.applyTo(parent);
    }

    public void clearSlideCardConnections() {
        ConstraintSet set = new ConstraintSet();
        int slideID = slideCard.getId();
        set.clone(parent);
        set.clear(slideID, ConstraintSet.TOP);
        set.clear(slideID, ConstraintSet.BOTTOM);
        set.clear(slideID, ConstraintSet.END);
        set.clear(slideID, ConstraintSet.START);
        set.applyTo(parent);
    }

    //Navigation Frames Connection
    public void removeSliderConnections_Left() {
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

        switchFrames.switchFrames(leftFrame, centerFrame, rightFrame);
        //connectSlideCard();

    }

    public void removeSliderConnections_Right() {
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
        switchFrames.switchFrames(leftFrame, centerFrame, rightFrame);
        //connectSlideCard();

    }

    public void BaseConnectLayouts() {
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        //-------Base Connection of all Frames
        set.connect(centerFrame.getId(), ConstraintSet.START, parent.getId(), ConstraintSet.START);
        //Connect left Frame to center Frame
        set.connect(leftFrame.getId(), ConstraintSet.END, centerFrame.getId(), ConstraintSet.START);
        //Connect right Frame to Center Frame
        set.connect(rightFrame.getId(), ConstraintSet.START, centerFrame.getId(), ConstraintSet.END);
        //Connect ProgressBar to Center Frame
        set.applyTo(parent);
    }

    public void connectProgressToFrame(View Frame) {

        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        set.connect(progressBar.getId(), ConstraintSet.START, Frame.getId(), ConstraintSet.START);
        set.connect(progressBar.getId(), ConstraintSet.END, Frame.getId(), ConstraintSet.END);
        set.connect(progressBar.getId(), ConstraintSet.TOP, Frame.getId(), ConstraintSet.TOP);
        set.connect(progressBar.getId(), ConstraintSet.BOTTOM, Frame.getId(), ConstraintSet.BOTTOM);
        set.applyTo(parent);
        //progressBar.setVisibility(View.VISIBLE);

    }

    public void clearProgressConstraints() {

        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        set.clear(progressBar.getId(), ConstraintSet.START);
        set.clear(progressBar.getId(), ConstraintSet.END);
        set.clear(progressBar.getId(), ConstraintSet.TOP);
        set.clear(progressBar.getId(), ConstraintSet.BOTTOM);
        set.applyTo(parent);
        progressBar.setVisibility(View.GONE);

    }

    public void removeAllConnections() {
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
        set.clear(sliderContainer.getId(), ConstraintSet.END);

        set.applyTo(parent);
    }

    public void sliderToParent_Left() {
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        set.clear(centerFrame.getId(), ConstraintSet.START);
        set.clear(centerFrame.getId(), ConstraintSet.END);
        set.connect(sliderContainer.getId(), ConstraintSet.START, parent.getId(), ConstraintSet.START);
        set.connect(centerFrame.getId(), ConstraintSet.END, sliderContainer.getId(), ConstraintSet.END);
        set.applyTo(parent);
    }

    public void sliderToParent_Right() {
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        set.clear(centerFrame.getId(), ConstraintSet.START);
        set.clear(centerFrame.getId(), ConstraintSet.END);
        set.connect(sliderContainer.getId(), ConstraintSet.END, parent.getId(), ConstraintSet.END);
        set.connect(centerFrame.getId(), ConstraintSet.START, sliderContainer.getId(), ConstraintSet.START);
        set.applyTo(parent);
    }

    public void resetSliderConstraint() {
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);
        set.clear(sliderContainer.getId(), ConstraintSet.START);
        set.clear(sliderContainer.getId(), ConstraintSet.END);
        set.clear(centerFrame.getId(), ConstraintSet.START);
        set.clear(centerFrame.getId(), ConstraintSet.END);
        set.applyTo(parent);
    }

    public boolean isLeft(int numb) {
        boolean isEven;
        isEven = numb % 2 == 0;

        return isEven;
    }

    public float convertDpToPixel(float dp) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
