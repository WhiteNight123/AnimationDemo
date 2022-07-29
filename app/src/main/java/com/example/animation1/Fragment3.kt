package com.example.animation1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment3.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment3 : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var floatActionBar1: FloatingActionButton
    lateinit var floatActionBar2: FloatingActionButton
    lateinit var floatActionBar3: FloatingActionButton


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_3, container, false)
        floatActionBar1 = view.findViewById(R.id.floatingActionBar1)
        floatActionBar2 = view.findViewById(R.id.floatingActionBar2)
        floatActionBar3 = view.findViewById(R.id.floatingActionBar3)


        recyclerView = view.findViewById(R.id.recycleView2)
        val data = ArrayList<String>(20)
        repeat(5) {
            data.add("但风向是会转变的。终有一天,会吹向更有光亮的方向。从今往后,带着我的祝福,活得更加从容一些吧。")
            data.add("在黎明到来之前，必须有人稍微照亮黑暗。")
            data.add("我们都还没来得及说再见，所以，我不会把它当做离别。")
            data.add("在这个真实得太过分的世界里，每个人都需要仰望星空。")
            data.add("风带来了故事的种子，时间使之发芽。")
        }
        Log.d("TAG", "onCreate: ${data.toString()}")
        recyclerView.layoutAnimation = // 入场动画
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.recycler_view_fade_in
                )
            )
        recyclerView.adapter = RecyclerViewAdapter(data) { view1, msg ->
            startActivity(
                view1,
                msg
            )
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        );
        floatActionBar1.setOnClickListener {
            exitTransition = MaterialElevationScale(false).apply {
                duration = 300
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = 300
            }
            val fragment5 = Fragment5()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.constraintLayout4, fragment5).addToBackStack(null).commit()
        }

        floatActionBar2.setOnClickListener {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
                duration = 300
            }
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
                duration = 300
            }
            val fragment6 = Fragment6()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.constraintLayout4, fragment6).addToBackStack(null).commit()
        }

        floatActionBar3.setOnClickListener {
            exitTransition = MaterialFadeThrough().apply {
                duration = 300
            }
            reenterTransition = MaterialFadeThrough().apply {
                duration = 300
            }
            val fragment7 = Fragment7()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.constraintLayout4, fragment7).addToBackStack(null).commit()
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun startActivity(view: View, msg: String) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = 300
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 300
        }
        val fragment4 = Fragment4()
        //fragment4.sharedElementEnterTransition = MaterialContainerTransform()
        val bundle = Bundle()
        bundle.putString("param1", msg)
        bundle.putString("param2", view.transitionName)
        fragment4.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .addSharedElement(view, view.transitionName)
            .replace(R.id.constraintLayout4, fragment4).addToBackStack(null).commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment3.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}