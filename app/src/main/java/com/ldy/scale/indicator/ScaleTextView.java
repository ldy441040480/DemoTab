package com.ldy.scale.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.administrator.demotab.R;

/**
 * Created by lidongyang on 2015/7/26.
 */
public class ScaleTextView extends TextView {

    private static final float MAX_RATE = 1f / 5f;  // 最大变化比率
    private static final float MIN_RATE = 1f / 1000f;  // 最小变化比率
    private static final float MIN_SIZE = 14f; //最小字体
    private static final float MAX_SIZE = 16f; //最大字体
    private static final int FROM_COLOR = 0xFFF5D336; //最大字体
    private static final int TO_COLOR = 0xFF98A8AA; //最大字体

    private RGB RGB_MAX; //最大颜色
    private RGB RGB_MIN; //最小颜色

    private float mScale = -1f;
    private float mMinSize;
    private float mMaxSize;
    private int mFromColor;
    private int mToColor;
    private boolean isScale;
    private boolean isChangeColor;

    public ScaleTextView(Context context) {
        super(context);
    }

    public ScaleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        RGB_MAX = new RGB(mFromColor);
        RGB_MIN = new RGB(mToColor);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        mMinSize = array.getDimension(R.styleable.ScaleView_minSize, dip2px(MIN_SIZE));
        mMaxSize = array.getDimension(R.styleable.ScaleView_maxSize, dip2px(MAX_SIZE));
        mFromColor = array.getColor(R.styleable.ScaleView_fromColor, FROM_COLOR);
        mToColor = array.getColor(R.styleable.ScaleView_toColor, TO_COLOR);
        isScale = array.getBoolean(R.styleable.ScaleView_isScale, false);
        isChangeColor = array.getBoolean(R.styleable.ScaleView_isChangeColor, true);
        array.recycle();
    }

    public void setTabScale(float nexScale) {
        if (isInvalidate(nexScale)) {

            if (nexScale == 0 || nexScale == 1) {
                mScale = nexScale;
            } else if (nexScale > mScale) {
                mScale = (nexScale - mScale > MAX_RATE) ? mScale + MAX_RATE : nexScale;
            } else if (nexScale < mScale) {
                mScale = (mScale - nexScale > MAX_RATE) ? mScale - MAX_RATE : nexScale;
            }

            if (isScale)
                setTextSize(TypedValue.COMPLEX_UNIT_PX,getTabSize());

            if (isChangeColor) {
                setTextColor(getTabColor());
            } else if (nexScale == 0 || nexScale == 1) {
                setTextColor(getTabColor());
            }
        }
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

    /**
     * 获取当前文字颜色
     *
     * @return
     */
    private int getTabColor() {
        int r = getColor(RGB_MAX.r, RGB_MIN.r, mScale);
        int b = getColor(RGB_MAX.b, RGB_MIN.b, mScale);
        int g = getColor(RGB_MAX.g, RGB_MIN.g, mScale);
        return Color.rgb(r, g, b);
    }

    /**
     * 根据 scale 获取单一色值
     *
     * @param maxValue
     * @param minValue
     * @return
     */
    private int getColor(int maxValue, int minValue, float scale) {
        return (int) ((float) (maxValue - minValue) * (scale) + (float) minValue);
    }

    private int dip2px(float size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
                getResources().getDisplayMetrics());
    }

    private static class RGB {
        int r, g, b;


        public RGB(int color) {
            this.r = Color.red(color);
            this.g = Color.green(color);
            this.b = Color.blue(color);
        }

        public RGB(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
}
