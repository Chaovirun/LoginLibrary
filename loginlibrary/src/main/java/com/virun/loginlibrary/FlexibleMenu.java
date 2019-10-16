package com.virun.loginlibrary;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * Created By Chan Youvita on 2019-06-26
 */
public class FlexibleMenu {
    private Context mContext;
    private PopupWindow mPopupWindow;
    private RelativeLayout mMainLayout;
    private GridLayout mMenuBody;
    private TextView mMenuText;
    private ViewGroup.LayoutParams mParams;

    public static final int SHOW_AS_DROPDOWN = 0;
    public static final int SHOW_AT_LOCATION = 1;
    public static final int BOTTOM_ANCHOR    = 0;
    public static final int CENTER_ANCHOR    = 1;
    public static final int TOP_ANCHOR       = 2;
    public static final int LEFT_ANCHOR      = 3;
    public static final int RIGHT_ANCHOR     = 4;

    /*
     * {right_top_radius,right_top_radius,left_top_radius,left_top_radius,right_bottom_radius,right_bottom_radius,left_bottom_radius,left_bottom_radius}
     */
    private float radius               = 5.0f;
    private float[] mHeaderTopRadii    = new float[] {radius, radius, radius, radius, 0, 0, 0, 0};
    private float[] mHeaderBottomRadii = new float[] {0, 0, 0, 0, 0, 0, 0, 0};
    private float[] mCenterTopRadii    = new float[] {0, 0, 0, 0, 0, 0, 0, 0};
    private float[] mCenterBottomRadii = new float[] {0, 0, 0, 0, 0, 0, 0, 0};
    private float[] mFooterTopRadii    = new float[] {0, 0, 0, 0, 0, 0, 0, 0};
    private float[] mFooterBottomRadii = new float[] {0, 0, 0, 0, radius, radius, radius, radius};

    private int mCellHeight;
    private int mCellWidth;
    private int mCellBackgroundColor;
    private int mCellTextColorNor;
    private int mCellTextColorSet;
    private int mMenuStrokeWidth;
    private int mMenuPaddingLeft;
    private int mMenuPaddingTop;
    private int mMenuPaddingRight;
    private int mMenuPaddingBottom;
    private int mMenuMargin = 15;
    private int mFadeDuration = 150;
    private int mItemSize;

    private int[][] states = new int[][] {new int[] {android.R.attr.state_pressed}, new int[] {}};
    private int[] colors = new int[2];

    private static int mItemSelected = -1;

    private boolean mCheckHighLightBackground;
    private boolean mCheckHighLightText;
    private boolean mCheckViewDefault;

    private enum MenuType {
        MENU_TOP,
        MENU_CENTER,
        MENU_BOTTOM
    }

    @IntDef(value = {
        SHOW_AS_DROPDOWN,
        SHOW_AT_LOCATION
    })

    @Retention(RetentionPolicy.SOURCE)
    @interface ShowType {}

    @IntDef(value = {
        BOTTOM_ANCHOR,
        CENTER_ANCHOR,
        TOP_ANCHOR,
        LEFT_ANCHOR,
        RIGHT_ANCHOR
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface AnchorGravity{}

    private onFlexibleMenuListener mListener;

    public void setOnMenuItemClickListener(onFlexibleMenuListener listener) {
        mListener = listener;
    }

    /*
     * menu_view: 0 is default
     */
    public FlexibleMenu(Context context, int menu_view, int cell_width, int cell_height, int stroke_width) {
        mContext         = context;
        mCellWidth       = cell_width;
        mCellHeight      = cell_height;
        mMenuStrokeWidth = stroke_width;

        int view = menu_view;
        /*
         * default view
         */
        if (menu_view == 0) {
            mCheckViewDefault = true;
            view = R.drawable.menu_background;
        }
        mPopupWindow = new PopupWindow(context);
        mPopupWindow.setContentView(initView(view));
        mPopupWindow.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /*
     * init view
     */
    private View initView(int menu_view) {
        try {
            mParams = new ViewGroup.LayoutParams(mCellWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            mMainLayout = new RelativeLayout(mContext);
            mMenuBody   = new GridLayout(mContext);
            mMenuBody.setBackgroundResource(menu_view);
            mMenuBody.setOrientation(GridLayout.VERTICAL);
            mMainLayout.addView(mMenuBody, mParams);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return mMainLayout;
    }

    /*
     * set padding to view
     */
    public void setMenuPadding(int padding_left, int padding_top, int padding_right, int padding_bottom) {
        mMenuPaddingLeft   = padding_left;
        mMenuPaddingTop    = padding_top;
        mMenuPaddingRight  = padding_right;
        mMenuPaddingBottom = padding_bottom;
    }

    /*
     * add menu item
     */
    public void setMenuItem(String[] text, int size, int text_color_nor, int text_color_set, int pressed_color, boolean pressed_animation) {
        try {
            mCellTextColorNor    = text_color_nor;
            mCellTextColorSet    = text_color_set;
            mCellBackgroundColor = pressed_color;

            mParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mItemSize = text.length;
            for (int i = 0; i < mItemSize; i++) {
                mMenuText = new TextView(mContext);
                mMenuText.setLayoutParams(mParams);
                mMenuText.setId(i);
                mMenuText.setHeight(mCellHeight);
                mMenuText.setText(text[i]);
                mMenuText.setTextSize(size);
                mMenuText.setTextColor(getColorStartList(text_color_nor, text_color_set));
                mMenuText.setClickable(true);
                mMenuText.setPadding(mMenuPaddingLeft, mMenuPaddingTop, mMenuPaddingRight, mMenuPaddingBottom);
                mMenuText.setBackgroundDrawable(getMenuBackground(pressed_color, pressed_animation));
                mMenuText.setGravity(Gravity.CENTER_VERTICAL);
                mMenuBody.addView(mMenuText);
            }

            setMenuItemEnableClick();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * enable listener menu items
     */
    private void setMenuItemEnableClick() {
        int childCount = mMenuBody.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mMenuBody.getChildAt(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemSelected = v.getId();
                    mListener.onMenuItemClicked(v.getId());
                    mPopupWindow.dismiss();
                }
            });


            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
                            if (mItemSelected != -1 && v.getId() != mItemSelected) {
                                /*
                                 * only text
                                 */
                                if (mCheckHighLightText) setHighLightText(mItemSelected, mCellTextColorNor);

                                /*
                                 * only background
                                 */
                                if (mCheckHighLightBackground) setHighLightBackground(mItemSelected, Color.TRANSPARENT);

                                /*
                                 * reset padding only custom menu box
                                 */
                                if (!mCheckViewDefault) mMenuBody.setPadding(0, 0, 0, 0);
                            }
                            break;
                        case KeyEvent.ACTION_UP:
                            if (mItemSelected != -1) {
                                /*
                                 * only text
                                 */
                                if (mCheckHighLightText) setHighLightText(mItemSelected, mCellTextColorSet);

                                /*
                                 * only background
                                 */
                                if (mCheckHighLightBackground) setHighLightBackground(mItemSelected, mCellBackgroundColor);
                            }
                            break;
                    }
                    return false;
                }
            });

        }
    }

