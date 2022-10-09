package com.example.letsvibe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import kotlin.collections.List
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letsvibe.databinding.ActivityMainBinding


import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), RecAdapter.OnItemClickListener {

    private lateinit var binding : ActivityMainBinding
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var songArrayList : ArrayList<Songs>

//    lateinit var filteredList : ArrayList<Songs>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

//        binding.navSearchBar.clearFocus()
//        binding.navSearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filterList(newText)
//                return true
//            }
//
//        })


        binding.recView.layoutManager = LinearLayoutManager(this)
        binding.recView.setHasFixedSize(true)
        // NavigationBar Setup
        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
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
                R.id.menuItem3 -> Toast.makeText(applicationContext,"Developer is Noob",Toast.LENGTH_SHORT).show()
                R.id.menuItem4 -> Toast.makeText(applicationContext,"It's Private Ok....",Toast.LENGTH_SHORT).show()
            }
            true
        }

        //Initializing of the array List
        songArrayList = ArrayList()
        retrieveSongs()
    }

    private fun retrieveSongs() {
        val databaseRef : DatabaseReference = Firebase.database.getReference("Songs")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var favour : String = "some shit"
                var dataSongName : String = ""
                var dataImageURL : String = ""
                var dataMediaID : String = ""
                var dataSingerName : String = "some shit"
                var dataSongURL : String = "some shit"

                for (snapshot_01 : DataSnapshot in snapshot.children){
                    for (snapshot_02 : DataSnapshot in snapshot_01.children){
                        if(Objects.equals(snapshot_02.key,"Name"))
                            dataSongName = snapshot_02.value.toString()
                        if(Objects.equals(snapshot_02.key,"imageURL"))
                            dataImageURL = snapshot_02.value.toString()
                        if(Objects.equals(snapshot_02.key,"mediaID"))
                            dataMediaID = snapshot_02.value.toString()
                    }
                    val songObj = Songs(favour, dataSongName, dataImageURL, dataMediaID, dataSingerName, dataSongURL)
                    songArrayList.add(songObj)
                }
                binding.recView.adapter = RecAdapter(songArrayList,this@MainActivity)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun opendrawer(view: View?) {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(position: Int) {
        var intent = Intent(this,nowPlaying::class.java)
        var num = Integer.parseInt(songArrayList[position].mediaID)
        intent.putExtra("mediaID",num)
        startActivity(intent)
    }

}

