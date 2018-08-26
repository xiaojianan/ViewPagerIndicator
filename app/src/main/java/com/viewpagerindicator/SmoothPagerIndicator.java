package com.viewpagerindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class SmoothPagerIndicator extends View {
    private static final String TAG = "SmoothPagerIndicator";
    private Paint mPaint;
    private int mIndicatorColor;
    private int mSelectedIndicatorColor;
    private float mIndicatorWidth;
    private float mSelectedIndicatorWidth;
    private float mIndicatorDistance;
    private float mIndicatorCorner;

    private ViewPager mViewPager;
    private int mIndicatorNum;

    private float mStartX;
    private float mRadius;
    private int mScrollState;
    private int mScrollDirection = 0; // 1向右滑 -1向左滑
    private float mPrePositionOffset = 0;
    private int mCurrentItem = 0;

    public SmoothPagerIndicator(Context context) {
        super(context);
        initPaint();
    }

    public SmoothPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setStyleable(context, attrs);
        initPaint();
    }

    public SmoothPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setStyleable(context, attrs);
        initPaint();
    }

    private void setStyleable(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SmoothPagerIndicator);
        mIndicatorColor = array.getColor(R.styleable.SmoothPagerIndicator_indicator_color, 0x3fffffff);
        mSelectedIndicatorColor = array.getColor(R.styleable.SmoothPagerIndicator_indicator_selectedColor, 0xffffffff);
        mIndicatorWidth = array.getDimension(R.styleable.SmoothPagerIndicator_indicator_width, 20);
        mSelectedIndicatorWidth = array.getDimension(R.styleable.SmoothPagerIndicator_indicator_selectedWidth, 40);
        mIndicatorDistance = array.getDimension(R.styleable.SmoothPagerIndicator_indicator_distance, 20);
        mIndicatorCorner = array.getDimension(R.styleable.SmoothPagerIndicator_indicator_corner, 9999);
        array.recycle();
    }

    public void setViewPager(final ViewPager mViewPager) {
        this.mViewPager = mViewPager;

        if (mViewPager == null || mViewPager.getAdapter() == null || mViewPager.getAdapter().getCount() <= 1) {
            setVisibility(View.GONE);
            return;
        }

        mIndicatorNum = mViewPager.getAdapter().getCount();
        mCurrentItem = mViewPager.getCurrentItem();
        setVisibility(VISIBLE);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mPrePositionOffset != 0 && positionOffset != 0) {
                    if (mScrollState == ViewPager.SCROLL_STATE_DRAGGING) {
                        if (positionOffset - mPrePositionOffset > 0f) {
                            mScrollDirection = 1;
                        } else if (positionOffset - mPrePositionOffset < 0f) {
                            mScrollDirection = -1;
                        }
                    }
                }
                if (mScrollDirection == 1) {
                    mCurrentItem = position;
                } else if (mScrollDirection == -1) {
                    mCurrentItem = position + 1;
                }
                invalidate();

                mPrePositionOffset = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mScrollState = state;
                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    mCurrentItem = mViewPager.getCurrentItem();
                    mScrollDirection = 0;
                }
                invalidate();
            }
        });
        postInvalidate();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mStartX = (w - (mIndicatorNum - 1f) * (mIndicatorDistance + mIndicatorWidth) - mSelectedIndicatorWidth) / 2f;
        mRadius = mIndicatorWidth * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mViewPager == null) {
            return;
        }

        int height = canvas.getHeight();
        canvas.translate(mStartX, height / 2f);

        for (int i = 0; i < mIndicatorNum; i++) {
            RectF rect = new RectF(0, -mRadius, 0, mRadius);
            if (i == mCurrentItem) {
                if (mScrollDirection == 1) {
                    int dismissColor = GradientColorUtil.caculateColor(mSelectedIndicatorColor, mIndicatorColor, mPrePositionOffset);
                    rect.left = i * (mIndicatorWidth + mIndicatorDistance);
                    rect.right = i * (mIndicatorWidth + mIndicatorDistance) + mIndicatorWidth + (mSelectedIndicatorWidth - mIndicatorWidth) * (1f - mPrePositionOffset);
                    mPaint.setColor(dismissColor);
                    canvas.drawRoundRect(rect, mIndicatorCorner, mIndicatorCorner, mPaint);
                } else if (mScrollDirection == -1) {
                    int dismissColor = GradientColorUtil.caculateColor(mSelectedIndicatorColor, mIndicatorColor, 1f - mPrePositionOffset);
                    rect.left = i * (mIndicatorWidth + mIndicatorDistance) + (mSelectedIndicatorWidth - mIndicatorWidth) * (1 - mPrePositionOffset);
                    rect.right = i * (mIndicatorWidth + mIndicatorDistance) + mSelectedIndicatorWidth;
                    mPaint.setColor(dismissColor);
                    canvas.drawRoundRect(rect, mIndicatorCorner, mIndicatorCorner, mPaint);
                } else if (mScrollDirection == 0) {
                    rect.left = i * (mIndicatorWidth + mIndicatorDistance);
                    rect.right = i * (mIndicatorWidth + mIndicatorDistance) + mSelectedIndicatorWidth;
                    mPaint.setColor(mSelectedIndicatorColor);
                    canvas.drawRoundRect(rect, mIndicatorCorner, mIndicatorCorner, mPaint);
                }
            } else if (i == mCurrentItem + 1) {
                if (mScrollDirection == 1) {
                    int appearColor = GradientColorUtil.caculateColor(mIndicatorColor, mSelectedIndicatorColor, mPrePositionOffset);
                    rect.left = (i - 1) * (mIndicatorWidth + mIndicatorDistance) + mIndicatorWidth + (mSelectedIndicatorWidth - mIndicatorWidth) * (1f - mPrePositionOffset) + mIndicatorDistance;
                    rect.right = (i - 1) * (mIndicatorWidth + mIndicatorDistance) + mSelectedIndicatorWidth + mIndicatorWidth + mIndicatorDistance;
                    mPaint.setColor(appearColor);
                    canvas.drawRoundRect(rect, mIndicatorCorner, mIndicatorCorner, mPaint);
                } else {
                    rect.left = (i - 1) * (mIndicatorWidth + mIndicatorDistance) + mSelectedIndicatorWidth + mIndicatorDistance;
                    rect.right = (i - 1) * (mIndicatorWidth + mIndicatorDistance) + mSelectedIndicatorWidth + mIndicatorWidth + mIndicatorDistance;
                    mPaint.setColor(mIndicatorColor);
                    canvas.drawRoundRect(rect, mIndicatorCorner, mIndicatorCorner, mPaint);
                }
            } else if (i == mCurrentItem - 1) {
                if (mScrollDirection == -1) {
                    int appearColor = GradientColorUtil.caculateColor(mIndicatorColor, mSelectedIndicatorColor, 1f - mPrePositionOffset);
                    rect.left = i * (mIndicatorWidth + mIndicatorDistance);
                    rect.right = i * (mIndicatorWidth + mIndicatorDistance) + mIndicatorWidth + (mSelectedIndicatorWidth - mIndicatorWidth) * (1f - mPrePositionOffset);
                    mPaint.setColor(appearColor);
                    canvas.drawRoundRect(rect, mIndicatorCorner, mIndicatorCorner, mPaint);
                } else {
                    rect.left = i * (mIndicatorWidth + mIndicatorDistance);
                    rect.right = i * (mIndicatorWidth + mIndicatorDistance) + mIndicatorWidth;
                    mPaint.setColor(mIndicatorColor);
                    canvas.drawRoundRect(rect, mIndicatorCorner, mIndicatorCorner, mPaint);
                }
            } else if (i < mCurrentItem - 1) {
                rect.left = i * (mIndicatorWidth + mIndicatorDistance);
                rect.right = i * (mIndicatorWidth + mIndicatorDistance) + mIndicatorWidth;
                mPaint.setColor(mIndicatorColor);
                canvas.drawRoundRect(rect, mIndicatorCorner, mIndicatorCorner, mPaint);
            } else if (i > mCurrentItem + 1) {
                rect.left = (i - 1) * (mIndicatorWidth + mIndicatorDistance) + mSelectedIndicatorWidth + mIndicatorDistance;
                rect.right = (i - 1) * (mIndicatorWidth + mIndicatorDistance) + mSelectedIndicatorWidth + mIndicatorWidth + mIndicatorDistance;
                mPaint.setColor(mIndicatorColor);
                canvas.drawRoundRect(rect, mIndicatorCorner, mIndicatorCorner, mPaint);
            }
            super.onDraw(canvas);
        }
    }
}