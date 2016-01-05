package com.ldy.scale.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.demotab.R;

/**
 * Created by lidongyang on 2015/7/26.
 */
public class ScaleTabLayout extends LinearLayout implements OnPageChangeListener, OnClickListener {

    private static final int BOTTOM_COLOR = 0xFFF5D336;

    private Paint mPaint;
    private int mCount;
    private int mLastPosition;
    private int mCurrPosition;

    private int mBottomColor;
    private int mBottomHeight;

    private int mMovePosition;
    private float mMoveOffset;

    private boolean isClick = false;
    /** 下划线是否随文字宽度 */
    private boolean isFitText= false;
    /** 下划线左右边距 */
    private int linePaddingStart = 0;
    private int linePaddingEnd = 0;
    private int linePaddingBottom = 0;

    private ViewPager mPager;
    private OnTabItemClickListener mTabClicklistener;
    private OnScalePageChangeListener onPagerChangeListener;

    public ScaleTabLayout(Context context) {
        super(context);
    }

    public ScaleTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleLayout);
        mBottomColor = array.getColor(R.styleable.ScaleLayout_bottom_color, BOTTOM_COLOR);
        mBottomHeight = array.getDimensionPixelSize(R.styleable.ScaleLayout_bottom_height, 2);
        isFitText = array.getBoolean(R.styleable.ScaleLayout_fit_text, true);
        linePaddingStart = array.getDimensionPixelSize(R.styleable.ScaleLayout_line_left, 0);
        linePaddingEnd = array.getDimensionPixelSize(R.styleable.ScaleLayout_line_right, 0);
        linePaddingBottom = array.getDimensionPixelSize(R.styleable.ScaleLayout_line_bottom, 0);
        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPaint = new Paint();
        mCount = getChildCount();

        for (int i = 0; i < mCount; i ++) {
            getChildAt(i).setOnClickListener(this);
            getChildAt(i).setTag(i);
        }
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

        mPaint.setColor(mBottomColor);

        View selected = getChildAt(mMovePosition);
        int left = getLeft(selected);
        int right = getRight(selected);
        if (mMoveOffset > 0f && mMovePosition < (getChildCount() - 1)) {
            View nextTitle = getChildAt(mMovePosition + 1);
            left = (int) (mMoveOffset * getLeft(nextTitle) +
                    (1.0f - mMoveOffset) * left);
            right = (int) (mMoveOffset * getRight(nextTitle) +
                    (1.0f - mMoveOffset) * right);
        }

        canvas.drawRect(left, height - mBottomHeight - linePaddingBottom, right, height - linePaddingBottom, mPaint);
    }

    private int getLeft(View view) {
        if ((view instanceof TextView) && isFitText) {
            return view.getLeft() + view.getWidth() / 2 - (int) getTextWidth((TextView) view) / 2;
        } else {
            return view.getLeft() + linePaddingStart;
        }
    }

    private int getRight(View view) {
        if ((view instanceof TextView) && isFitText) {
            return view.getRight() - view.getWidth() / 2+ (int) getTextWidth((TextView) view) / 2;
        } else {
            return view.getRight() - linePaddingEnd;
        }
    }

    private float getTextWidth(TextView textView) {
        return textView.getPaint().measureText(textView.getText().toString());
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
