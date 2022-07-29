package com.example.animation1

import android.view.View

class WrapperView(private val mTarget: View) {
    var width: Int
        get() = mTarget.layoutParams.width
        set(width) {
            mTarget.layoutParams.width = width
            mTarget.requestLayout()
        }
}