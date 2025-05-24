package com.example.musicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {

    companion object{
        lateinit var binding:ActivityFavouriteBinding
        var favouritesong: ArrayList<Music> = ArrayList()
        var favouriteAdapter: FavouriteAdapter?=null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rcvFavourite.setHasFixedSize(true)
        binding.rcvFavourite.setItemViewCacheSize(100)
        binding.rcvFavourite.layoutManager = LinearLayoutManager(this)
        favouriteAdapter = FavouriteAdapter(this, favouritesong)
        binding.rcvFavourite.adapter = favouriteAdapter
        if (favouritesong.size < 1 ) binding.shuffleBtnF.visibility = View.INVISIBLE
        binding.shuffleBtnF.setOnClickListener {
            val intent = Intent(this,PlayerMusicActivity::class.java)
            intent.putExtra("class","ShuffiFavourite")
            startActivity(intent)
        }
    }
}