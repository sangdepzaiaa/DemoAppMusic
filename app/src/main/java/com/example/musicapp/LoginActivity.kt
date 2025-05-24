package com.example.musicapp

import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.musicapp.databinding.ActivityLoginBinding

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
            .getString("current_user", null)

        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }



        binding.txvLogin.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val inputUser = binding.edtuser.text.toString()
            val inputPass = binding.edtpass.text.toString()

            val sharedPref = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
            val json = sharedPref.getString("user_list", null)
            val gson = Gson()

            val type = object : TypeToken<List<User>>() {}.type
            val userList: List<User> = if (json != null) gson.fromJson(json, type) else emptyList()

            val isValidUser = userList.any { it.username == inputUser && it.password == inputPass }

            if (isValidUser) {
                // Có thể lưu user đang đăng nhập để dùng trong phiên hiện tại
                with(sharedPref.edit()) {
                    putString("current_user", inputUser)
                    apply()
                }

                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show()
            }

        }

        binding.txvLogin.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


}