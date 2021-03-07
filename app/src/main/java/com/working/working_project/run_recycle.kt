package com.working.working_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class run_recycle(val runRecycle: ArrayList<run_recycle_list>) : RecyclerView.Adapter<run_recycle.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): run_recycle.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.run_recycle, parent, false)
        return run_recycle.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: run_recycle.ViewHolder, position: Int) {
        holder.date.text = runRecycle.get(position).date
        holder.run.text = runRecycle.get(position).run
    }

    override fun getItemCount(): Int {
        return runRecycle.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var date = itemView.findViewById<TextView>(R.id.date)
        var run = itemView.findViewById<TextView>(R.id.run_record1)
    }

}
class run_recycle_list(val date:String, val run:String){
}