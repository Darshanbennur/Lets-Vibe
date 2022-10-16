package com.example.letsvibe

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.icu.number.IntegerWidth
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.letsvibe.databinding.ActivityNowPlayingBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.math.roundToInt

class nowPlaying : AppCompatActivity() {
    private lateinit var binding : ActivityNowPlayingBinding
    var mediaID : Int = 0

    var stringMusicUrl : String = ""
    var stringImageUrl : String = ""
    var singerName : String = ""
    var songName : String = ""

    private lateinit var sharedPreferences : SharedPreferences

    //for taking them into Favourites
    var favourites : Boolean = false

    var shuru : Boolean = false

    private lateinit var mediaPlayer : MediaPlayer

    private var totalSongDuration : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNowPlayingBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE)

        var intent : Intent = this.intent
        mediaID = intent.getIntExtra("mediaID",0)

        fetchMusicDetails(mediaID)

        binding.animater.pauseAnimation()

        mediaPlayer = MediaPlayer()
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.5f,0.5f)

        binding.songProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2){
                    mediaPlayer.seekTo(p1)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2){
                    var vol = p1 / 100.0f
                    mediaPlayer.setVolume(vol,vol)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })


        Thread(Runnable {
            while (mediaPlayer != null){
                try {
                    var msg = Message()
                    msg.what = mediaPlayer.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                }catch (E : InterruptedException){

                }
            }
        }).start()
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what

            binding.songProgress.progress = currentPosition

            var elapsedTime = createTimeLabel(currentPosition)
            binding.timeElapsed.text = elapsedTime

            var remainingIme = createTimeLabel(totalSongDuration - currentPosition)
            binding.timeRemaining.text = "-$remainingIme"
        }
    }

    fun favThisSong(view : View){
        if (favourites){
            binding.favSong.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
            favourites = false
            setFavourites("false")
        }else{
            binding.favSong.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
            favourites = true
            setFavourites("true")
        }
    }

    fun setFavourites(fav : String){
        var updateFav : DatabaseReference = Firebase.database.getReference("Users")
        var favNum : Int = 0
        if(fav.equals("true")){
            favNum = mediaID
        }else{
            favNum = -1
        }
        val favourUpdate = mapOf<String,Int>(
            mediaID.toString() to favNum
        )
        updateFav.child(
            sharedPreferences.getString("number","user1").toString()
        ).child("Favourites").updateChildren(favourUpdate).addOnSuccessListener {
            Toast.makeText(applicationContext,"Added to Favourites DataBase",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext,"Not added to Database",Toast.LENGTH_SHORT).show()
        }
    }

    private fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10)
            timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    fun playSong(view : View){
        if (shuru){
            mediaPlayer.pause()
            binding.animater.pauseAnimation()
            binding.playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24)
            shuru = false
        }else{
            mediaPlayer.start()
            binding.animater.playAnimation()
            totalSongDuration = mediaPlayer.duration
            binding.songProgress.max = totalSongDuration
            binding.playButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            shuru = true
        }
    }

    fun forwardSong(view : View){
        mediaPlayer.stop()
        mediaID %= 9
        mediaID++
        fetchMusicDetails(mediaID)
        mediaPlayer = MediaPlayer()
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        shuru = false
        binding.playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24)
    }

    fun backwardSong(view : View){
        mediaPlayer.stop()
        mediaID--
        if (mediaID == 0){
            mediaID = 9
        }
        fetchMusicDetails(mediaID)
        mediaPlayer = MediaPlayer()
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        shuru = false
        binding.playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24)
    }

    fun prepareMediaPlayer(string : String){
        try {
            mediaPlayer.setDataSource(string)
            mediaPlayer.prepare()
        }catch (e : Exception){
//            Toast.makeText(applicationContext,e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchMusicDetails(media : Int){
        var fetchData : DatabaseReference = Firebase.database.getReference("Songs").child(media.toString())
        fetchData.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    if(Objects.equals(data.key,"songURL")){
                        stringMusicUrl = data.value.toString()
                        prepareMediaPlayer(stringMusicUrl)
                    }
                    if(Objects.equals(data.key,"imageURL")){
                        stringImageUrl = data.value.toString()
                        Glide.with(this@nowPlaying).load(stringImageUrl).into(binding.avatarImage)
                    }
                    if(Objects.equals(data.key,"singerName")){
                        singerName = data.value.toString()
                        binding.singerName.text = "- " + singerName
                    }
                    if(Objects.equals(data.key,"Name")){
                        songName = data.value.toString()
                        binding.songName.text = songName
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        var favUpdate : DatabaseReference = Firebase.database.getReference("Users").child(
            sharedPreferences.getString("number","user1").toString()
        ).child("Favourites")
        favUpdate.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.favSong.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
                favourites = false
                for (data in snapshot.children){
                    if (Objects.equals(data.value.toString(),mediaID.toString())){
                        binding.favSong.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
                        favourites = true
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onBackPressed() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        finish()
        super.onBackPressed()
    }
}