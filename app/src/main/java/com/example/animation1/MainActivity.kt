package com.example.animation1


import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.animation.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair


class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var ball1: ImageView
    lateinit var ball2: ImageView

    lateinit var button1: Button
    lateinit var button2: Button
    lateinit var button3: Button
    lateinit var button4: Button
    lateinit var button5: Button
    lateinit var button6: Button
    lateinit var button7: Button
    lateinit var button8: Button
    lateinit var button9: Button
    lateinit var button10: Button
    lateinit var button11: Button


    lateinit var construntLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById<TextView>(R.id.hello_world)
        ball1 = findViewById<ImageView>(R.id.ball1)
        ball2 = findViewById<ImageView>(R.id.ball2)

        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)
        button7 = findViewById(R.id.button7)
        button8 = findViewById(R.id.button8)
        button9 = findViewById(R.id.button9)
        button10 = findViewById(R.id.button10)
        button11 = findViewById(R.id.button11)

        construntLayout = findViewById(R.id.constraintLayout)
        textView.setOnClickListener {
            doAnimation1()
            //textView.animate().alpha(0f).x(50f).y(50f)
//            val animator = AnimatorInflater.loadAnimator(this, R.animator.animator_translation)
//            animator.setTarget(textView)
//            animator.start()
        }
        ball1.setOnClickListener {
            doAnimation2()
        }
        ball2.setOnClickListener {
            val animator = ObjectAnimator.ofObject(
                ball2,
                "fallingPos",
                FallingBallEvaluator(),
                Point(ball2.x.toInt(), 0),
                Point(ball2.x.toInt() - 500, 500)
            )
            animator.duration = 1000
            animator.start()
        }
        button1.setOnClickListener {
            //从0旋转到360°
            val animator = ObjectAnimator.ofFloat(textView, "rotation", 0f, 360f)
            // animator.interpolator = OvershootInterpolator()
            animator.duration = 1000
            animator.start()
        }
        button2.setOnClickListener {
            //在X轴上平移300f
            val curTranslationX = textView.translationX
            val animator = ObjectAnimator.ofFloat(
                textView,
                "translationX",
                curTranslationX,
                300f,
                curTranslationX
            )
            animator.duration = 1000
            animator.start()
        }
        button3.setOnClickListener {
            //在X轴上放大2倍再还原
//            val animator = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 3f, 1f)
//            animator.duration = 1000
//            animator.start()

            doAnimation3()
        }
        button4.setOnClickListener {
            //透明度从1到0再到1
            val animator = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f, 1f)
            animator.duration = 1000
            animator.start()
        }
        button5.setOnClickListener {
            doAnimation4()
        }
        button6.setOnClickListener {
            doAnimation5()
        }
        button7.setOnClickListener {
            // activity之间转场
            doAnimation7()
        }
        button8.setOnClickListener {
            //doAnimation8()
            startActivity(Intent(this, MainActivity4::class.java))
        }
        button9.setOnClickListener {
            doAnimator9()
        }
        button10.setOnClickListener {
            // fragment 之间转场
            val intent = Intent(this, MainActivity3::class.java)
            val transitionActivityOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(textView, "translation1")
                )
            startActivity(intent, transitionActivityOptions.toBundle())
        }
        button11.setOnClickListener {
            startActivity(Intent(this, MainActivity6::class.java))
        }
    }

    private fun doAnimation1() {
        val animator = ValueAnimator.ofInt(0, 400)
        animator.duration = 500
        animator.startDelay = 200
        animator.repeatCount = 0
        animator.repeatMode = ValueAnimator.RESTART
        // animator.interpolator = MyInterpolator()
        animator.setEvaluator(MyEvaluator())
        //animator.interpolator = Test2()

        animator.addUpdateListener { animation ->
            val curValue = animation.animatedValue as Int
            textView.layout(
                curValue,
                curValue,
                curValue + textView.width,
                curValue + textView.height
            )
        }
        animator.start()
    }

    private fun doAnimation2() {
        val animator = ValueAnimator.ofObject(FallingBallEvaluator(), Point(0, 0), Point(500, 500))
        animator.addUpdateListener { animation ->
            val mCurPoint = animation.animatedValue as Point
            ball1.layout(
                mCurPoint.x,
                mCurPoint.y,
                mCurPoint.x + ball1.width,
                mCurPoint.y + ball1.height
            )
        }
        animator.duration = 2000
        animator.start()
    }

    private fun doAnimation3() {
        val wrapper = WrapperView(textView)
        val animator = ObjectAnimator.ofInt(wrapper, "width", 500)
        animator.duration = 1000
        animator.start()
        //AnimatorSet().playTogether()

    }

    private fun doAnimation4() {
        val tvAnimator1 = ObjectAnimator.ofInt(
            textView, "textColor",
            0xffff00ff.toInt(), 0xffffff00.toInt(), 0xffff00ff.toInt()
        )
        val tvAnimator2 = ObjectAnimator.ofFloat(textView, "translationY", 0f, 300f, 0f)
        val tvAnimator3 = ObjectAnimator.ofFloat(textView, "rotation", 0f, 180f, 0f)
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(tvAnimator1, tvAnimator2, tvAnimator3)
        animatorSet.duration = 1000
        animatorSet.start()
    }

    private fun doAnimation5() {
        val tvAnimator1 = ObjectAnimator.ofInt(
            textView, "textColor",
            0xffff00ff.toInt(), 0xffffff00.toInt(), 0xffff00ff.toInt()
        )
        val tvAnimator2 = ObjectAnimator.ofFloat(textView, "translationY", 0f, 300f, 0f)
        val tvAnimator3 = ObjectAnimator.ofFloat(textView, "rotation", 0f, 180f, 0f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(tvAnimator1, tvAnimator2, tvAnimator3)
        animatorSet
        animatorSet.duration = 1000
        animatorSet.start()
    }

    private fun doAnimation7() {
        //window.sharedElementExitTransition = Explode()
        window.exitTransition = Explode()
        val intent = Intent(this, MainActivity2::class.java)
        val transitionActivityOptions =
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair(textView, "translation1"))
        startActivity(intent, transitionActivityOptions.toBundle())
    }

    private fun doAnimation8() {
        val slide = Slide(Gravity.RIGHT)
        slide.duration = 1000
        TransitionManager.beginDelayedTransition(construntLayout)
        textView.visibility = if (textView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun doAnimator9() {
        val viewRect = Rect()
        val explode = Explode()
        explode.epicenterCallback = object : Transition.EpicenterCallback() {
            override fun onGetEpicenter(transition: Transition): Rect {
                return viewRect
            }
        }
        explode.duration = 1000
        TransitionManager.beginDelayedTransition(construntLayout, explode)
        construntLayout.removeAllViews()
    }
}