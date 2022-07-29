package com.example.animation1

import android.animation.TypeEvaluator

class MyEvaluator : TypeEvaluator<Int> {
    override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
        return (endValue - fraction * (endValue - startValue)).toInt()
    }
}