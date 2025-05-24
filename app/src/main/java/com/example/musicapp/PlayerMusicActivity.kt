package com.example.musicapp

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.databinding.ActivityPlayerMusicBinding
import com.example.musicapp.databinding.FavouriteViewBinding
import com.example.musicapp.databinding.SheetdialogtimerBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.lang.Thread.sleep


class PlayerMusicActivity : AppCompatActivity(),ServiceConnection,MediaPlayer.OnCompletionListener{
    companion object{
        var musiclistPA= ArrayList<Music>()
        var songPosition:Int = -1
        lateinit var binding: ActivityPlayerMusicBinding
        var isPlaying:Boolean = false
        var musicService : MusicService?=null
        var repeat:Boolean = false
        var nowPlaying: String = ""
        var isFavourite: Boolean =false
        var findex:Int = -1

    }
    var min10:Boolean = false
    var min60:Boolean = false
    var min120:Boolean = false
    lateinit var runnable:Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initlizerLayout()
        binding.imgbackPA.setOnClickListener {
            finish()
        }
        binding.btnplayorpausePA.setOnClickListener {

            if (isPlaying) pausemusic() else playmusic()

        }
        binding.btnnextPA.setOnClickListener {
            prevNextSong(increment = true)
        }
        binding.btnpreviousPA.setOnClickListener {
            prevNextSong(increment = false)
        }

