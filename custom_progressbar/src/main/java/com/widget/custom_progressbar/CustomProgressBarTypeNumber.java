package com.widget.custom_progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * 自定义的水平进度条
 * Created by raoxuting on 2017/6/6.
 */

public class CustomProgressBarTypeNumber extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;

    /**
     * painter of all drawing things
     */
    protected Paint mPaint = new Paint();
    /**
     * color of progress number
     */
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    /**
     * size of text (sp)
     */
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);

    /**
     * offset of draw progress
     */
    protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);

    /**
     * height of reached progress bar
     */
    protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);

    /**
     * color of reached bar
     */
    protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
    /**
     * color of unreached bar
     */
    protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
    /**
     * height of unreached progress bar
     */
    protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
    /**
     * view width except padding
     */
    protected int mRealWidth;

    protected boolean mIfDrawText = true;

    protected static final int VISIBLE = 0;

    protected static final int HORIZONTAL = 2;

    protected static final int RING = 3;

    /**
     * mRadius of view
     */
    private int mRadius = dp2px(30);

    /**
     * Type of progressbar, type horizontal or type ring
     */
    private int progressStyle;

    private int mMaxPaintWidth;

    public CustomProgressBarTypeNumber(Context context) {
        this(context, null);
    }

    public CustomProgressBarTypeNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBarTypeNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainStyledAttributes(attrs);

        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);

        if (progressStyle == RING) {
            mReachedProgressBarHeight = (int) (mUnReachedProgressBarHeight * 2.5f);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }

    }

    /**
     * get the styled attributes
     *
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs) {
        // init values from custom attributes
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomProgressBarTypeNumber);

        progressStyle = attributes.getInt(R.styleable.CustomProgressBarTypeNumber_cus_progressStyle,
                HORIZONTAL);
        mRadius = (int) attributes.getDimension(
                R.styleable.CustomProgressBarTypeNumber_cus_radius, mRadius);
        mTextColor = attributes.getColor(
                R.styleable.CustomProgressBarTypeNumber_cus_progress_text_color,
                DEFAULT_TEXT_COLOR);
        mTextSize = (int) attributes.getDimension(
                R.styleable.CustomProgressBarTypeNumber_cus_progress_text_size,
                mTextSize);

        mReachedBarColor = attributes.getColor(
                R.styleable.CustomProgressBarTypeNumber_cus_progress_reached_color,
                mTextColor);
        mUnReachedBarColor = attributes.getColor(
                R.styleable.CustomProgressBarTypeNumber_cus_progress_unreached_color,
                DEFAULT_COLOR_UNREACHED_COLOR);
        mReachedProgressBarHeight = (int) attributes.getDimension(
                R.styleable.CustomProgressBarTypeNumber_cus_progress_reached_bar_height,
                mReachedProgressBarHeight);
        mUnReachedProgressBarHeight = (int) attributes.getDimension(
                R.styleable.CustomProgressBarTypeNumber_cus_progress_unreached_bar_height,
                mUnReachedProgressBarHeight);
        mTextOffset = (int) attributes.getDimension(
                R.styleable.CustomProgressBarTypeNumber_cus_progress_text_offset,
                mTextOffset);

        int textVisible = attributes
                .getInt(R.styleable.CustomProgressBarTypeNumber_cus_progress_text_visibility,
                        VISIBLE);
        if (textVisible != VISIBLE) {
            mIfDrawText = false;
        }
        if (progressStyle == RING) {
            mReachedProgressBarHeight = (int) (mUnReachedProgressBarHeight * 2.5f);
            mTextSize = sp2px(14);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        attributes.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {

        if (progressStyle == HORIZONTAL) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = measureHeight(heightMeasureSpec);
            setMeasuredDimension(width, height);

            mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();

        } else {
            mMaxPaintWidth = Math.max(mReachedProgressBarHeight,
                    mUnReachedProgressBarHeight);
            int expect = mRadius * 2 + mMaxPaintWidth + getPaddingLeft()
                    + getPaddingRight();
            int width = resolveSize(expect, widthMeasureSpec);
            int height = resolveSize(expect, heightMeasureSpec);
            int realWidth = Math.min(width, height);

            mRadius = (realWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;

            setMeasuredDimension(realWidth, realWidth);

        }

    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            float textHeight = (mPaint.descent() - mPaint.ascent());
            result = (int) (getPaddingTop() + getPaddingBottom() + Math.max(
                    Math.max(mReachedProgressBarHeight,
                            mUnReachedProgressBarHeight), Math.abs(textHeight)));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        if (progressStyle == HORIZONTAL) {
            //平移之前保存画布当前状态,避免影响到其他无须平移的元素
            canvas.save();
            //画笔平移到指定paddingLeft， getHeight() / 2位置，注意以后坐标都以此为0，0
            canvas.translate(getPaddingLeft(), getHeight() / 2);

            boolean noNeedBg = false;
            //当前进度和总值的比例
            float ratio = getProgress() * 1.0f / getMax();
            //已到达的宽度
            float progressPosX = (int) (mRealWidth * ratio);
            //绘制的文本
            String text = getProgress() + "%";

            //拿到字体的宽度和高度
            float textWidth = mPaint.measureText(text);
            float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

            //如果到达最后，则未到达的进度条不需要绘制
            if (progressPosX + textWidth > mRealWidth) {
                progressPosX = mRealWidth - textWidth;
                noNeedBg = true;
            }

            // 绘制已到达的进度
            float endX = progressPosX - mTextOffset / 2;
            if (endX > 0) {
                mPaint.setColor(mReachedBarColor);
                mPaint.setStrokeWidth(mReachedProgressBarHeight);
                canvas.drawLine(0, 0, endX, 0, mPaint);
            }

            // 绘制文本
            if (mIfDrawText) {
                mPaint.setColor(mTextColor);
                canvas.drawText(text, progressPosX, -textHeight, mPaint);
            }

            // 绘制未到达的进度条
            if (!noNeedBg) {
                float start = progressPosX + mTextOffset / 2 + textWidth;
                mPaint.setColor(mUnReachedBarColor);
                mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
                canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
            }

        } else {
            String text = getProgress() + "%";
            // mPaint.getTextBounds(text, 0, text.length(), mTextBound);
            float textWidth = mPaint.measureText(text);
            float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

            canvas.save();
            canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2,
                    getPaddingTop() + mMaxPaintWidth / 2);
            mPaint.setStyle(Paint.Style.STROKE);
            // draw unreaded bar
            mPaint.setColor(mUnReachedBarColor);
            mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
            // draw reached bar
            mPaint.setColor(mReachedBarColor);
            mPaint.setStrokeWidth(mReachedProgressBarHeight);
            float sweepAngle = -getProgress() * 1.0f / getMax() * 360;
            canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), 90,
                    sweepAngle, false, mPaint);
            // draw text
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight,
                    mPaint);

        }
        canvas.restore();


    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mRealWidth = w - getPaddingRight() - getPaddingLeft();
//
//    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }
}
