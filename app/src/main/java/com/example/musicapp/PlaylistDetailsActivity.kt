package com.example.musicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.R
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetailsActivity : AppCompatActivity() {
    companion object{
        var currentMusicPos:Int = -1
        lateinit var binding: ActivityPlaylistDetailsBinding
        var musicAdapter:MusicAdapter?=null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentMusicPos = intent.extras?.get("class") as Int
        binding.rcvPlaylist.setHasFixedSize(true)
        binding.rcvPlaylist.setItemViewCacheSize(100)
        binding.rcvPlaylist.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this,PlaylistActivity.musicPlaylist.ref[currentMusicPos].playlist,playlistdetails = true)
        binding.rcvPlaylist.adapter = musicAdapter
        binding.btndeletePD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
                .setTitle("Do you want delete all music")
                .setPositiveButton("yes"){dia,_->
                    PlaylistActivity.musicPlaylist.ref[currentMusicPos].playlist.clear()
                    musicAdapter?.updateSearch2()
                    dia.dismiss()
                }
                .setNegativeButton("no"){dia,_->
                    dia.dismiss()
                }
            val cus = builder.create()
            cus.show()
            cus.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,com.example.musicapp.R.color.red))
            cus.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this,com.example.musicapp.R.color.red))
        }
        binding.btnAddPD.setOnClickListener {
            startActivity(Intent(this,SelectionActivity::class.java))
        }
        binding.shuffiPD.setOnClickListener {
            val intent = Intent(this,PlayerMusicActivity::class.java)
            intent.putExtra("class","shuffiPD")
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        binding.txvPlaylistnamePD.text = "Total Song: ${PlaylistActivity.musicPlaylist.ref[currentMusicPos].playlist.size} \n\n"
        binding.txvinfoPD.text = "createBy: ${PlaylistActivity.musicPlaylist.ref[currentMusicPos].createBy}\n\n"+
                "createOn: ${PlaylistActivity.musicPlaylist.ref[currentMusicPos].createOn} \n\n"
        if (PlaylistActivity.musicPlaylist.ref[currentMusicPos].playlist.size >0){
            binding.shuffiPD.visibility = View.VISIBLE
            Glide.with(this).load(PlaylistActivity.musicPlaylist.ref[currentMusicPos].playlist[0].albumArtUri).apply(
                RequestOptions.placeholderOf(com.example.musicapp.R.drawable.nct).centerCrop()
            ).into(binding.imgPD)
        }
        musicAdapter?.notifyDataSetChanged()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()

        val jsonStringpl = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("plsong",jsonStringpl)
        editor.apply()
    }



}