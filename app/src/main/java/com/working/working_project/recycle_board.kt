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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.get_board_recycler, parent, false)
        return recycle_board.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: recycle_board.ViewHolder, position: Int) {
        holder.title.text = board.get(position).title
        holder.id.text = board.get(position).id
        Log.d("holder_board_title", "${holder.title}")
    }

    override fun getItemCount(): Int {
        Log.d("확인3", "${board.size}")
        return board.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var title:TextView = itemView.findViewById(R.id.recycler_text)
        var id:TextView = itemView.findViewById(R.id.recycler_id)
    }

}
class board_list(val title:String, val id:String){
}
