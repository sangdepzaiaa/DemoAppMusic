package com.example.musicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    companion object{
        lateinit var binding : ActivitySelectionBinding
        var musicAdapter : MusicAdapter?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rcvSelec.setHasFixedSize(true)
        binding.rcvSelec.setItemViewCacheSize(100)
        binding.rcvSelec.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this,MainActivity.musiclistMA, selectionActivity = true)
        binding.rcvSelec.adapter = musicAdapter
        binding.searchSelec.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean =true

            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicSearch = ArrayList()
                if (newText != null){
                    MainActivity.search = true
                    val input = newText.lowercase()
                    for (i in MainActivity.musiclistMA){
                        if (i.title.lowercase().contains(input)){
                            MainActivity.musicSearch.add(i)
                        }
                    }
                    musicAdapter?.updateSearch(searchlist = MainActivity.musicSearch)
                }
                return true
            }

        })
    }
}