package com.example.animation1;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class FallingBallImageView extends ImageView {
    public FallingBallImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //set函数对应的属性是fallingPos或FallingPos
    public void setFallingPos(Point pos) {
        layout(pos.x, pos.y, pos.x + getWidth(), pos.y + getHeight());
    }
}
