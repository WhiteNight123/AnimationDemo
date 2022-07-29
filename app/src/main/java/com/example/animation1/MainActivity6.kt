package com.example.animation1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity6 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        this.title = "MaterialContainerTransform"
//        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback());
//        window.sharedElementsUseOverlay = false;
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)
        val fragment3 = Fragment3()
        supportFragmentManager.beginTransaction().replace(R.id.constraintLayout4, fragment3)
            .commit()
    }
}