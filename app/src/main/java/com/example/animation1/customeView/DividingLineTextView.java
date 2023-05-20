package com.example.animation1.customeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.example.animation1.R;

public class DividingLineTextView extends androidx.appcompat.widget.AppCompatTextView {
    //线性渐变
    private LinearGradient mLinearGradient;
    //textPaint
    private TextPaint mPaint;
    //文字
    private String mText = "";
    //屏幕宽度
    private int mScreenWidth;
    //开始颜色
    private int mStartColor;
    //结束颜色
    private int mEndColor;
    //字体大小
    private int mTextSize;

    //构造函数
    public DividingLineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTextSize = getResources().getDimensionPixelSize(R.dimen.text_size);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mStartColor = ContextCompat.getColor(getContext(), R.color.purple_200);
        mEndColor = ContextCompat.getColor(getContext(), R.color.teal_700);
        mLinearGradient = new LinearGradient(0, 0, mScreenWidth, 0,
                new int[]{mStartColor, mEndColor, mStartColor},
                new float[]{0, 0.5f, 1f},
                Shader.TileMode.CLAMP);
        mPaint = new TextPaint();
    }

    public DividingLineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DividingLineTextView(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        int len = getTextLength(mText, mPaint);
        // 文字绘制起始坐标
        int sx = mScreenWidth / 2 - len / 2;
        // 文字绘制结束坐标
        int ex = mScreenWidth / 2 + len / 2;
        int height = getMeasuredHeight();
        mPaint.setShader(mLinearGradient);
        // 绘制左边分界线，从左边开始：左边距15dp， 右边距距离文字15dp
        canvas.drawLine(mTextSize, height / 2, sx - mTextSize, height / 2, mPaint);
        mPaint.setShader(mLinearGradient);
        // 绘制右边分界线，从文字右边开始：左边距距离文字15dp，右边距15dp
        canvas.drawLine(ex + mTextSize, height / 2,
                mScreenWidth - mTextSize, height / 2, mPaint);
    }

    //返回指定文字的宽度，单位px
    private int getTextLength(String str, TextPaint paint) {
        return (int) paint.measureText(str);
    }

    //更新文字
    public void update(String text) {
        mText = text;
        setText(mText);
        // 刷新重绘
        requestLayout();
    }
}
