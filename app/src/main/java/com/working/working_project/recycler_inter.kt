package com.working.working_project

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.working.working_project.databinding.ActivityCommunityBinding

interface recycler_inter {
    fun turn_content(position:Int){
        lateinit var binding: ActivityCommunityBinding
        lateinit var firebaseDatabase: DatabaseReference// 파이어베이스 실시간 db 관리 객체 얻어오기(Root 가져옴.) ()는 최상위 값.
        var user: FirebaseUser? = null
        lateinit var board_data: DatabaseReference
        lateinit var all_board: DatabaseReference


        return
    }
}