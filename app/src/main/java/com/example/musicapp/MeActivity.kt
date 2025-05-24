package com.example.musicapp

import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.musicapp.databinding.ActivityMeBinding

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class MeActivity:AppCompatActivity() {
    lateinit var binding: ActivityMeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnFavourite.setOnClickListener {
            startActivity(Intent(this,FavouriteActivity::class.java))
        }
        binding.btnPlaylist.setOnClickListener {
            startActivity(Intent(this,PlaylistActivity::class.java))
        }
        binding.btnlogout.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
                .setTitle("Do you want log out")
                .setPositiveButton("yes"){dia,_->
                    exitApplication2()
                    // XÓA thông tin user đã login
                    getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
                        .edit()
                        .remove("current_user")
                        .apply()

                    // Mở lại LoginActivity, xoá luôn backstack
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    dia.dismiss()
                }
                .setNegativeButton("no"){dia,_->
                    dia.dismiss()
                }
            val cus = builder.create()
            cus.show()
            cus.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,R.color.red))
            cus.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this,R.color.red))
        }
    }
}