package com.virun.loginlibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Create By Chan Youvita on 03/06/2019
 * Email: chanyouvita@gmail.com
 * Tel: (+855)070-922-049
 */
public class FlexibleToolBar extends Toolbar {
    public static final int GB_LEFT  = 0;
    public static final int GB_RIGHT = 1;

    @IntDef(value = {
            GB_RIGHT,
            GB_LEFT
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface GroupButton {}

    private GridLayout mGroupLeft;
    private GridLayout mGroupRight;
    private LinearLayout.LayoutParams mParams;
    private Context mContext;
    private TextView mTitle;
    private BizToolBarListener mListener;

    private int mTextSize;
    private int mTextColor;
    private int mIndex      = 1;
    private int mIndexLeft  = 1;
    private int mIndexRight = 1;
    private int mMaxLength  = 10;

    public void setOnToolBarClickListener(BizToolBarListener listener) {
        mListener = listener;
    }

    public FlexibleToolBar(Context context) {
        super(context);
        mContext = context;

        initView();
    }

    public FlexibleToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initView();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlexibleToolBar);

        mTextSize  = a.getInt(R.styleable.FlexibleToolBar_title_size, 18);
        mTextColor = a.getColor(R.styleable.FlexibleToolBar_title_color, Color.parseColor("#ffffff"));

        String mTextTitle = a.getString(R.styleable.FlexibleToolBar_title_text);
        setToolBarTitle(mTextTitle);

        a.recycle();
    }

