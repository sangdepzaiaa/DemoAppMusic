package com.example.musicapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CommentManager {
    private const val PREF_NAME = "comments"
    private val gson = Gson()
//lấy ra danh sách hiện tại
    fun getComments(context: Context, songId: Int): MutableList<Comment> {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = sharedPref.getString("song_$songId", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Comment>>() {}.type
            gson.fromJson(json, type)
        } else mutableListOf()
    }
// thêm vào danh sách
    fun addComment(context: Context, comment: Comment) {
        val list = getComments(context, comment.songId)
        list.add(comment)
        saveComments(context, comment.songId, list)
    }
// lưu vào dánh sách
    private fun saveComments(context: Context, songId: Int, comments: List<Comment>) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val json = gson.toJson(comments)
        editor.putString("song_$songId", json)
        editor.apply()
    }
}
