package com.example.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicapp.databinding.FragmentAddPlayingBinding

class AddPlayingFragment : Fragment() {
    companion object{
        lateinit var binding: FragmentAddPlayingBinding
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_playing,container,false)
        binding = FragmentAddPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE

        binding.btnplaypuaefrag.setOnClickListener {
            if (PlayerMusicActivity.isPlaying) pausemusic() else playmusic()
        }
        binding.btnnextfrag.setOnClickListener {
            setSongPosition(increment = true)
            PlayerMusicActivity.musicService?.createMediaPlayer2()
            Glide.with(this).load(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].albumArtUri).apply(
                RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
            ).into(binding.imgfrag)
            binding.txvfrag.text = PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].title
            playmusic()
        }
        binding.root.setOnClickListener {
            val intent  = Intent(requireContext(),PlayerMusicActivity::class.java)
            intent.putExtra("index",PlayerMusicActivity.songPosition)
            intent.putExtra("class","Playing")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }


    private fun playmusic() {
        PlayerMusicActivity.isPlaying = true
        PlayerMusicActivity.binding.btnplayorpausePA.setImageResource(R.drawable.baseline_pause_24)
        PlayerMusicActivity.musicService?.showNotification(R.drawable.baseline_pause_24)
        binding.btnplaypuaefrag.setImageResource(R.drawable.baseline_pause_24)
        PlayerMusicActivity.musicService?.mediaPlayer?.start()
    }

    private fun pausemusic() {
        PlayerMusicActivity.isPlaying = false
        PlayerMusicActivity.binding.btnplayorpausePA.setImageResource(R.drawable.play)
        PlayerMusicActivity.musicService?.showNotification(R.drawable.play)
        binding.btnplaypuaefrag.setImageResource(R.drawable.play)
        PlayerMusicActivity.musicService?.mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (PlayerMusicActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            binding.txvfrag.isSelected = true
            Glide.with(requireContext()).load(PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].albumArtUri).apply(
                RequestOptions.placeholderOf(R.drawable.nct).centerCrop()
            ).into(binding.imgfrag)
            binding.txvfrag.text = PlayerMusicActivity.musiclistPA[PlayerMusicActivity.songPosition].title

            if (PlayerMusicActivity.isPlaying) binding.btnplaypuaefrag.setImageResource(R.drawable.baseline_pause_24)
            else binding.btnplaypuaefrag.setImageResource(R.drawable.play)
        }else {
            binding.root.visibility = View.GONE
        }

    }







}