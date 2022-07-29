package com.example.animation1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity4 : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var statusBar: View
//    lateinit var navigationBar: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        this.title = "自定义Transition"

        recyclerView = findViewById(R.id.recycleView)
        val data = ArrayList<String>(20)
        repeat(5) {
            data.add("但风向是会转变的。终有一天,会吹向更有光亮的方向。从今往后,带着我的祝福,活得更加从容一些吧。")
            data.add("在黎明到来之前，必须有人稍微照亮黑暗。")
            data.add("我们都还没来得及说再见，所以，我不会把它当做离别。")
            data.add("在这个真实得太过分的世界里，每个人都需要仰望星空。")
            data.add("风带来了故事的种子，时间使之发芽。")
        }
        Log.d("TAG", "onCreate: ${data.toString()}")
        recyclerView.adapter = RecyclerViewAdapter(data) { view, msg -> startActivity(view, msg) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        val decor = window.decorView
        decor.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                decor.viewTreeObserver.removeOnPreDrawListener(this)
                statusBar = decor.findViewById<View>(android.R.id.statusBarBackground)
//                navigationBar = findViewById<View>(android.R.id.navigationBarBackground)
                return true
            }
        })
    }

    private fun startActivity(view: View, msg: String) {
        val intent = Intent(this, MainActivity5::class.java)
        intent.putExtra("param1", msg)
        intent.putExtra("param2", view.transitionName)
        val pair1 = Pair.create(view, view.transitionName)
        val pair2 = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)
      //  val pair3 = Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)
        val compat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1, pair2)
        ActivityCompat.startActivity(this, intent, compat.toBundle())
    }
}