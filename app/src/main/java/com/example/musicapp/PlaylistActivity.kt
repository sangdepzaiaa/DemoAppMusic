package com.example.musicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.ActivityPlaylistBinding
import com.example.musicapp.databinding.AddplaylistBinding

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlaylistActivity : AppCompatActivity() {
    companion object{
        lateinit var binding: ActivityPlaylistBinding
        var playlistAdapter : PlaylistAdapter?=null
        lateinit var musicPlaylist : MusicPlaylist
        var musicAdapter:MusicAdapter?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rcvPlaylist.setHasFixedSize(true)
        binding.rcvPlaylist.setItemViewCacheSize(100)
        binding.rcvPlaylist.layoutManager = LinearLayoutManager(this)
        playlistAdapter = PlaylistAdapter(this, musicPlaylist.ref)
        binding.rcvPlaylist.adapter = playlistAdapter
        binding.addPlaylist.setOnClickListener {
            setupAdd()
        }

    }

    private fun setupAdd() {
        val view = LayoutInflater.from(this).inflate(R.layout.addplaylist, binding.root,false)
        val cus = AddplaylistBinding.bind(view)
        val builder = MaterialAlertDialogBuilder(this)
            .setView(view)
            .setTitle("Add Playlist")
            .setPositiveButton("ADD"){dia,_->
                val namepl = cus.playlistname.text
                val creatrBy = cus.name.text
                if (namepl != null && creatrBy != null){
                    if (namepl.isNotEmpty() && creatrBy.isNotEmpty()){
                        addPlaylist(namepl.toString(),creatrBy.toString())
                    }
                }
                dia.dismiss()
            }
        val cust = builder.create()
        cust.show()
        cust.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,R.color.red))
    }

    private fun addPlaylist(namepl: String, creatrBy: String) {
        var playlistexit = false
        for(i in musicPlaylist.ref){
            if (i.name.equals(namepl)){
                playlistexit = true
                break
            }
        }
        if (playlistexit) Toast.makeText(this, "Playlist Exit",Toast.LENGTH_SHORT).show()
        else{
            var templaylist = Playlist()
            templaylist.playlist = ArrayList()
            templaylist.name = namepl
            templaylist.createBy = creatrBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd/mmm/yyyy", Locale.ENGLISH)
            templaylist.createOn = sdf.format(calendar)
            musicPlaylist.ref.add(templaylist)
            playlistAdapter?.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        playlistAdapter?.notifyDataSetChanged()
    }
}