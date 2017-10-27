package com.ldy.scale.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.administrator.demotab.R;

/**
 * Created by lidongyang on 2015/7/26.
 */
public class ScaleTextView extends TextView {

    private static final float MAX_RATE = 1f / 5f;
    private static final float MIN_RATE = 1f / 1000f;
    private static final float MIN_SIZE = 14f;
    private static final float MAX_SIZE = 16f;
    private static final int FROM_COLOR = 0xFFF5D336;
    private static final int TO_COLOR = 0xFF98A8AA;

    private float mScale = -1f;
    private float mMinSize;
    private float mMaxSize;
    private int mFromColor;
    private int mToColor;
    private boolean isScale;
    private int changeColor;

    public ScaleTextView(Context context) {
        super(context);
    }

    public ScaleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        mMinSize = array.getDimension(R.styleable.ScaleView_minSize, dip2px(MIN_SIZE));
        mMaxSize = array.getDimension(R.styleable.ScaleView_maxSize, dip2px(MAX_SIZE));
        mFromColor = array.getColor(R.styleable.ScaleView_fromColor, FROM_COLOR);
        mToColor = array.getColor(R.styleable.ScaleView_toColor, TO_COLOR);
        isScale = array.getBoolean(R.styleable.ScaleView_isScale, false);
        array.recycle();
    }

    public void setTabScale(float nexScale, int color, boolean isChangeColor) {
        if (isInvalidate(nexScale)) {

            if (nexScale == 0 || nexScale == 1) {
                mScale = nexScale;
            } else if (nexScale > mScale) {
                mScale = (nexScale - mScale > MAX_RATE) ? mScale + MAX_RATE : nexScale;
            } else if (nexScale < mScale) {
                mScale = (mScale - nexScale > MAX_RATE) ? mScale - MAX_RATE : nexScale;
            }

            if (isScale) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, getTabSize());
            }

            if (isChangeColor) {
                this.changeColor = color;
                setTextColor(color);
            } else if (nexScale > 0.5f) {
                this.changeColor = mFromColor;
                setTextColor(mFromColor);
            } else {
                this.changeColor = mToColor;
                setTextColor(mToColor);
            }
        }
    }

    public int getFromColor() {
        return mFromColor;
    }

    public void setFromColor(int fromColor) {
        mFromColor = fromColor;
    }

    public int getToColor() {
        return mToColor;
    }

    public int getChangeColor() {
        return changeColor > 0 ? mFromColor : changeColor;
    }

    /**
     * 是否重绘view
     *
     * @param nexScale
     * @return
     */
    private boolean isInvalidate(float nexScale) {
        if (nexScale == mScale) {
            return false;
        }
        if (nexScale == 0f || nexScale == 1f) {
            return true;
        }
        return (Math.abs(nexScale - mScale) > MIN_RATE);
    }

    /**
     * 获取当前文字大小
     *
     * @return
     */
    private float getTabSize() {
        return (mMaxSize - mMinSize) * mScale + mMinSize;
    }

    private int dip2px(float size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
                getResources().getDisplayMetrics());
    }

}
