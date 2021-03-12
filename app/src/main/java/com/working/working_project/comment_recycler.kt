package com.working.working_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

class comment_recycler(val com_rec:ArrayList<comment>): RecyclerView.Adapter<comment_recycler.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): comment_recycler.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_rycycler, parent,false)
        return comment_recycler.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: comment_recycler.ViewHolder, position: Int) {
        holder.id.text = com_rec.get(position).id.toString()
        holder.comment.text = com_rec.get(position).comment.toString()
    }

    override fun getItemCount(): Int {
        return com_rec.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var id = itemView.findViewById<TextView>(R.id.comment_id)
        var comment = itemView.findViewById<TextView>(R.id.comment_comment)
    }
}
class comment(val id:String, val comment:String)