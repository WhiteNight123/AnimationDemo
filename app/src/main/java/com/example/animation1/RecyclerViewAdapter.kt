package com.example.animation1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val dataSet: ArrayList<String>, private val onClick: (view:View,msg:String) -> Unit):
    RecyclerView.Adapter<RecyclerViewAdapter.InnerHolder>() {
    inner class InnerHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView:TextView
        init {
            textView = view.findViewById(R.id.rv_tv)
            textView.setOnClickListener {
                onClick(textView, (textView.text as String) .repeat(10))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item1,parent, false)
        return InnerHolder(view)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.textView.text = dataSet[position]
        holder.textView.transitionName = "transition$position"
    }

    override fun getItemCount(): Int =dataSet.size
}