package com.example.voicesearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicesearch.R


/**
 * Created by Naveen Verma on 19/3/21.
 * To The New
 * naveen.verma@tothenew.com
 */

internal class TextAdapter(private var titleList: List<String>) : RecyclerView.Adapter<TextAdapter.MyViewHolder>() {

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.tv_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_text, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = titleList[position]
    }

    override fun getItemCount(): Int {
        return titleList.size
    }

    fun setList(data: String) {
//        (this.titleList as ArrayList<String>).add(data)
        (this.titleList as ArrayList<String>).reverse()
        notifyDataSetChanged()
    }
}