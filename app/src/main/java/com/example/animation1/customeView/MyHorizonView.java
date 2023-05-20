package com.example.animation1.customeView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MyHorizonView extends ViewGroup {

    private List<View> mMatchedChildrenList = new ArrayList<>();

    public MyHorizonView(Context context) {
        super(context);
    }

    public MyHorizonView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public MyHorizonView(Context context, AttributeSet attributes, int defStyleAttr) {
        super(context, attributes, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int left = 0;
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int childWidth = child.getMeasuredWidth();
                // 因为是水平滑动的，所以以宽度来适配
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
                left += childWidth;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMatchedChildrenList.clear();
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        // 如果不是确定的的值，说明是 AT_MOST,与父 View 同宽高
        final boolean measureMatchParentChildren = heightSpecMode != MeasureSpec.EXACTLY ||
                widthSpecMode != MeasureSpec.EXACTLY;
        int childCount = getChildCount();
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final LayoutParams layoutParams = child.getLayoutParams();
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                if (measureMatchParentChildren) {
                    // 需要先计算出父 View 的高度来再来测量子 view
                    if (layoutParams.width == LayoutParams.MATCH_PARENT
                            || layoutParams.height == LayoutParams.MATCH_PARENT) {
                        mMatchedChildrenList.add(child);
                    }
                }
            }
        }

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            // 如果宽高都是AT_MOST的话，即都是wrap_content布局模式，就用View自己想要的宽高值
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            // 如果只有宽度都是AT_MOST的话，即只有宽度是wrap_content布局模式，宽度就用View自己想要的宽度值，高度就用父ViewGroup指定的高度值
            setMeasuredDimension(getMeasuredWidth(), heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            // 如果只有高度都是AT_MOST的话，即只有高度是wrap_content布局模式，高度就用View自己想要的宽度值，宽度就用父ViewGroup指定的高度值
            setMeasuredDimension(widthSpecSize, getMeasuredHeight());
        }

        for (int i = 0; i < mMatchedChildrenList.size(); i++) {
            View matchChild = getChildAt(i);
            if (matchChild.getVisibility() != View.GONE) {
                final LayoutParams layoutParams = matchChild.getLayoutParams();
                // 计算子 View 宽的 MeasureSpec
                final int childWidthMeasureSpec;
                if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, layoutParams.width);
                }
                // 计算子 View 高的 MeasureSpec
                final int childHeightMeasureSpec;
                if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, layoutParams.height);
                }
                // 根据 MeasureSpec 计算自己的宽高
                matchChild.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }
}