package com.example.musicapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.logging.Handler

class MusicService: Service() {
    var mediaPlayer: MediaPlayer? = null
    var myBinder = MyBinder()
    override fun onCreate() {
        super.onCreate()

    }
    inner class MyBinder() : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Chỉ cần gọi showNotification ở đây nếu bạn muốn hiển thị notification ngay khi start
        // showNotification(R.drawable.baseline_pause_24)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    fun showNotification(btnPausePlay: Int) {
        val intent = Intent(baseContext, MainActivity::class.java)
        var flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)


        val prevIntent =
            Intent(this, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val pendingIntentPrev = PendingIntent.getBroadcast(this, 1, prevIntent, flag)

        val playIntent =
            Intent(this, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val pendingIntentPlay = PendingIntent.getBroadcast(this, 2, playIntent, flag)

        val nextIntent =
            Intent(this, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val pendingIntentNext = PendingIntent.getBroadcast(this, 3, nextIntent, flag)

        val exitIntent =
            Intent(this, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val pendingIntentExit = PendingIntent.getBroadcast(this, 4, exitIntent, flag)



        val imgrv = getArtImg(this, PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].path)
        val imgArt = if (imgrv != null) {
            BitmapFactory.decodeByteArray(imgrv, 0, imgrv.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.nct)
        }


        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].title)
            .setContentText(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music)
            .setLargeIcon(imgArt)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.VISIBILITY_PUBLIC)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1)
            )
            .addAction(R.drawable.previous, "previous", pendingIntentPrev)
            .addAction(btnPausePlay, "play", pendingIntentPlay)
            .addAction(R.drawable.next, "next", pendingIntentNext)
            .addAction(R.drawable.exit, "exit", pendingIntentExit)
            .build()
        startForeground(13, notification)

    }

    fun createMediaPlayer2() {
        try {
//            // Hiện loading animation, ẩn play/pause
//            binding.btnplayorpausePA.visibility = View.GONE
//            binding.loadingAnim.visibility = View.VISIBLE
//            binding.loadingAnim.playAnimation()
            val selectedMusic = PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition]
            val afd = assets.openFd(selectedMusic.path)
            if (PlayerMusicActivity.musicService?.mediaPlayer == null) PlayerMusicActivity.musicService?.mediaPlayer = MediaPlayer()
            PlayerMusicActivity.musicService?.mediaPlayer?.reset()
            PlayerMusicActivity.musicService?.mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            PlayerMusicActivity.musicService?.mediaPlayer?.prepare()




//            musicService?.mediaPlayer?.setOnPreparedListener {
//                it.start()

            // Khi đã load xong: ẩn loading, hiện nút play
//                Log.e("Player", "Animation done - hiển thị play/pause")
//                binding.loadingAnim.cancelAnimation()
//                binding.loadingAnim.visibility = View.GONE
//                binding.btnplayorpausePA.visibility = View.VISIBLE
            PlayerMusicActivity.isPlaying = true
            PlayerMusicActivity.binding.btnplayorpausePA.setImageResource(R.drawable.baseline_pause_24)

//                // Gọi UI update
//                runOnUiThread {
//                    setLayout()
//                    setupSeekbar()
//                }
// Gửi broadcast để Activity/Fragment cập nhật UI

            PlayerMusicActivity.musicService?.showNotification(R.drawable.baseline_pause_24)

            PlayerMusicActivity.binding.seekbarStart.text = formatSongDuration(PlayerMusicActivity.musicService?.mediaPlayer?.currentPosition!!.toLong())
            PlayerMusicActivity.binding.seekbarEnd.text = formatSongDuration(PlayerMusicActivity.musicService?.mediaPlayer?.duration!!.toLong())
            PlayerMusicActivity.binding.seekbar.progress = 0
            PlayerMusicActivity.binding.seekbar.max = PlayerMusicActivity.musicService?.mediaPlayer?.duration!!
            PlayerMusicActivity.nowPlaying = PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].id



        } catch (e: Exception) {
            Log.e("createMediaPlayer", "Error: ${e.message}")
        }
    }








//    fun createMediaPlayer3() {
//        mediaPlayer?.reset()
//        val song = PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition]
//        mediaPlayer?.setDataSource(song.path)
//        mediaPlayer?.prepare()
//    }
}


