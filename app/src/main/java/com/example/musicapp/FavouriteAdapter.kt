package com.example.musicapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp.databinding.ActivityFavouriteBinding

import com.example.musicapp.databinding.FavouriteViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class FavouriteAdapter(var context: Context,var musicFavoueite:ArrayList<Music>):RecyclerView.Adapter<FavouriteAdapter.myHolder>(){
    class myHolder(binding: FavouriteViewBinding):RecyclerView.ViewHolder(binding.root) {
      val name = binding.txvFa
        val image  =binding.imgfa
        val root = binding.root
        val delete = binding.btndeletefa
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        return myHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return musicFavoueite.size
    }

    override fun onBindViewHolder(holder: myHolder, position: Int) {
        val music = musicFavoueite[position]
        holder.name.apply {
            text = music.title
            isSelected = true // ⚠️ Quan trọng: cần để marquee chạy
        }

        Glide.with(context).load(musicFavoueite[position].albumArtUri).apply(
            RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
        ).into(holder.image)
        holder.root.setOnClickListener {
            val intent = Intent(context,PlayerMusicActivity::class.java)
            intent.putExtra("class","FavouriteAdapter")
            ContextCompat.startActivity(context,intent,null)
        }
        holder.delete.setOnClickListener {
            val cus = MaterialAlertDialogBuilder(context)
                .setTitle("Delete Playlist")
                .setPositiveButton("yes"){dia,_->
                    FavouriteActivity.favouritesong.removeAt(position)
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
    }
    fun refreshPlaylist() {
        musicFavoueite = ArrayList()
        musicFavoueite.addAll(FavouriteActivity.favouritesong)
        notifyDataSetChanged()
    }


}
