package com.hufeng.filemanager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by feng on 14-5-19.
 */
public class NavigationDrawerItem extends RelativeLayout {

    private TextView mText;
    private View mSeperateLine;
    private View mSeperateBoldLine;
    private int mBoldPadding;
    private int mThinPadding;
    private int mLabelSize;
    private int mNonLabelSize;
    private float mDensity;

    public NavigationDrawerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDensity = getResources().getDisplayMetrics().density;
        mBoldPadding = (int)(15 * mDensity);
        mThinPadding = (int)(8 * mDensity);

        mNonLabelSize = 18;
        mLabelSize = 12;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mText = (TextView) findViewById(R.id.text);
        mSeperateLine = findViewById(R.id.seperate_line);
        mSeperateBoldLine = findViewById(R.id.seperate_bold_line);
    }

    public void bind(DrawerItem drawer) {
        drawer.render(this);
    }

    public void setText(String text){
        mText.setText(text);
    }

    public void setAsLabel(boolean label, int icon) {

        if (label) {
            mText.setTextSize(mLabelSize);
            mText.setPadding(0, mThinPadding, 0, mThinPadding);
            mSeperateLine.setVisibility(View.GONE);
            mSeperateBoldLine.setVisibility(View.VISIBLE);
        } else {
            mText.setTextSize(mNonLabelSize);
            mText.setPadding(0, mBoldPadding, 0, mBoldPadding);
            mSeperateLine.setVisibility(View.VISIBLE);
            mSeperateBoldLine.setVisibility(View.GONE);
        }

        if (icon != 0) {
            Drawable drawable = getResources().getDrawable(icon);
            int size = (int)((mNonLabelSize+5)*mDensity);
            drawable.setBounds(0, 0, size, size);
            mText.setCompoundDrawables(drawable, null, null, null);
        } else {
            mText.setCompoundDrawables(null, null, null, null);
        }

    }
}
