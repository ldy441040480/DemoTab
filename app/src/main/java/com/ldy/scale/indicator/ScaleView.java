package com.ldy.scale.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.administrator.demotab.R;

/**
 * Created by lidongyang on 2015/7/26.
 */
public class ScaleView extends View {

    private static final String INSTANCE_STATE = "instance_state";
    private static final String STATE_SCALE = "state_scale";
    private static final float MAX_RATE = 1f / 5f;  // 最大变化比率
    private static final float MIN_RATE = 0;  // 最小变化比率
    private static final float MAX_SIZE = 20f;
    private static final float MIN_SIZE = 15f;
    private final RGB RGB_MAX = new RGB(255, 255, 255);
    private final RGB RGB_MIN = new RGB(255, 128, 128);

    private float mScale = -1f;
    private float mMinSize;
    private float mMaxSize;
    private String mText;

    private Paint mTextPaint;

    public ScaleView(Context context) {
        super(context);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        mMinSize = array.getDimension(R.styleable.ScaleView_minSize, dip2px(MIN_SIZE));
        mMaxSize = array.getDimension(R.styleable.ScaleView_maxSize, dip2px(MAX_SIZE));
        mText = array.getString(R.styleable.ScaleView_text);
        if (mText == null)
            mText = "";
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTextPaint.setTextSize(getTabSize());
        mTextPaint.setColor(getTabColor());
        FontMetricsInt metricsInt = mTextPaint.getFontMetricsInt();
        int baseline = getTop() + (getBottom() - getTop() - metricsInt.bottom + metricsInt.top) / 2 - metricsInt.top;
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mText, getWidth() / 2, baseline, mTextPaint);
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

            invalidateView();
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
        return (int) ((float) (maxValue - minValue) * (1 - scale) + (float) minValue);
    }

    private int dip2px(float size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
                getResources().getDisplayMetrics());
    }

    private static class RGB {
        int r, g, b;

        public RGB(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    protected final void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(STATE_SCALE, mScale);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mScale = bundle.getFloat(STATE_SCALE);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
