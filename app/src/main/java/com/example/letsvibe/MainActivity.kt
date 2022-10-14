package com.example.letsvibe


import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letsvibe.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity(), RecAdapter.OnItemClickListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var toggle : ActionBarDrawerToggle
    lateinit var songArrayList : ArrayList<Songs>
    private lateinit var preferences : SharedPreferences
    private lateinit var firebaseAuth : FirebaseAuth

    private lateinit var alert : AlertDialog.Builder

//    lateinit var filteredList : ArrayList<Songs>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        preferences = getSharedPreferences("userData", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = preferences.edit()

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

        alert = AlertDialog.Builder(this)

        firebaseAuth = FirebaseAuth.getInstance()

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
                R.id.menuItem5 -> {
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
        fetchUserDetails()
        //Initializing of the array List
        songArrayList = ArrayList()
        retrieveSongs()

    }

    private fun fetchUserDetails(){
        val refer : DatabaseReference = Firebase.database.getReference("Users").child(
            preferences.getString("number","user1").toString()
        )
        refer.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    if (Objects.equals(data.key,"Name")){
                        binding.displayName.text = "Hello, " + data.value.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun retrieveSongs() {
        val databaseRef : DatabaseReference = Firebase.database.getReference("Songs")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
//                var favour : String = "some shit"
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
                    val songObj = Songs(dataSongName, dataImageURL, dataMediaID, dataSingerName, dataSongURL)
                    songArrayList.add(songObj)
                }
                binding.recView.adapter = RecAdapter(songArrayList,this@MainActivity)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

//    override fun onStart() {
//        super.onStart()
//        var user : FirebaseUser? = firebaseAuth.currentUser
//        if (user == null){
//            val intent = Intent(this, LoginPage::class.java)
//            startActivity(intent)
//        }
//    }

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

