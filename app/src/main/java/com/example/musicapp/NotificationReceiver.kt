package com.example.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.graphics.createBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import kotlin.system.exitProcess

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> prevNextnotification(
                context = context!!,
                increment = false
            )

            ApplicationClass.PLAY -> if (PlayerMusicActivity.isPlaying) pausemusic() else playmusic()
            ApplicationClass.NEXT -> prevNextnotification(context = context!!, increment = true)
            ApplicationClass.EXIT -> {
                exitApplication()
            }
        }
    }

    private fun prevNextnotification(context: Context, increment: Boolean) {
        setSongPosition(increment = increment)
        PlayerMusicActivity.musicService?.createMediaPlayer2()
        Glide.with(context)
            .load(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].albumArtUri)
            .apply(
                RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
            ).into(PlayerMusicActivity.binding.imgsongpositionPA)
        PlayerMusicActivity.binding.txvsongnamePA.text =
            PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].title

        Glide.with(context)
            .load(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].albumArtUri)
            .apply(
                RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
            ).into(PlayerMusicActivity.binding.imgBlurred)

        Glide.with(context)
            .load(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].albumArtUri)
            .apply(
                RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
            ).into(AddPlayingFragment.binding.imgfrag)
        AddPlayingFragment.binding.txvfrag.text =
            PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].title
        playmusic()

        PlayerMusicActivity.findex =
            checkFavourite(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].id)
        if (PlayerMusicActivity.isFavourite) PlayerMusicActivity.binding.btnFavouritePA.setImageResource(
            R.drawable.baseline_favorite_24
        )
        else PlayerMusicActivity.binding.btnFavouritePA.setImageResource(R.drawable.baseline_favorite_border_24)
    }

    private fun playmusic() {
        PlayerMusicActivity.isPlaying = true
        PlayerMusicActivity.musicService?.mediaPlayer?.start()
        PlayerMusicActivity.binding.btnplayorpausePA.setImageResource(R.drawable.baseline_pause_24)
        PlayerMusicActivity.musicService?.showNotification(R.drawable.baseline_pause_24)
        AddPlayingFragment.binding.btnplaypuaefrag.setImageResource(R.drawable.baseline_pause_24)
    }

    private fun pausemusic() {
        PlayerMusicActivity.isPlaying = false
        PlayerMusicActivity.musicService?.mediaPlayer?.pause()
        PlayerMusicActivity.binding.btnplayorpausePA.setImageResource(R.drawable.play)
        PlayerMusicActivity.musicService?.showNotification(R.drawable.play)
        AddPlayingFragment.binding.btnplaypuaefrag.setImageResource(R.drawable.play)
    }

}