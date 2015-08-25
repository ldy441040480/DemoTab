package com.ldy.scale.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 * Created by lidongyang on 2015/7/26.
 */
public class ScaleTabLayout extends LinearLayout implements OnPageChangeListener, OnClickListener {

    private static final int BOTTOM_DIPS = 2;
    private static final int BOTTOM_COLOR = Color.parseColor("#FF9AA0");

    private Paint mPaint;
    private float mDensity;
    private int mCount;
    private int mLastPosition;
    private int mCurrPosition;

    private int mBottomColor = BOTTOM_COLOR;
    private int mMovePosition;
    private float mMoveOffset;

    private boolean isClick = false;
    private ViewPager mPager;
    private OnTabItemClickListener mTabClicklistener;
    private OnScalePageChangeListener onPagerChangeListener;

    public ScaleTabLayout(Context context) {
        super(context);
    }

    public ScaleTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDensity = getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setColor(mBottomColor);
        mCount = getChildCount();

        for (int i = 0; i < mCount; i ++) {
            getChildAt(i).setOnClickListener(this);
            getChildAt(i).setTag(i);
        }
    }

    public void setBottomColor(int color) {
        this.mBottomColor = color;
        invalidate();
    }

    public void setViewPager(ViewPager pager) {
        mPager = pager;
        if (mPager != null) {
            mPager.setOnPageChangeListener(this);
        }
    }

    public void setCurrentItem(int position) {
        mPager.setCurrentItem(position, false);
    }

    @Override
    public void onClick(View v) {
        if (mPager != null) {
            isClick = true;
            mLastPosition = mPager.getCurrentItem();
            int position = (Integer) v.getTag();
            mPager.setCurrentItem(position);

            if (mTabClicklistener != null) {
                mTabClicklistener.onTabClick(v, position);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrPosition = mPager.getCurrentItem();

        if (onPagerChangeListener != null) {
            onPagerChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position < 0 || position >= mCount) {
            return;
        }

        if (positionOffset == 0 || position == mCount -1) {
            setTabScale(position);
            setMoveLine(position, 0f);
            isClick = false;
            return;
        }

        if (isClick) {
            if (mLastPosition < mCurrPosition) {
                if (position == mCurrPosition - 1) {
                    setTabScale(mLastPosition, 1 - positionOffset);
                    setTabScale(mCurrPosition, positionOffset);
                }
            } else if (mLastPosition > mCurrPosition) {
                if (position == mCurrPosition) {
                    setTabScale(mLastPosition, positionOffset);
                    setTabScale(mCurrPosition, 1 - positionOffset);
                }
            }
        } else {
            setTabScale(position, 1 - positionOffset);
            setTabScale(position + 1, positionOffset);
        }
        setMoveLine(position, positionOffset);

        if (onPagerChangeListener != null) {
            onPagerChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (onPagerChangeListener != null) {
            onPagerChangeListener.onPageScrollStateChanged(state);
        }
    }

    private void setTabScale(int position) {
        for (int i = 0; i < mCount; i ++) {
            setTabScale(i, (position == i) ? 1f : 0f);
        }
    }

    /**
     * 变化title
     *
     * @param position
     * @param scale
     */
    private void setTabScale(int position, float scale) {
        if (position < 0 || position >= mCount) {
            return;
        }
        View view = getChildAt(position);
        if (view instanceof ScaleTextView) {
            ((ScaleTextView) view).setTabScale(scale);
        } else if (view instanceof ScaleView) {
            ((ScaleView) view).setTabScale(scale);
        }
    }

    /**
     *滑动线
     *
     * @param position
     * @param positionOffset
     */
    private void setMoveLine(int position, float positionOffset) {
        mMovePosition = position;
        mMoveOffset = positionOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        View selected = getChildAt(mMovePosition);
        int left = selected.getLeft();
        int right = selected.getRight();
        if (mMoveOffset > 0f && mMovePosition < (getChildCount() - 1)) {
            View nextTitle = getChildAt(mMovePosition + 1);
            left = (int) (mMoveOffset * nextTitle.getLeft() +
                    (1.0f - mMoveOffset) * left);
            right = (int) (mMoveOffset * nextTitle.getRight() +
                    (1.0f - mMoveOffset) * right);
        }
        int space = (right - left) / (mCount * 2);
        canvas.drawRect(left + space, height - (int) (BOTTOM_DIPS * mDensity),
                right - space, height, mPaint);
    }

    public void setOnTabItemClickListener(OnTabItemClickListener listener) {
        this.mTabClicklistener = listener;
    }

    public interface OnTabItemClickListener {
        void onTabClick(View view, int position);
    }

    public void setOnScalePageChangeListener(OnScalePageChangeListener listener) {
        this.onPagerChangeListener = listener;
    }

    public interface OnScalePageChangeListener {

        void onPageSelected(int position);

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageScrollStateChanged(int state);
    }
}
