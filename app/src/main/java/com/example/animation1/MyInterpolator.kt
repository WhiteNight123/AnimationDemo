package com.example.animation1

import android.view.animation.Interpolator

/**
 * @author WhiteNight(1448375249@qq.com)
 * @date 2022-07-26
 * @description
 */
class MyInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        return 1 - input
    }
}