package com.working.working_project

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

interface inter_run_information {
    fun member_information(move:String, date:String){
        var move_walk:Double = 0.0
        var move_object = 0.0

        move_walk = move_walk + move.toDouble()
    }
}