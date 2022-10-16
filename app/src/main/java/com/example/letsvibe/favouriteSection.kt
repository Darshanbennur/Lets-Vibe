package com.example.letsvibe

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
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
import kotlin.collections.ArrayList

class favouriteSection : AppCompatActivity(), RecAdapter.OnItemClickListener, SearchView.OnQueryTextListener  {

    private lateinit var binding : ActivityFavouriteSectionBinding
    lateinit var toggle : ActionBarDrawerToggle

    lateinit var songArrayMediaIDList : ArrayList<Int>
    lateinit var songArrayList : ArrayList<Songs>
    private lateinit var filteredList : ArrayList<Songs>

    private lateinit var sharedPreferences : SharedPreferences

    private lateinit var alert : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteSectionBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = sharedPreferences.edit()

        binding.recViewfav.layoutManager = LinearLayoutManager(this)
        binding.recViewfav.setHasFixedSize(true)

        toggle = ActionBarDrawerToggle(this,binding.drawerLayoutfav,R.string.open,R.string.close)
        binding.drawerLayoutfav.addDrawerListener(toggle)
        toggle.syncState()

        alert = AlertDialog.Builder(this)

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
                R.id.menuItem5 ->{
                    alert.setTitle("Are you Sure?")
                        .setMessage("Are you Sure You want to Logout")
                        .setCancelable(true)
                        .setPositiveButton("Yes"){ _: DialogInterface, _: Int ->
                            editor.clear()
                            editor.apply()
                            val intent = Intent(applicationContext, LoginPage::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .setNegativeButton("No"){ dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.cancel()
                        }
                        .show()
                }
            }
            true
        }

        songArrayMediaIDList = ArrayList()
        songArrayList = ArrayList()
        filteredList = ArrayList()
        getFavouriteMediaID()
        retrieveSongs()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getFavouriteMediaID(){
        val getFavID : DatabaseReference = Firebase.database.getReference("Users").child(
            sharedPreferences.getString("number","user1").toString()
        ).child("Favourites")
        getFavID.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataID in snapshot.children){
                    var value : Int = Integer.parseInt(dataID.value.toString())
                    if (value != -1){
                        songArrayMediaIDList.add(value)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrieveSongs() {
        val databaseRef : DatabaseReference = Firebase.database.getReference("Songs")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var dataSongName : String = ""
                var dataImageURL : String = ""
                var dataMediaID : String = ""
                var dataSingerName : String = ""
                var dataSongURL : String = "some shit"

                var id : Int = 0

                for (snapshot_01 : DataSnapshot in snapshot.children){
                    for (snapshot_02 : DataSnapshot in snapshot_01.children){
                        if(Objects.equals(snapshot_02.key,"mediaID")) {
                            dataMediaID = snapshot_02.value.toString()
                            id = Integer.parseInt(dataMediaID)
                        }
                        if(Objects.equals(snapshot_02.key,"Name"))
                            dataSongName = snapshot_02.value.toString()
                        if(Objects.equals(snapshot_02.key,"imageURL"))
                            dataImageURL = snapshot_02.value.toString()
                        if(Objects.equals(snapshot_02.key,"singerName"))
                            dataSingerName = snapshot_02.value.toString()
                    }
                    if (songArrayMediaIDList.contains(id)){
                        val songObj = Songs(dataSongName, dataImageURL, dataMediaID, dataSingerName, dataSongURL)
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
        var num = 0
        if (filteredList.isEmpty()){
            num = Integer.parseInt(songArrayList[position].mediaID)
        }else{
            num = Integer.parseInt(filteredList[position].mediaID)
        }

        intent.putExtra("mediaID",num)
        startActivity(intent)
    }

    private fun filterList(newText: String?) {
        filteredList.clear()
        for (item : Songs in songArrayList){
            if (newText != null) {
                if (item.Name.toLowerCase().contains(newText.toLowerCase())){
                    filteredList.add(item)
                }
            }
        }

        if (filteredList.isEmpty()){
            filteredList.clear()
            binding.recViewfav.adapter = RecAdapter(filteredList,this@favouriteSection)
            Toast.makeText(applicationContext,"No Such Songs",Toast.LENGTH_SHORT).show()
        }else{
            binding.recViewfav.adapter = RecAdapter(filteredList,this@favouriteSection)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view,menu)
        val  search = menu?.findItem(R.id.searchView)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = false
        searchView?.setOnQueryTextListener(this)
        return true
    }

    override fun onBackPressed() {
        finish()
        var intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            filterList(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null){
            filterList(newText)
        }
        return true
    }
}