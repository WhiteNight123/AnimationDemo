package com.example.animation1

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.animation1.utils.themeColor
import com.google.android.material.card.MaterialCardView
import com.google.android.material.transition.MaterialContainerTransform

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment5.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment5 : Fragment() {
    lateinit var materialCardView1: MaterialCardView
    lateinit var constraintLayout: ConstraintLayout
    lateinit var materialCardView2: MaterialCardView
    lateinit var scrimView: View
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var textView3: TextView
    lateinit var textView4: TextView

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_5, container, false)
        materialCardView1 = view.findViewById(R.id.materialCardView2)
        materialCardView2 = view.findViewById(R.id.materialCardView3)
        constraintLayout = view.findViewById(R.id.constraintLayout5)
        scrimView = view.findViewById(R.id.card_scrim)
        textView1 = view.findViewById(R.id.tv_yuanshen)
        textView2 = view.findViewById(R.id.wangzhe)
        textView3 = view.findViewById(R.id.tv_yuanshen2)
        textView4 = view.findViewById(R.id.huangyeluandou)
        materialCardView1.setOnClickListener {
            expandChip(it)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.floatingActionBar1)
            endView = requireView().findViewById(R.id.materialCardView1)
            duration = 300
            scrimColor = Color.TRANSPARENT
            containerColor =
                requireContext().themeColor(com.google.android.material.R.attr.colorSurface)
            startContainerColor =
                requireContext().themeColor(com.google.android.material.R.attr.colorSecondary)
            endContainerColor =
                requireContext().themeColor(com.google.android.material.R.attr.colorSurface)
        }
        returnTransition = Slide().apply {
            duration = 300
            addTarget(R.id.materialCardView1)
        }
    }

    private fun expandChip(chip: View) {
        //materialCardView2.setOnClickListener { collapseChip(chip) }
        textView2.setOnClickListener {
            collapseChip(chip, textView2.text as String?)
        }
        textView3.setOnClickListener {
            collapseChip(chip, textView3.text as String?)
        }
        textView4.setOnClickListener {
            collapseChip(chip, textView4.text as String?)
        }
        scrimView.visibility = View.VISIBLE
        scrimView.setOnClickListener { collapseChip(chip) }
        closeRecipientCardOnBackPressed.isEnabled = true
        closeRecipientCardOnBackPressed.expandedChip = chip

        val transform = MaterialContainerTransform().apply {
            startView = chip
            endView = materialCardView2
            scrimColor = Color.TRANSPARENT
            endElevation = 3f
            addTarget(materialCardView2)
        }
        TransitionManager.beginDelayedTransition(constraintLayout, transform)
        materialCardView2.visibility = View.VISIBLE
        chip.visibility = View.INVISIBLE
    }

    private fun collapseChip(chip: View, string: String? = null) {
        scrimView.visibility = View.GONE
        closeRecipientCardOnBackPressed.expandedChip = null
        closeRecipientCardOnBackPressed.isEnabled = false
        if (string != null) {
            textView1.text = string
        }
        val transform = MaterialContainerTransform().apply {
            startView = materialCardView2
            endView = chip
            scrimColor = Color.TRANSPARENT
            startElevation = 3f
            addTarget(chip)
        }
        TransitionManager.beginDelayedTransition(constraintLayout, transform)
        chip.visibility = View.VISIBLE
        materialCardView2.visibility = View.INVISIBLE
    }

    private val closeRecipientCardOnBackPressed = object : OnBackPressedCallback(false) {
        var expandedChip: View? = null
        override fun handleOnBackPressed() {
            expandedChip?.let { collapseChip(it) }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment5.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment5().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}