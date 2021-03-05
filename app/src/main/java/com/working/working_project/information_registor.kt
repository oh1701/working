package com.working.working_project

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.working.working_project.databinding.ActivityInformationRegistorBinding

class information_registor : AppCompatActivity() {

    lateinit var binding: ActivityInformationRegistorBinding

    /*var user: FirebaseUser? = null
    lateinit var get_information: DatabaseReference

    private var key_list = arrayOfNulls<String>(99)
    private var value_list = arrayOfNulls<String>(99)
    private var infor_checkd = 0*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationRegistorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /*user = FirebaseAuth.getInstance().currentUser
        val username = user!!.email.toString().split("@")
        get_information =
            FirebaseDatabase.getInstance().getReference("member").child("${username[0]}")*/


    }
}