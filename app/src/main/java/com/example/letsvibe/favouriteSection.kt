package com.example.letsvibe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letsvibe.databinding.ActivityFavouriteSectionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class favouriteSection : AppCompatActivity(), RecAdapter.OnItemClickListener  {

    private lateinit var binding : ActivityFavouriteSectionBinding
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var songArrayList : ArrayList<Songs>

    var sendingSong : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteSectionBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.recViewfav.layoutManager = LinearLayoutManager(this)
        binding.recViewfav.setHasFixedSize(true)

        toggle = ActionBarDrawerToggle(this,binding.drawerLayoutfav,R.string.open,R.string.close)
        binding.drawerLayoutfav.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menuItem1 -> {
                    finish()
                    var intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuItem2 -> {
                    finish()
                    var intent = Intent(this,favouriteSection::class.java)
                    startActivity(intent)
                }
                R.id.menuItem3 -> Toast.makeText(applicationContext,"Developer is Noob", Toast.LENGTH_SHORT).show()
                R.id.menuItem4 -> Toast.makeText(applicationContext,"It's Private Ok...", Toast.LENGTH_SHORT).show()
            }
            true
        }

        songArrayList = ArrayList()
        retrieveSongs()
    }

    fun opendrawer(view: View?) {
        binding.drawerLayoutfav.openDrawer(GravityCompat.START)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun retrieveSongs() {
        val databaseRef : DatabaseReference = Firebase.database.getReference("Songs")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var favour : String = ""
                var dataSongName : String = ""
                var dataImageURL : String = ""
                var dataMediaID : String = ""
                var dataSingerName : String = "some shit"
                var dataSongURL : String = "some shit"

                for (snapshot_01 : DataSnapshot in snapshot.children){
                    for (snapshot_02 : DataSnapshot in snapshot_01.children){
                        if(Objects.equals(snapshot_02.key,"Favourites"))
                            favour = snapshot_02.value.toString()
                        if(Objects.equals(snapshot_02.key,"Name"))
                            dataSongName = snapshot_02.value.toString()
                        if(Objects.equals(snapshot_02.key,"imageURL"))
                            dataImageURL = snapshot_02.value.toString()
                        if(Objects.equals(snapshot_02.key,"mediaID")) {
                            dataMediaID = snapshot_02.value.toString()
                            sendingSong = Integer.parseInt(snapshot_02.value.toString())
                        }
                    }
                    if (favour.equals("true")){
                        val songObj = Songs(favour, dataSongName, dataImageURL, dataMediaID, dataSingerName, dataSongURL)
                        songArrayList.add(songObj)
                    }
                }
                binding.recViewfav.adapter = RecAdapter(songArrayList,this@favouriteSection)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onItemClick(position: Int) {
        var intent = Intent(this,nowPlaying::class.java)
        var num = Integer.parseInt(songArrayList[position].mediaID)
        intent.putExtra("mediaID",num)
        startActivity(intent)
    }

    override fun onBackPressed() {
        finish()
        var intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}