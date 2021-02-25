package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.working.working_project.databinding.ActivityMyWriteBoardBinding

class my_write_board : Fragment() {

    lateinit var binding:ActivityMyWriteBoardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityMyWriteBoardBinding.inflate(layoutInflater, container, false)



        return binding.root
    }

    override fun onResume() {


        super.onResume()
    }
}