package com.example.animation1

import android.animation.Animator
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Fade
import android.transition.Scene
import android.transition.TransitionManager
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout


class MainActivity2 : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var ball1: ImageView
    lateinit var ball2: ImageView
    lateinit var ball3: ImageView
    lateinit var button1: Button
    lateinit var button2: Button
    lateinit var button3: Button
    lateinit var button4: Button
    lateinit var constraintLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        window.exitTransition = Fade()
        window.returnTransition = Fade()
        window.enterTransition = Fade()
        textView = findViewById<TextView>(R.id.textView2)
        ball1 = findViewById<ImageView>(R.id.ball3)
        ball2 = findViewById<ImageView>(R.id.ball4)
        ball3 = findViewById<ImageView>(R.id.ball5)
        constraintLayout = findViewById(R.id.constraintLayout2)
        button1 = findViewById(R.id.button11)
        button2 = findViewById(R.id.button12)
        button3 = findViewById(R.id.button13)
        button4 = findViewById(R.id.button14)


        button2.setOnClickListener {
            // scene
            val scene = Scene.getSceneForLayout(constraintLayout, R.layout.scene2, this)
            TransitionManager.go(scene, ChangeBounds())
        }
        button1.setOnClickListener {
            // 关键帧
            val frame0 = Keyframe.ofFloat(0f, 0f)
            val frame1 = Keyframe.ofFloat(0.1f, -45f)
            val frame2 = Keyframe.ofFloat(1f, 0f)
            val frameHolder = PropertyValuesHolder.ofKeyframe("rotation", frame0, frame1, frame2)
            val animator: Animator = ObjectAnimator.ofPropertyValuesHolder(textView, frameHolder)
            animator.duration = 1000
            animator.start()
        }

        button4.setOnClickListener {
            val rotationHolder =
                PropertyValuesHolder.ofFloat(
                    "Rotation",
                    50f,
                    -50f,
                    30f,
                    -30f,
                    20f,
                    -20f,
                    10f,
                    -10f,
                    0f
                )
            val scaleXHolder = PropertyValuesHolder.ofFloat("ScaleX", 1f, 2f)
            val scaleYHolder = PropertyValuesHolder.ofFloat("ScaleY", 1f, 2f)
            val animator: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                textView,
                rotationHolder,
                scaleXHolder,
                scaleYHolder
            )
            animator.duration = 2000
            animator.interpolator = AccelerateInterpolator()
            animator.start()
        }

    }
}