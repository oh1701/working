package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.working.working_project.databinding.ActivityAlarmactivityBinding
import java.time.LocalDateTime

class alarmactivity : AppCompatActivity() {
    lateinit var binding:ActivityAlarmactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.alarmText.text = "현재 시각은 : " + LocalDateTime.now().toString()
    }
}