        binding.btnShare.setOnClickListener {
            val selectedMusic = musiclistPA[songPosition]

            try {
                // 1. Mở file từ assets
                val inputStream = assets.open(selectedMusic.path)

                // 2. Tạo file tạm trong cache
                val outFile = File(cacheDir, "${selectedMusic.title}.mp3")
                val outputStream = FileOutputStream(outFile)

                // 3. Copy dữ liệu
                inputStream.copyTo(outputStream)

                inputStream.close()
                outputStream.close()

                // 4. Lấy URI bằng FileProvider
                val uri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.provider",  // Đảm bảo khai báo đúng authority trong AndroidManifest
                    outFile
                )

                // 5. Tạo Intent chia sẻ
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "audio/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài hát qua"))

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Không thể chia sẻ bài hát", Toast.LENGTH_SHORT).show()
            }
        }




        binding.btnRepeatPA.setOnClickListener {
            if (!repeat){
                repeat = true
                Toast.makeText(this,"bài hát sẽ được lặp lại",Toast.LENGTH_SHORT).show()
                binding.btnRepeatPA.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
            }else{
                repeat = false
                Toast.makeText(this,"bài hát sẽ dừng lặp lại",Toast.LENGTH_SHORT).show()
                binding.btnRepeatPA.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
            }
        }
        binding.btnTimer.setOnClickListener {
            val min = min10 || min60 || min120
            if (!min){
                setupTimer()
            }else{
                val builder = MaterialAlertDialogBuilder(this)
                    .setTitle("Bạn muốn dừng timer")
                    .setPositiveButton("yes"){dia,_->
                        min10 = false
                        min60 = false
                        min120 = false
                        binding.btnTimer.setColorFilter(ContextCompat.getColor(this,R.color.red))
                        dia.dismiss()
                    }
                    .setNegativeButton("no"){dia,_->
                        dia.dismiss()
                    }
                val cus = builder.create()
                cus.show()
                cus.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                cus.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this,R.color.red))
            }
        }
        binding.btnEqualizer.setOnClickListener {
            try{
                val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService?.mediaPlayer?.audioSessionId)
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(intent,14)
            }catch (e : Exception){
                Toast.makeText(this,"Equalizer error",Toast.LENGTH_SHORT).show()}
        }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService?.mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
        binding.btnFavouritePA.setOnClickListener {
            if (isFavourite){
                isFavourite = false
                binding.btnFavouritePA.setImageResource(R.drawable.baseline_favorite_border_24)
                FavouriteActivity.favouritesong.removeAt(findex)
            }
            else{
                isFavourite = true
                binding.btnFavouritePA.setImageResource(R.drawable.baseline_favorite_24)
                FavouriteActivity.favouritesong.add(musiclistPA[songPosition])
            }
        }
        binding.btnChat.setOnClickListener {
            startActivity(Intent(this,ChatActivity::class.java))
        }

    }
    fun setupSeekbar(){
        runnable = Runnable {
            musicService?.mediaPlayer?.let { player ->
                binding.seekbarStart.text = formatSongDuration(player.currentPosition.toLong())
                binding.seekbar.progress = player.currentPosition
                Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

//    fun setupSeekbar(){
//       runnable = Runnable{
//           binding.seekbarStart.text = formatSongDuration(musicService?.mediaPlayer?.currentPosition!!.toLong())
//           binding.seekbar.progress = musicService?.mediaPlayer?.currentPosition!!
//           Handler(Looper.getMainLooper()).postDelayed(runnable,200)
//       }
//        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 14 || resultCode == RESULT_OK){
            return
        }
    }

    private fun setupTimer() {
        val cus = BottomSheetDialog(this@PlayerMusicActivity)
        cus.setContentView(R.layout.sheetdialogtimer)
        cus.show()
        cus.findViewById<LinearLayout>(R.id.min15)?.setOnClickListener {
            Toast.makeText(this,"bài hát sẽ dừng trong 10 giây",Toast.LENGTH_SHORT).show()
            min10 = true
            binding.btnTimer.setColorFilter(ContextCompat.getColor(this,R.color.red))
            Thread{Thread.sleep(10000)
                if (min10) exitApplication()
            }.start()
            cus.dismiss()

        }
        cus.findViewById<LinearLayout>(R.id.min30)?.setOnClickListener {
            Toast.makeText(this,"bài hát sẽ dừng trong 60 giây",Toast.LENGTH_SHORT).show()
            min60 = true
            binding.btnTimer.setColorFilter(ContextCompat.getColor(this,R.color.red))
            Thread{Thread.sleep(60000)
                if (min60) exitApplication()
            }.start()
            cus.dismiss()
        }
        cus.findViewById<LinearLayout>(R.id.min60)?.setOnClickListener {
            Toast.makeText(this,"bài hát sẽ dừng trong 120 giây",Toast.LENGTH_SHORT).show()
            min120 = true
            binding.btnTimer.setColorFilter(ContextCompat.getColor(this,R.color.red))
            Thread{Thread.sleep(120000)
                if (min120) exitApplication()
            }.start()
            cus.dismiss()
        }
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment){
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        }else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }



    private fun pausemusic() {
        binding.btnplayorpausePA.setImageResource(R.drawable.play)
        isPlaying = false
        musicService?.mediaPlayer?.pause()
        AddPlayingFragment.binding.btnplaypuaefrag.setImageResource(R.drawable.play)
        musicService?.showNotification(R.drawable.play)
    }
    private fun playmusic() {
        PlayerMusicActivity.binding.btnplayorpausePA.setImageResource(R.drawable.baseline_pause_24)
        PlayerMusicActivity.isPlaying = true
        PlayerMusicActivity.musicService?.mediaPlayer?.start()
        AddPlayingFragment.binding.btnplaypuaefrag.setImageResource(R.drawable.baseline_pause_24)
        PlayerMusicActivity.musicService?.showNotification(R.drawable.baseline_pause_24)
    }



    private fun initlizerLayout() {

        songPosition = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "musicAdapter"->{
                val intentservice = Intent(this,MusicService::class.java)
                bindService(intentservice,this, BIND_AUTO_CREATE)
                startForegroundService(intentservice)
                musiclistPA = ArrayList()
                musiclistPA.addAll(MainActivity.musiclistMA)
                setLayout()
            }
            "musicSearch"->{
                val intentservice = Intent(this,MusicService::class.java)
                bindService(intentservice,this, BIND_AUTO_CREATE)
                startForegroundService(intentservice)
                musiclistPA = ArrayList()
                musiclistPA.addAll(MainActivity.musicSearch)
                setLayout()
            }
            "shuffiMain"->{
                val intentservice = Intent(this,MusicService::class.java)
                bindService(intentservice,this, BIND_AUTO_CREATE)
                startForegroundService(intentservice)
                musiclistPA = ArrayList()
                musiclistPA.addAll(MainActivity.musiclistMA)
                musiclistPA.shuffle()
                setLayout()
            }
            "Playing"->{
                setLayout()
                binding.seekbarStart.text = formatSongDuration(musicService?.mediaPlayer?.currentPosition!!.toLong())
                binding.seekbarEnd.text = formatSongDuration(musicService?.mediaPlayer?.duration!!.toLong())
                binding.seekbar.progress = musicService?.mediaPlayer?.currentPosition!!
                binding.seekbar.max = musicService?.mediaPlayer?.duration!!
                if (isPlaying) binding.btnplayorpausePA.setImageResource(R.drawable.baseline_pause_24)
                else binding.btnplayorpausePA.setImageResource(R.drawable.play)

            }
            "ShuffiFavourite"->{
                val intentservice = Intent(this,MusicService::class.java)
                bindService(intentservice,this, BIND_AUTO_CREATE)
                startForegroundService(intentservice)
                musiclistPA = ArrayList()
                musiclistPA.addAll(FavouriteActivity.favouritesong)
                musiclistPA.shuffle()
                setLayout()
            }
            "FavouriteAdapter"->{
                val intentservice = Intent(this,MusicService::class.java)
                bindService(intentservice,this, BIND_AUTO_CREATE)
                startForegroundService(intentservice)
                musiclistPA = ArrayList()
                musiclistPA.addAll(FavouriteActivity.favouritesong)
                setLayout()
            }
            "playlistdetails"->{
                val intentservice = Intent(this,MusicService::class.java)
                bindService(intentservice,this, BIND_AUTO_CREATE)
                startForegroundService(intentservice)
                musiclistPA = ArrayList()
                musiclistPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentMusicPos].playlist)
                setLayout()
            }
            "shuffiPD"->{
                val intentservice = Intent(this,MusicService::class.java)
                bindService(intentservice,this, BIND_AUTO_CREATE)
                startForegroundService(intentservice)
                musiclistPA = ArrayList()
                musiclistPA.shuffle()
                musiclistPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentMusicPos].playlist)
                setLayout()
            }
        }
    }

    fun setLayout() {
        Glide.with(this).load(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].albumArtUri).apply(
            RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
        ).into(PlayerMusicActivity.binding.imgsongpositionPA)

        Glide.with(this).load(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].albumArtUri).apply(
            RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
        ).into(PlayerMusicActivity.binding.imgBlurred)

        PlayerMusicActivity.binding.txvsongnamePA.text = PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].title

        if (min10 || min60 || min120) PlayerMusicActivity.binding.btnTimer.setColorFilter(ContextCompat.getColor(this,R.color.red))


        if (PlayerMusicActivity.repeat) PlayerMusicActivity.binding.btnRepeatPA.setColorFilter(
            ContextCompat.getColor(this,R.color.red))
        else PlayerMusicActivity.binding.btnRepeatPA.setColorFilter(ContextCompat.getColor(this,R.color.black))

        PlayerMusicActivity.findex = checkFavourite(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].id)
        if (PlayerMusicActivity.isFavourite) PlayerMusicActivity.binding.btnFavouritePA.setImageResource(R.drawable.baseline_favorite_24)
        else PlayerMusicActivity.binding.btnFavouritePA.setImageResource(R.drawable.baseline_favorite_border_24)


    }

    //    private fun createMediaPlayer() {
