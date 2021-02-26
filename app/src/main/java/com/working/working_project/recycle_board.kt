package com.working.working_project

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recycle_board(val board: ArrayList<board_list>):RecyclerView.Adapter<recycle_board.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recycle_board.ViewHolder {
        Log.d("확인", "확인1")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_silheom, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: recycle_board.ViewHolder, position: Int) {
        Log.d("board", "${board[0].title}")
        Log.d("board", "${board[1].title}")

        holder.title.text = board[position].title.toString()
    }

    override fun getItemCount(): Int {
        Log.d("확인3", "${board.size}")
        return board.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var title = itemView.findViewById<TextView>(R.id.recycler_text)
    }

    class board_list(val title:String){
    }
}