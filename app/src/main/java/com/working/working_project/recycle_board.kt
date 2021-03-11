package com.working.working_project

import android.os.Bundle
import android.text.method.KeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceControl
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentViewHolder

var d = 0
lateinit var select_title:String
lateinit var select_content:String
lateinit var full_title:String

class recycle_board(val board: ArrayList<board_list>, var frag_m:FragmentManager, val mutable_re:MutableList<String>, val mutable_content:MutableList<String>)
    : RecyclerView.Adapter<recycle_board.ViewHolder>() { //프래그먼트매니저를 인자로 받아서 클릭 이벤트로 활용

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recycle_board.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.get_board_recycler, parent, false)

        return recycle_board.ViewHolder(view).apply{
            title.setOnClickListener {
                var cursor = adapterPosition
                for(i in board.indices){
                    if(i == cursor){ // 아래의 뷰일수록 i는 커진다.
                        Log.d("확인", cursor.toString())
                        Log.d("확인", mutable_re.size.toString())
                        Log.d("확인", board.size.toString())

                        d = board.size - 1 - i

                        val string_arr = mutable_re[d].split(") ")

                        full_title = mutable_re[d]
                        select_title = string_arr[1].toString()
                        select_content = mutable_content[d]
                        //turn_content(d - i, d, mutable_re[d], mutable_content[d])
                        frag_m.beginTransaction().replace(R.id.main_frame, select_board()).commit()
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
