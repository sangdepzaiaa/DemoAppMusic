package com.example.musicapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp.MainActivity.Companion.binding
import com.example.musicapp.databinding.MusicViewBinding


class MusicAdapter2(var context: Context,var musiclist: ArrayList<Music>) : RecyclerView.Adapter<MusicAdapter2.MusicViewHolder>() {
    inner class MusicViewHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.txvnamesongs
        // val duration = binding.txvdurationsongs
        val image = binding.imgsongs
        val artist = binding.txvSongalbumMV
        val root = binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(MusicViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val song = musiclist[position]
        holder.name.text = song.title
        holder.artist.text = song.artist
        Glide.with(context).load(song.albumArtUri).apply(
            RequestOptions.placeholderOf(R.drawable.nct).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener {
            sendIntent2(pos = position, ref = "musicAdapterr")
        }
    }

    override fun getItemCount(): Int = musiclist.size
    private fun sendIntent2(pos: Int, ref: String){
        val intent2 = Intent(context,PlayerMusicActivity::class.java)
        intent2.putExtra("indexx",pos)
        intent2.putExtra("classs",ref)
        context.startActivity(intent2)
    }


}