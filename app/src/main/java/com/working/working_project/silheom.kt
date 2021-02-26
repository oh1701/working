package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.working.working_project.databinding.ActivitySilheomBinding

class silheom : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivitySilheomBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_silheom)

        var re_array = arrayListOf(recycle_board.board_list("aaaaa"), recycle_board.board_list("1234123124"))

        var board = findViewById<RecyclerView>(R.id.board)

        board.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        board.setHasFixedSize(true)
        board.adapter = recycle_board(re_array)
    }
}