package com.example.animation1

import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import androidx.appcompat.app.AppCompatActivity

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        val fragment1 = Fragment1()

        fragment1.sharedElementEnterTransition = Fade()
        window.exitTransition = Slide()
        fragment1.sharedElementEnterTransition = Slide()
        fragment1.sharedElementReturnTransition = Fade()
        supportFragmentManager.beginTransaction().replace(R.id.constraintLayout3, fragment1)
            .commit()
    }
}