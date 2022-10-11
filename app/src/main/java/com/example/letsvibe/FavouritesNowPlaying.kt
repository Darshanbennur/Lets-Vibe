package com.example.letsvibe

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.letsvibe.databinding.ActivityFavouritesNowPlayingBinding
import com.example.letsvibe.databinding.ActivityNowPlayingBinding

class FavouritesNowPlaying : AppCompatActivity() {
    lateinit var binding : ActivityFavouritesNowPlayingBinding

    var mediaID : Int = 0

    var stringMusicUrl : String = ""
    var stringImageUrl : String = ""
    var singerName : String = ""
    var songName : String = ""

    var favourites : Boolean = false

    var shuru : Boolean = false

    private lateinit var mediaPlayer : MediaPlayer

    private var totalSongDuration : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesNowPlayingBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)


    }



}