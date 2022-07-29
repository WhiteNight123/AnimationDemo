package com.example.animation1;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.core.view.ViewCompat;


public class MyTransition extends Transition {
    private static final String TOP = "top";
    private static final String HEIGHT = "height";

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        Rect rect = new Rect();
        view.getHitRect(rect);
        transitionValues.values.put(TOP, rect.top);
        transitionValues.values.put(HEIGHT, view.getHeight());
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(TOP, 0);
        transitionValues.values.put(HEIGHT, transitionValues.view.getHeight());
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        final View endView = endValues.view;
        final int startTop = (int) startValues.values.get(TOP);
        final int startHeight = (int) startValues.values.get(HEIGHT);
        final int endTop = (int) endValues.values.get(TOP);
        final int endHeight = (int) endValues.values.get(HEIGHT);

        endView.setTranslationY(startTop);
        endView.getLayoutParams().height = startHeight;
        endView.requestLayout();
        ValueAnimator positionAnimator = ValueAnimator.ofInt(startTop, endTop);
        positionAnimator.setDuration(200);
        positionAnimator.setInterpolator(new LinearInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int current = (int) positionAnimator.getAnimatedValue();
                endView.setTranslationY(current);
            }
        });
        ValueAnimator sizeAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        sizeAnimator.setDuration(200);
        sizeAnimator.setInterpolator(new LinearInterpolator());
        sizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int current = (int) valueAnimator.getAnimatedValue();
                endView.getLayoutParams().height = current;
                endView.requestLayout();
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.play(sizeAnimator).after(positionAnimator);
        return set;
    }
}
