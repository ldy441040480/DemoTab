package com.ldy.scale.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private Paint mPaint;
    private int mCount;
    private int mLastPosition;
    private int mCurrPosition;

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
    private boolean isChangeColor;
    private boolean isChangeBottomColor;

    private ViewPager mPager;
    private OnTabItemClickListener mTabClickListener;
    private OnScalePageChangeListener onPagerChangeListener;
    private ScaleTextView[] mScaleViews;

    public ScaleTabLayout(Context context) {
        super(context);
    }

    public ScaleTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleLayout);
        mBottomHeight = array.getDimensionPixelSize(R.styleable.ScaleLayout_bottomHeight, 2);
        isFitText = array.getBoolean(R.styleable.ScaleLayout_fitText, true);
        linePaddingStart = array.getDimensionPixelSize(R.styleable.ScaleLayout_lineLeft, 0);
        linePaddingEnd = array.getDimensionPixelSize(R.styleable.ScaleLayout_lineRight, 0);
        linePaddingBottom = array.getDimensionPixelSize(R.styleable.ScaleLayout_lineBottom, 0);
        isChangeColor = array.getBoolean(R.styleable.ScaleLayout_changeColor, true);
        isChangeBottomColor = array.getBoolean(R.styleable.ScaleLayout_changeBottomColor, false);
        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPaint = new Paint();
        mCount = getChildCount();
        mScaleViews = new ScaleTextView[mCount];
        for (int i = 0; i < mCount; i++) {
            mScaleViews[i] = (ScaleTextView) getChildAt(i);
            mScaleViews[i].setOnClickListener(this);
            mScaleViews[i].setTag(i);
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

    public void setTabTitles(String[] titles) {
        for (int i = 0; i < mCount; i++) {
            mScaleViews[i].setText(titles[i]);
        }
    }

    public void setTabColor(int[] colors) {
        for (int i = 0; i < mCount; i++) {
            mScaleViews[i].setFromColor(colors[i]);
        }
    }

    public void setTabsTitleAndColor(String[] titles, int[] colors) {
        for (int i = 0; i < mCount; i++) {
            mScaleViews[i].setText(titles[i]);
            mScaleViews[i].setFromColor(colors[i]);
        }
    }

    @Override
    public void onClick(View v) {
        if (mPager != null) {
            isClick = true;
            mLastPosition = mPager.getCurrentItem();
            int position = (Integer) v.getTag();
            mPager.setCurrentItem(position);

            if (mTabClickListener != null) {
                mTabClickListener.onTabClick(v, position);
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
        ScaleTextView view = mScaleViews[position];
        if (isChangeColor) {
            int curColor = getCurColor(view.getFromColor(), view.getToColor(), scale);
            view.setTabScale(scale, curColor, isChangeColor);
        } else {
            view.setTabScale(scale, view.getFromColor(), isChangeColor);
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

        ScaleTextView selected = mScaleViews[mMovePosition];
        mPaint.setColor(selected.getFromColor());

        int left = getLeft(selected);
        int right = getRight(selected);
        if (mMoveOffset > 0f && mMovePosition < (getChildCount() - 1)) {
            ScaleTextView nextTitle = mScaleViews[mMovePosition + 1];

            left = (int) (mMoveOffset * getLeft(nextTitle) +
                    (1.0f - mMoveOffset) * left);
            right = (int) (mMoveOffset * getRight(nextTitle) +
                    (1.0f - mMoveOffset) * right);

            if (isChangeBottomColor) {
                int curColor = getCurColor(nextTitle.getChangeColor(), selected.getChangeColor(), mMoveOffset);
                mPaint.setColor(curColor);
            } else {
                if (mMoveOffset > 0.5f) {
                    mPaint.setColor(nextTitle.getFromColor());
                } else {
                    mPaint.setColor(selected.getFromColor());
                }
            }
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
        this.mTabClickListener = listener;
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

    private int getCurColor(int maxColor, int minColor, float offset) {
        int r = getColor(Color.red(maxColor), Color.red(minColor), offset);
        int b = getColor(Color.blue(maxColor), Color.blue(minColor), offset);
        int g = getColor(Color.green(maxColor), Color.green(minColor), offset);
        return Color.rgb(r, g, b);
    }

    private int getColor(int maxValue, int minValue, float offset) {
        return (int) ((float) (maxValue - minValue) * (offset) + (float) minValue);
    }

}
