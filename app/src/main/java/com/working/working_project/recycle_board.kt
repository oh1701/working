package com.working.working_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recycle_board(val board: ArrayList<board_list>):RecyclerView.Adapter<recycle_board.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recycle_board.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_community, parent, true)
    }

    override fun onBindViewHolder(holder: recycle_board.ViewHolder, position: Int) {
        holder.title.text = board.get(position).title
    }

    override fun getItemCount(): Int {
        return board.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.recycler_text)
    }

    class board_list(val title:String){
    }
}