//        try {
////            // Hiện loading animation, ẩn play/pause
////            binding.btnplayorpausePA.visibility = View.GONE
////            binding.loadingAnim.visibility = View.VISIBLE
////            binding.loadingAnim.playAnimation()
//            val selectedMusic = musiclistPA[songPosition]
//            val afd = assets.openFd(selectedMusic.path)
//            if (musicService?.mediaPlayer == null) musicService?.mediaPlayer = MediaPlayer()
//            musicService?.mediaPlayer?.reset()
//            musicService?.mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
//            musicService?.mediaPlayer?.prepare()
//            musicService?.mediaPlayer?.start()
//
////            musicService?.mediaPlayer?.setOnPreparedListener {
////                it.start()
//
//                // Khi đã load xong: ẩn loading, hiện nút play
////                Log.e("Player", "Animation done - hiển thị play/pause")
////                binding.loadingAnim.cancelAnimation()
////                binding.loadingAnim.visibility = View.GONE
////                binding.btnplayorpausePA.visibility = View.VISIBLE
//                binding.btnplayorpausePA.setImageResource(R.drawable.baseline_pause_24)
//
////                // Gọi UI update
////                runOnUiThread {
////                    setLayout()
////                    setupSeekbar()
////                }
//
//                isPlaying = true
//                musicService?.showNotification(R.drawable.baseline_pause_24)
//
//                binding.seekbarStart.text = formatSongDuration(musicService?.mediaPlayer?.currentPosition!!.toLong())
//                binding.seekbarEnd.text = formatSongDuration(musicService?.mediaPlayer?.duration!!.toLong())
//                binding.seekbar.progress = 0
//                binding.seekbar.max = musicService?.mediaPlayer?.duration!!
//                setupSeekbar()
//            musicService?.mediaPlayer?.setOnCompletionListener(this)
//            nowPlaying = musiclistPA[songPosition].id
//
//        } catch (e: Exception) {
//            Log.e("createMediaPlayer", "Error: ${e.message}")
//        }
//    }
    private fun createMediaPlayer() {
        try {

            val selectedMusic = musiclistPA[songPosition]
            val afd = assets.openFd(selectedMusic.path)
            if (musicService?.mediaPlayer == null) musicService?.mediaPlayer = MediaPlayer()
            musicService?.mediaPlayer?.reset()
            musicService?.mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            musicService?.mediaPlayer?.prepare()
            musicService?.mediaPlayer?.start()
            isPlaying = true
            binding.btnplayorpausePA.setImageResource(R.drawable.baseline_pause_24)
            musicService?.showNotification(R.drawable.baseline_pause_24)
            binding.seekbarStart.text = formatSongDuration( musicService?.mediaPlayer?.currentPosition!!.toLong())
            binding.seekbarEnd.text = formatSongDuration( musicService?.mediaPlayer?.duration!!.toLong())
            binding.seekbar.progress = 0
            binding.seekbar.max =  musicService?.mediaPlayer?.duration!!
            setupSeekbar()


            musicService?.mediaPlayer?.setOnCompletionListener(this)
            nowPlaying = musiclistPA[songPosition].id

        } catch (e: Exception) {
            Log.e("createMediaPlayer", "Error: ${e.message}")
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        Glide.with(baseContext).load(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].albumArtUri).apply(
            RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
        ).into(AddPlayingFragment.binding.imgfrag)
        AddPlayingFragment.binding.txvfrag.text = PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].title
        createMediaPlayer()

        try {
            setLayout()
        }catch (e:Exception){return}
    }


}