    /**
     * init view
     */
    private void initView() {
        try {
            RelativeLayout.LayoutParams params;
            RelativeLayout mMainLayout = new RelativeLayout(mContext);

            /*
             * group left
             */
            mGroupLeft  = new GridLayout(mContext);
            params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            mMainLayout.addView(mGroupLeft, params);

            /*
             * group right
             */
            mGroupRight = new GridLayout(mContext);
            params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mMainLayout.addView(mGroupRight, params);

            /*
             * center title
             */
            mTitle = new TextView(mContext);
            params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            mMainLayout.addView(mTitle, params);

            /*
             * shadow toolbar
             */
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                setElevation(10);
            }
            setContentInsetsAbsolute(0,0);
            addView(mMainLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            mParams     = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, getActionBarHeight());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set title max length
     * @param maxLength is text max length
     */
    public void setToolBarTitleMaxLength(int maxLength) {
        mMaxLength = maxLength;
    }

    /**
     * set clear view for add new view
     */
    public void setToolBarRemoveAllView() {
        mGroupRight.removeAllViews();
        mGroupLeft.removeAllViews();
        mIndexLeft  = 1;
        mIndexRight = 1;
    }

    /**
     * set title toolbar for text center
     * @param text is text for title
     * @param color is color for text title
     */
    public void setToolBarTitle(String text, int color) {
        if (text != null) {
            if (text.length() > mMaxLength) {
                text = text.substring(0, mMaxLength) + "...";
            }
            mTitle.setText(text);
            mTitle.setTextSize(mTextSize);
            mTitle.setTextColor((color > 0 ? ContextCompat.getColor(mContext, color) : color));
            mTextColor = color;
        }
    }

    /**
     * set default title toolbar for text center
     * @param text is text for title
     */
    public void setToolBarTitle(String text) {
        if (text != null) {
            if (text.length() > mMaxLength) {
                text = text.substring(0, mMaxLength) + "...";
            }
            mTitle.setText(text);
            mTitle.setTextSize(mTextSize);
            mTitle.setTextColor((mTextColor > 0 ? ContextCompat.getColor(mContext, mTextColor) : mTextColor));
        }
    }

    /**
     * for image text button group left or right
     * @param groupButton is GB_RIGHT and GB_LEFT
     * @param text is text will add to view
     * @param margin_left is margin left of view
     * @param margin_right is margin right of view
     */
    public void setToolBarButton(@GroupButton int groupButton, String text, int margin_left, int margin_right) {
        int mViewId = getIndexComponent(groupButton);
        /*
         * set margins to text view of the first text index or last index
         */
        mParams.setMargins(margin_left, 0, margin_right, 0);

        TextView textView = new TextView(mContext);
        textView.setText(text);
        textView.setTextSize(mTextSize);
        textView.setTextColor((mTextColor > 0 ? ContextCompat.getColor(mContext, mTextColor) : mTextColor));
        textView.setLayoutParams(mParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        RelativeLayout mMainLayout = new RelativeLayout(mContext);
        mMainLayout.addView(textView);
        mMainLayout.addView(addBadgeView(generateId(groupButton, mViewId)));

        /*
         * add view to each groups
         */
        addViewByGroup(groupButton, addViewToParent(mMainLayout, mViewId));
    }

    /**
     * for image button group left or right
     * @param groupButton is GB_RIGHT and GB_LEFT
     * @param drawableId is image will add to view
     * @param margin_left is margin left of view
     * @param margin_right is margin right of view
     */
    public void setToolBarButton(@GroupButton int groupButton, int drawableId, int margin_left, int margin_right) {
        int mViewId = getIndexComponent(groupButton);
        /*
         * set margins to text view of the first text index or last index
         */
        mParams.setMargins(margin_left, 0, margin_right, 0);

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(drawableId);
        imageView.setLayoutParams(mParams);

        RelativeLayout mMainLayout = new RelativeLayout(mContext);
        mMainLayout.addView(imageView);
        mMainLayout.addView(addBadgeView(generateId(groupButton, mViewId)));

        /*
         * add view to each groups
         */
        addViewByGroup(groupButton, addViewToParent(mMainLayout, mViewId));
    }

    /**
     * create auto id for components
     * @param groupButton is GB_RIGHT and GB_LEFT
     */
    private int getIndexComponent(@GroupButton int groupButton) {

        switch (groupButton) {
            case GB_LEFT:
                mIndex = mIndexLeft++;
                break;
            case GB_RIGHT:
                mIndex = mIndexRight++;
                break;
        }
        return mIndex;
    }

    /**
     * add view to parent
     * @param view is view will add to parent view
     * @param id is index of component when click
     */
    private LinearLayout addViewToParent(View view, int id) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(mParams);
        linearLayout.setId(id);
        linearLayout.addView(view);
        return linearLayout;
    }

    /**
     * add view to each by group in gridlayout
     * @param groupButton is GB_RIGHT and GB_LEFT
     * @param linearLayout is component for add to right or left
     */
    private void addViewByGroup(@GroupButton int groupButton, LinearLayout linearLayout) {
        switch (groupButton) {
            case GB_LEFT:
                mGroupLeft.addView(linearLayout);
                break;

            case GB_RIGHT:
                mGroupRight.addView(linearLayout);
                break;
        }
        /*
         * set listener for button click
         */
        setToolBarButtonEnableClick(groupButton);
    }

    /**
     * find view by id each group
     * @param id is id of view in toolbar
     */
    public View findViewById(@GroupButton int groupButton, int id) {
        if (id < 1) return null;
        return (groupButton == GB_LEFT) ? mGroupLeft.getChildAt((id - 1)) : mGroupRight.getChildAt((id - 1));
    }

    /**
     * enable listener for button
     * @param groupButton is GB_RIGHT and GB_LEFT when clicked
     */
    private void setToolBarButtonEnableClick(final @GroupButton int groupButton) {
        int childCount = (groupButton == GB_LEFT) ? mGroupLeft.getChildCount() : mGroupRight.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = (groupButton == GB_LEFT) ? mGroupLeft.getChildAt(i) : mGroupRight.getChildAt(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected()); // for only selector button
                    mListener.onToolBarClicked(groupButton, v);
                }
            });
        }
    }

    /**
     * enable listener for button
     * @param groupButton is GB_RIGHT and GB_LEFT when clicked
     * @param id is component id
     * @param selected if true: selected, false: unselected
     */
    public void setToolBarButtonSelected(@GroupButton int groupButton, int id, boolean selected) {
        if (id < 1) return;
        View view = (groupButton == GB_LEFT) ? mGroupLeft.getChildAt((id - 1)) : mGroupRight.getChildAt((id - 1));
        view.setSelected(selected);
    }

    /**
     * get action bar height
     */
    @SuppressLint("ObsoleteSdkInt")
    private int getActionBarHeight() {
        int[] abSzAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            abSzAttr = new int[] { android.R.attr.actionBarSize };
        } else {
            abSzAttr = new int[] { R.attr.actionBarSize };
        }
        @SuppressLint("Recycle") TypedArray a = mContext.obtainStyledAttributes(abSzAttr);
        return a.getDimensionPixelSize(0, -1);
    }

    /**
     * add badge count
     * @param badge_id is id of view
     */
    private View addBadgeView(int badge_id) {
        /* background badge */
        RelativeLayout badgeView = new RelativeLayout(mContext);
        badgeView.setId(R.id.badge_view + badge_id);
        badgeView.setVisibility(View.GONE);
        badgeView.setBackgroundDrawable(drawShape());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(15, 0, 15, 5);

        /* text badge */
        TextView badgeText = new TextView(mContext);
        badgeText.setTextColor(Color.WHITE);
        badgeText.setId(R.id.badge_text + badge_id);
        badgeText.setLayoutParams(params);
        badgeView.addView(badgeText);

        return badgeView;
    }

    /**
     * draw badge background
     */
    private GradientDrawable drawShape() {
        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius(100);
        shape.setColor(Color.RED);
        return shape;
    }

    /**
     * set badge count
     * @param groupButton is GB_RIGHT and GB_LEFT
     * @param id is view id
     * @param count is badge count
     */
    public void setToolBarBadge(@GroupButton int groupButton, int id, String count) {
        View anchor = (groupButton == GB_LEFT) ? mGroupLeft.getChildAt((id -1)) : mGroupRight.getChildAt((id - 1));
        RelativeLayout badgeView = findViewById(R.id.badge_view + generateId(groupButton, anchor.getId()));
        badgeView.setLayoutParams(getDefaultBadgeLocation());

        if (count.equals("") || count.isEmpty()) {
            badgeView.setVisibility(View.GONE);
        }else {
            badgeView.setVisibility(View.VISIBLE);
        }

        /* set badge count */
        TextView textView = badgeView.findViewById(R.id.badge_text + generateId(groupButton, anchor.getId()));
        textView.setText(count);
    }

    /**
     * set badge count
     * @param groupButton is GB_RIGHT and GB_LEFT
     * @param id is view id
     * @param count is badge count
     */
    public void setToolBarBadge(GradientDrawable gradientDrawable, @GroupButton int groupButton, int id, String count) {
        View anchor = (groupButton == GB_LEFT) ? mGroupLeft.getChildAt((id -1)) : mGroupRight.getChildAt((id - 1));
        RelativeLayout badgeView = findViewById(R.id.badge_view + generateId(groupButton, anchor.getId()));
        badgeView.setBackgroundDrawable(gradientDrawable);
        badgeView.setLayoutParams(getDefaultBadgeLocation());

        if (count.equals("") || count.isEmpty()) {
            badgeView.setVisibility(View.GONE);
        }else {
            badgeView.setVisibility(View.VISIBLE);
        }

        /* set badge count */
        TextView textView = badgeView.findViewById(R.id.badge_text + generateId(groupButton, anchor.getId()));
        textView.setText(count);
    }

    /**
     * default location badge
     */
    private RelativeLayout.LayoutParams getDefaultBadgeLocation() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 20, 0, 0);
        return params;
    }

    /**
     * set badge location
     * @param groupButton is GB_RIGHT and GB_LEFT
     * @param id is view id
     * @param left margin left
     * @param top margin top
     * @param right margin right
     * @param bottom margin bottom
     */
    public void setToolBarBadgeLocation(@GroupButton int groupButton, int id, int left, int top, int right, int bottom) {
        View anchor = (groupButton == GB_LEFT) ? mGroupLeft.getChildAt((id -1)) : mGroupRight.getChildAt((id - 1));
        RelativeLayout badgeView = findViewById(R.id.badge_view + generateId(groupButton, anchor.getId()));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        badgeView.setLayoutParams(params);
    }

    /**
     * generate id for view
     * @param val1 is value of groupButton: GB_RIGHT and GB_LEFT
     * @param val2 is value of view index
     */
    private int generateId(int val1, int val2) {
        return Integer.parseInt(val1 + String.valueOf(val2));
    }

    public interface BizToolBarListener {
        void onToolBarClicked(@GroupButton int groupButton, View view);
    }
}