    /*
     * set selector for text color
     */
    private ColorStateList getColorStartList(int text_color_nor, int text_color_set) {
        colors[0] = text_color_set > 0 ? ContextCompat.getColor(mContext, text_color_set) : text_color_set;
        colors[1] = text_color_nor > 0 ? ContextCompat.getColor(mContext, text_color_nor) : text_color_nor;

        return new ColorStateList(states, colors);
    }

    /*
     * set selector for text view
     */
    public void setMenuTextSelector(int text_color_nor, int text_color_set) {
        mCellTextColorNor = text_color_nor;
        mCellTextColorSet = text_color_set;
        mMenuText.setTextColor(getColorStartList(text_color_nor, text_color_set));
    }

    /*
     * set high light background in local
     */
    private void setHighLightBackground(int index, int color) {
        if (mItemSelected != -1) {
            /*
             * set padding only other custom menu box
             */
            if (!mCheckViewDefault) {
                mMenuBody.setPadding(mMenuStrokeWidth, 0, mMenuStrokeWidth, 0);
            }
            mMenuBody.getChildAt(index).setBackgroundColor(color > 0 ? ContextCompat.getColor(mContext, color) : color);
        }
    }

    /*
     * set high light background in global
     */
    public void setHighLightBackground(int index) {
        mCheckHighLightBackground = true;
        setHighLightBackground(index, mCellBackgroundColor);
    }

    /*
     * set high light text color in local
     */
    private void setHighLightText(int index, int color) {
        if (mItemSelected != -1) {
            View view = mMenuBody.getChildAt(index);
            mMenuText = view.findViewById(index);
            mMenuText.setTextColor(color > 0 ? ContextCompat.getColor(mContext, color) : color);
        }
    }

    /*
     * set high light text color in global
     */
    public void setHighLightText(int index) {
        mCheckHighLightText = true;
        setHighLightText(index, mCellTextColorSet);
    }

    /*
     * default margin
     */
    public void setMaxMargin(int max_margin) {
        mMenuMargin = max_margin;
    }

    public void setMenuShow(@ShowType int show_type, @AnchorGravity int gravity, View anchor) {
        setMenuShow(show_type, gravity, anchor, mMenuMargin, mMenuMargin);
    }

