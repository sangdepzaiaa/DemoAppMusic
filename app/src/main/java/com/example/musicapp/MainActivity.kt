package com.example.musicapp

import android.content.Intent

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.ActivityMainBinding
import java.io.File
import android.Manifest
import android.app.FragmentManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException
import kotlin.system.exitProcess
import android.graphics.Typeface
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentContainerView
import com.google.gson.Gson


class MainActivity : AppCompatActivity(){
    companion object{
        var musiclistMA = ArrayList<Music>()
        var musicAdapter : MusicAdapter?=null
        lateinit var binding: ActivityMainBinding
        var musicSearch = ArrayList<Music>()
        var search:Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestRuntimePermission()

        FavouriteActivity.favouritesong = ArrayList()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
        val jsonString = editor.getString("favouritesong",null)
        val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
        if (jsonString != null){
            val data : ArrayList<Music> = GsonBuilder().create().fromJson(jsonString,typeToken)
            FavouriteActivity.favouritesong.addAll(data)
        }

        PlaylistActivity.musicPlaylist = MusicPlaylist()
        val jsonStringpl = editor.getString("plsong",null)
        if (jsonStringpl != null){
            val datapl : MusicPlaylist = GsonBuilder().create().fromJson(jsonStringpl,MusicPlaylist::class.java)
            PlaylistActivity.musicPlaylist = datapl
        }



        musiclistMA = loadMusicListFromJson2(this)
        binding.rcvSongs.setHasFixedSize(true)
        binding.rcvSongs.setItemViewCacheSize(30)
        binding.rcvSongs.layoutManager = GridLayoutManager(this,3)
        musicAdapter = MusicAdapter(this,  musiclistMA)
        binding.rcvSongs.adapter = musicAdapter
        binding.txvtotals.text = "Total Songs: ${musicAdapter?.itemCount}"

        binding.btnShuffi.setOnClickListener {
            val intent = Intent(this,PlayerMusicActivity::class.java)
            intent.putExtra("class","shuffiMain")
            startActivity(intent)
        }

        val user = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
            .getString("current_user", null)
        binding.txvUser.text = "${user}"
        binding.search.setIconifiedByDefault(false) // luôn hiển thị input
        binding.search.setSubmitButtonEnabled(false)
        binding.search.clearFocus() // không bị focus ngay lúc mở app

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean{
                musicSearch = ArrayList()

                if (newText != null){
                    var input = newText.lowercase()
                    search = true
                    for (i in musiclistMA){
                        if(i.title.lowercase().contains(input)){
                            musicSearch.add(i)
                        }
                    }
                    musicAdapter?.updateSearch(searchlist = musicSearch)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean = true

        })





        binding.btnMe.setOnClickListener {
            startActivity(Intent(this,MeActivity::class.java))
        }
    }
    private fun requestRuntimePermission(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        // Kiểm tra phiên bản Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 trở lên
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivityForResult(intent, 101) // Mã yêu cầu là 101
                return false
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6 đến Android 10
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE // Thêm quyền ghi
                    ),
                    102 // Mã yêu cầu là 102
                )
            }
            return  false
        } else {
            // Android 5 trở xuống, không cần xử lý quyền runtime
            Toast.makeText(this, "Permission not required for this Android version", Toast.LENGTH_LONG).show()
            return false
        }
        return  true
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 || requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền đã được cấp!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền để hoạt động!", Toast.LENGTH_LONG).show()
            }
        }

        when (requestCode) {
            102 -> { // READ_EXTERNAL_STORAGE và WRITE_EXTERNAL_STORAGE
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Toast.makeText(this, "Unknown permission request code: $requestCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Xử lý kết quả từ ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) { // Mã yêu cầu cho quyền quản lý tệp
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "MANAGE_EXTERNAL_STORAGE permission granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Permission denied. Cannot access all files.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    fun loadMusicListFromJson2(context: Context): ArrayList<Music> {
        val json = context.assets.open("string.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Music>>() {}.type
        return gson.fromJson(json, type)
    }

    fun loadMusicListFromJson(): ArrayList<Music> {
        val musicList = ArrayList<Music>()
        try {
            val inputStream = assets.open("string.json")  // Giả sử bạn để trong assets
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val jsonString = String(buffer, Charsets.UTF_8)
            val listType = object : TypeToken<ArrayList<Music>>(){}.type
            musicList.addAll(GsonBuilder().create().fromJson(jsonString,listType))

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return musicList

    }

    fun getRandomMusicItems(sourceList: ArrayList<Music>): ArrayList<Music> {
        // Nếu danh sách có ít hơn 4 phần tử, trả về toàn bộ danh sách
        if (sourceList.size <= 6) {
            return ArrayList(sourceList)
        }

        // Dùng shuffled để trộn ngẫu nhiên, sau đó lấy 4 phần tử đầu
        return ArrayList(sourceList.shuffled().take(6))
    }

    fun mergeUnique(list1: ArrayList<Music>, list2: ArrayList<Music>): ArrayList<Music> {
        val combined = list1 + list2
        return ArrayList(combined.distinctBy { it.id }) // Loại trùng theo id
    }



    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerMusicActivity.isPlaying && PlayerMusicActivity.musicService?.mediaPlayer != null){
            exitApplication()
        }
    }

    override fun onResume() {
        super.onResume()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouritesong)
        editor.putString("favouritesong",jsonString)
        val jsonStringpl = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("plsong",jsonStringpl)
        editor.apply()
        val fragmentView = findViewById<FragmentContainerView>(R.id.nowplaying)

        if (PlayerMusicActivity.musicService?.mediaPlayer == null) {
            fragmentView.visibility = View.GONE
        } else {
            fragmentView.visibility = View.VISIBLE
        }
    }

}


