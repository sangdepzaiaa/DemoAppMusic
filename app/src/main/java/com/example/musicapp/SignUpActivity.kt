package com.example.musicapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.musicapp.databinding.ActivitySignUpBinding

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SignUpActivity : AppCompatActivity() {
    lateinit var binding:ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            register()
        }
    }

    fun register() {
        val sharedPref = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val gson = Gson()

        val username = binding.edtuser.text.toString()
        val password = binding.edtpass.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            // Lấy danh sách tài khoản hiện tại
            val json = sharedPref.getString("user_list", null)
            val type = object : TypeToken<MutableList<User>>() {}.type
            val userList: MutableList<User> = if (json != null) gson.fromJson(json, type) else mutableListOf()

            // Kiểm tra trùng tài khoản
            if (userList.any { it.username == username }) {
                Toast.makeText(this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show()
            } else {
                // Thêm tài khoản mới
                userList.add(User(username, password))
                val updatedJson = gson.toJson(userList)

                with(sharedPref.edit()) {
                    putString("user_list", updatedJson)
                    apply()
                }

                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show()
        }

    }


}