package com.example.musicapp

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.Serializable
import java.io.StringReader
import kotlin.system.exitProcess
import kotlin.time.Duration

data class Music(val id:String,val title:String, val artist:String,val duration: Long =0,val path:String,
                 val albumArtUri:String
)

class Playlist{
    lateinit var name: String
    lateinit var playlist: ArrayList<Music>
    lateinit var createOn: String
    lateinit var createBy:String
}

class MusicPlaylist{
    var ref: ArrayList<Playlist> = ArrayList()
}



fun formatSongDuration(duration: Long): String{
    val mimute = duration / 1000 / 60
    val second = (duration / 1000 % 60 ).toString().padStart(2,'0')
    return "$mimute:$second"
}

//fun getArtImg(path: String): ByteArray?{
//   val retriever = MediaMetadataRetriever()
//    retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
//
//    retriever.setDataSource(path)
// return  retriever.embeddedPicture
//}
fun getArtImg(context: Context, path: String): ByteArray? {
    return try {
        val afd = context.assets.openFd(path)
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        val art = retriever.embeddedPicture
        retriever.release()
        art
    } catch (e: Exception) {
        null
    }
}



fun exitApplication2(){
    if (PlayerMusicActivity.musicService != null ){
        PlayerMusicActivity.musicService?.stopForeground(true)
        PlayerMusicActivity.musicService?.mediaPlayer?.release()
        PlayerMusicActivity.musicService?.mediaPlayer = null
    }
}


fun exitApplication(){
    if (PlayerMusicActivity.musicService != null){
        PlayerMusicActivity.musicService?.mediaPlayer?.release()
        PlayerMusicActivity.musicService?.mediaPlayer = null
        exitProcess(1)
    }
}

fun setSongPosition(increment: Boolean) {
    if (!PlayerMusicActivity.repeat){
        if (increment){
            if (PlayerMusicActivity.songPosition == PlayerMusicActivity.musiclistPA.size - 1){
                PlayerMusicActivity.songPosition = 0
            }else ++PlayerMusicActivity.songPosition
        }else{
            if (PlayerMusicActivity.songPosition == 0){
                PlayerMusicActivity.songPosition = PlayerMusicActivity.musiclistPA.size -1
            }else --PlayerMusicActivity.songPosition
        }
    }


}

fun checkFavourite(id: String): Int{
    PlayerMusicActivity.isFavourite = false
    FavouriteActivity.favouritesong.forEachIndexed { index, music ->
        if (id == music.id){
            PlayerMusicActivity.isFavourite = true
            return index
        }
    }
    return -1
}

fun checkPlaylist(song : Music):Boolean{
    PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentMusicPos].playlist.forEachIndexed { index, music ->
        if (song.id == music.id){
            PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentMusicPos].playlist.removeAt(index)
            return false
        }
    }
    PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentMusicPos].playlist.add(song)
    return true
}