    /*
     * setMenuShow popup window
     */
    public void setMenuShow(@ShowType int show_type, @AnchorGravity int gravity, View anchor, int xoffset_margin, int yoffset_margin) {
        int[] anchorLocation = new int[2];
        anchor.getLocationOnScreen(anchorLocation);

        switch (show_type) {
            case SHOW_AS_DROPDOWN:
                switch (gravity) {
                    case TOP_ANCHOR:
                        mPopupWindow.showAsDropDown(anchor, xoffset_margin, (-anchorLocation[1] / 2) - yoffset_margin);
                        break;

                    case BOTTOM_ANCHOR:
                        mPopupWindow.showAsDropDown(anchor, xoffset_margin, yoffset_margin);
                        break;

                    case LEFT_ANCHOR:
                        mPopupWindow.showAsDropDown(anchor, (-anchorLocation[0] / 2) + xoffset_margin, -anchor.getHeight() + yoffset_margin);
                        break;

                    case RIGHT_ANCHOR:
                        mPopupWindow.showAsDropDown(anchor, anchor.getWidth() + xoffset_margin, -anchor.getHeight() + yoffset_margin);
                        break;

                    case CENTER_ANCHOR:
                        mPopupWindow.showAsDropDown(anchor, xoffset_margin, -anchor.getHeight() + yoffset_margin);
                        break;
                }
                break;

            case SHOW_AT_LOCATION:
                switch (gravity) {
                    case TOP_ANCHOR:
                        mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, anchorLocation[0] + xoffset_margin, (anchorLocation[1] / 2) + anchor.getHeight() - yoffset_margin);
                        break;

                    case BOTTOM_ANCHOR:
                        mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, anchorLocation[0] + xoffset_margin, anchorLocation[1] + anchor.getHeight() + yoffset_margin);
                        break;

                    case LEFT_ANCHOR:
                        mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, -anchorLocation[0] + anchor.getWidth() + xoffset_margin, anchorLocation[1] + yoffset_margin);
                        break;

                    case RIGHT_ANCHOR:
                        mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, anchorLocation[0] + anchor.getWidth() + xoffset_margin, anchorLocation[1] + yoffset_margin);
                        break;

                    case CENTER_ANCHOR:
                        mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, anchorLocation[0] + xoffset_margin, anchorLocation[1] + yoffset_margin);
                        break;
                }

                break;
        }
    }

    /*
     * set background pressed on menu item
     */
    private StateListDrawable getMenuBackground(int pressed_color, boolean pressed_animation) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, setBackgroundSelector(pressed_color > 0 ? ContextCompat.getColor(mContext, pressed_color) : pressed_color));
        stateListDrawable.addState(new int[] {}, new ColorDrawable(Color.TRANSPARENT));

        if (pressed_animation) {
            stateListDrawable.setEnterFadeDuration(mFadeDuration);
            stateListDrawable.setExitFadeDuration(mFadeDuration);
        }
        return stateListDrawable;
    }

    /*
     * press menu view fade duration
     */
    private void setMenuPressFadeDuration(int fade_duration) {
        mFadeDuration = fade_duration;
    }

    /*
     * set selector background
     */
    private Drawable setBackgroundSelector(int pressed_color)
    {
        Drawable layerDrawable = null;
        if (mMenuBody.getChildCount() == 0) {
            layerDrawable = drawShape(mHeaderTopRadii, mHeaderBottomRadii, MenuType.MENU_TOP, pressed_color);
        }
        else if (mMenuBody.getChildCount() > 0 && mMenuBody.getChildCount() < mItemSize -1){
            layerDrawable = drawShape(mCenterTopRadii, mCenterBottomRadii, MenuType.MENU_CENTER, pressed_color);
        }
        else if (mMenuBody.getChildCount() == mItemSize - 1) {
            layerDrawable = drawShape(mFooterTopRadii, mFooterBottomRadii, MenuType.MENU_BOTTOM, pressed_color);
        }
        return layerDrawable;
    }

    /*
     * draw selector background
     */
    private Drawable drawShape(float[] outer_top, float[] outer_bottom, MenuType menuType, int pressed_color) {
        RoundRectShape topRoundRectShape = new RoundRectShape(outer_top, null, null);
        ShapeDrawable topShapeDrawable = new ShapeDrawable(topRoundRectShape);
        topShapeDrawable.getPaint().setColor(pressed_color);

        RoundRectShape BottomRoundRectShape = new RoundRectShape(outer_bottom, null, null);
        ShapeDrawable bottomShapeDrawable = new ShapeDrawable(BottomRoundRectShape);
        bottomShapeDrawable.getPaint().setColor(pressed_color);

        Drawable[] drawarray = {topShapeDrawable, bottomShapeDrawable};
        LayerDrawable layerdrawable = new LayerDrawable(drawarray);

        switch (menuType) {
            case MENU_TOP:
                /*
                 * body top
                 */
                layerdrawable.setLayerInset(0, mMenuStrokeWidth, mMenuStrokeWidth, mMenuStrokeWidth, 0);
                layerdrawable.setLayerInset(1, mMenuStrokeWidth, mCellHeight, mMenuStrokeWidth, 0);
                break;

            case MENU_CENTER:
                /*
                 * body center
                 */
                layerdrawable.setLayerInset(0, mMenuStrokeWidth, 0, mMenuStrokeWidth, 0);
                layerdrawable.setLayerInset(1, mMenuStrokeWidth, 0, mMenuStrokeWidth, 0);
                break;

            case MENU_BOTTOM:
                /*
                 * body bottom
                 */
                layerdrawable.setLayerInset(0, mMenuStrokeWidth , 0, mMenuStrokeWidth, mCellHeight);
                layerdrawable.setLayerInset(1, mMenuStrokeWidth, 0, mMenuStrokeWidth, mMenuStrokeWidth);
                break;
        }
        return layerdrawable;
    }

    public interface onFlexibleMenuListener {
        void onMenuItemClicked(int index);
    }
}
