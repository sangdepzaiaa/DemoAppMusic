package com.example.musicapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicapp.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    lateinit var binding:ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
            .getString("current_user", null)


        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

    }
}