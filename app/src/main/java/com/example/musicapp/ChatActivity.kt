package com.example.musicapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.ActivityChatBinding
import com.example.musicapp.databinding.ChatViewBinding

import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChatActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatBinding
    lateinit var commentAdapter: CommentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadComments()


        binding.btnSendComment.setOnClickListener {
            val text = binding.edtComment.text.toString().trim()
            if (text.isNotEmpty()) {

                val username = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
                    .getString("current_user", "Guest") ?: "Guest"

                val comment = Comment(PlayerMusicActivity.songPosition, username, text)
                CommentManager.addComment(this, comment)
                binding.edtComment.text.clear()
                loadComments()
            }
        }

    }

    private fun loadComments() {
        val comments = CommentManager.getComments(this, PlayerMusicActivity.songPosition)
        commentAdapter = CommentAdapter(comments)
        binding.recyclerComments.adapter = commentAdapter
        binding.recyclerComments.layoutManager = LinearLayoutManager(this)
    }


}