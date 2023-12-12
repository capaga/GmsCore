package org.microg.tools.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.Random;

public class CircleTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Paint mPaint;
    private TypedArray typedArray;
    private float mRound;
    private int mBackgroundColor;

    /**
     * set random background color
     */
    public void setRandomBackgroundColor() {
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);

        String sr = Integer.toHexString(r);
        String sg = Integer.toHexString(g);
        String sb = Integer.toHexString(b);

        StringBuffer stringBuffer = new StringBuffer("#");
        stringBuffer.append(transform(sr)).append(transform(sg)).append(transform(sb));
        this.mBackgroundColor = Color.parseColor(stringBuffer.toString());
        invalidate();
    }

    private String transform(String color) {
        if (color.length() == 1) {
            return "0" + color;
        }
        return color;
    }


    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CircleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleTextView, 0, 0);
        mRound = typedArray.getDimension(R.styleable.CircleTextView_xRound, 5);
        mBackgroundColor = typedArray.getColor(R.styleable.CircleTextView_xBackground, getResources().getColor(R.color.settings_theme_accent));
        initConfig();

    }

    private void initConfig() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackgroundColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mBackgroundColor);
        canvas.drawCircle(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f, mRound, mPaint);
        super.onDraw(canvas);
    }
}
