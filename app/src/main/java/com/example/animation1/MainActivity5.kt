package com.example.animation1

import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "自定义Transition"
        setContentView(R.layout.activity_main5)
        val textView = findViewById<TextView>(R.id.textView5)
        textView.transitionName = intent.getStringExtra("param2")
        textView.text = intent.getStringExtra("param1")
        postponeEnterTransition()
        val decorView = window.decorView
        window.decorView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                decorView.viewTreeObserver.removeOnPreDrawListener(this)
                supportStartPostponedEnterTransition()
                return true
            }
        })
        val transition = MyTransition()
        transition.addTarget("transition2")

    }
}