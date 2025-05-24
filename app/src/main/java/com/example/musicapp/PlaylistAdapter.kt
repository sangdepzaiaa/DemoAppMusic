package com.example.musicapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp.databinding.FavouriteViewBinding
import com.example.musicapp.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistAdapter(var context: Context,var musiclist:ArrayList<Playlist>)
    :RecyclerView.Adapter<PlaylistAdapter.myHolder>(){
    class myHolder(binding: PlaylistViewBinding):RecyclerView.ViewHolder(binding.root) {
        val name = binding.txvpl
        val delete = binding.btndeletepl
        val image = binding.imgplaylist
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        return myHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }

    override fun onBindViewHolder(holder: myHolder, position: Int) {
        val music = musiclist[position]
        holder.name.text = music.name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val cus = MaterialAlertDialogBuilder(context)
                .setTitle("Delete Playlist")
                .setPositiveButton("yes"){dia,_->
                    PlaylistActivity.musicPlaylist?.ref?.removeAt(position)
                    refreshPlaylist()
                    dia.dismiss()
                }
                .setNegativeButton("no"){dia,_->
                    dia.dismiss()

                }
            val builder = cus.create()
            builder.show()
            builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context,R.color.red))
            builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context,R.color.red))
        }
        holder.root.setOnClickListener {
            val intent = Intent(context,PlaylistDetailsActivity::class.java)
            intent.putExtra("class",position)
            ContextCompat.startActivity(context,intent,null)
        }
        if (PlaylistActivity.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context).load(PlaylistActivity.musicPlaylist.ref[position].playlist[0].albumArtUri).apply(
                RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
            ).into(holder.image)
        }
    }

    fun refreshPlaylist() {
        musiclist = ArrayList()
        musiclist.addAll(PlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }


}