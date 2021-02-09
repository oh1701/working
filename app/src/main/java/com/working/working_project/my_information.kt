
package com.working.working_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.working.working_project.databinding.ActivityMainBinding
import com.working.working_project.databinding.ActivityMyInformationBinding

class my_information : Fragment(), inter_run_information {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ActivityMyInformationBinding.inflate(layoutInflater, container, false)

        binding.basicImage.setBackgroundColor(0)
        Glide.with(this).load(R.drawable.naver_icon).apply(RequestOptions.circleCropTransform()).into(binding.basicImage)
        // 글라이드를 이용, apply의 requestoption 중 circlecroptransform을 사용하여 이미지를 원형으로 자른다.


        return binding.root
    }
}