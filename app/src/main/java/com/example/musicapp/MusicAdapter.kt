package com.example.musicapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.databinding.MusicViewBinding

class  MusicAdapter(  var context: Context,var musiclist: ArrayList<Music>,var playlistdetails : Boolean =false,var
selectionActivity: Boolean = false)
    :RecyclerView.Adapter<MusicAdapter.myHolder>(){
    class myHolder(binding: MusicViewBinding) :RecyclerView.ViewHolder(binding.root) {
        val name = binding.txvnamesongs
        // val duration = binding.txvdurationsongs
        val image = binding.imgsongs
        val artist = binding.txvSongalbumMV
        val root = binding.root

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.myHolder {
        return myHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.myHolder, position: Int) {
        val music = musiclist[position]
        holder.name.text = music.title
        holder.artist.text = music.artist
        // holder.duration.text = formatSongDuration(music.duration).toString()
        Glide.with(context).load(music.albumArtUri).apply(
            RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
        ).into(holder.image)

        holder.root.setOnClickListener {
            when{
                selectionActivity->{
                    if (checkPlaylist(musiclist[position])) holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.red))
                    else holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.black))
                }
                playlistdetails ->  sendIntent(pos = position,ref = "playlistdetails")
                MainActivity.search -> sendIntent(pos = position,ref = "musicSearch")
                musiclist[position].id == PlayerMusicActivity.nowPlaying ->
                    sendIntent(pos = PlayerMusicActivity.songPosition, ref = "Playing")
                else->{ sendIntent(pos = position,ref = "musicAdapter")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }
    fun updateSearch(searchlist: ArrayList<Music>){
        musiclist = ArrayList()
        musiclist.addAll(searchlist)
        notifyDataSetChanged()
    }

    fun updateSearch2(){
        musiclist = ArrayList()
        musiclist = PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentMusicPos].playlist
        notifyDataSetChanged()
    }
    private fun sendIntent(pos: Int, ref: String){
        val intent = Intent(context,PlayerMusicActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)

        context.startActivity(intent)
    }




}
