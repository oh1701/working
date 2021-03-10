package com.working.working_project

import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceControl
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentViewHolder

class recycle_board(val board: ArrayList<board_list>, val dd:FragmentManager):RecyclerView.Adapter<recycle_board.ViewHolder>(), recycler_inter { //프래그먼트매니저를 인자로 받아서 클릭 이벤트로 활용

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recycle_board.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.get_board_recycler, parent, false)

        return recycle_board.ViewHolder(view).apply{
            title.setOnClickListener {
                var cursor = adapterPosition
                for(i in board.indices){
                    if(i == cursor){
                        Log.d("확인", cursor.toString())
                        /*dd.beginTransaction().replace(R.id.community_frag, my_board()).commit()
                        var inter_title = board.get(position).title*/
                        turn_content(i)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: recycle_board.ViewHolder, position: Int) {
        holder.title.text = board.get(position).title
        holder.id.text = board.get(position).id
        Log.d("holder_board_title", "${holder.title}")

    }

    override fun getItemCount(): Int {
        return board.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var title:TextView = itemView.findViewById(R.id.recycler_text)
        var id:TextView = itemView.findViewById(R.id.recycler_id)
    }

}
class board_list(val title:String, val id:String){
}
