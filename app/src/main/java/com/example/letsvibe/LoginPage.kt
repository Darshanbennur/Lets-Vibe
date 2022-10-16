package com.example.letsvibe

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.letsvibe.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class LoginPage : AppCompatActivity() {

    private lateinit var binding : ActivityLoginPageBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.registerHereButton.setOnClickListener {
            var intent = Intent(this,RegisterPage::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            var userPhone = binding.userPhoneNumber.text.toString()
            var userEmail = binding.userEmail.text.toString()
            var password = binding.userPassword.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            Toast.makeText(applicationContext,"Invalid Email Address Format", Toast.LENGTH_SHORT).show()
            binding.userEmail.requestFocus()
            }
            else if (userPhone.length != 10){
                Toast.makeText(applicationContext,"Phone Number to be 10 letters",Toast.LENGTH_SHORT).show()
                binding.userPhoneNumber.requestFocus()
            }
            else if (password.length < 8){
                Toast.makeText(applicationContext,"Password length minimum 8 characters",Toast.LENGTH_SHORT).show()
                binding.userPassword.requestFocus()
            }
            else{
                fetchUserphone (userPhone,userEmail)
                var flag = true
                var intent = Intent(this,MainActivity::class.java)
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        if (binding.helper.text.equals(userEmail)){
                            firebaseAuth.signInWithEmailAndPassword(userEmail,password).addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(applicationContext,"Logged in Successfully",Toast.LENGTH_SHORT).show()
                                    sharedPreferences.edit().putString("number","" + userPhone).apply()
                                    flag = false
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Toast.makeText(applicationContext,"Login Error",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        timer.cancel()
                    }
                }, 2000)
            }
        }
    }

    private fun fetchUserphone (userPhone : String, mail : String){
        var ref : DatabaseReference = Firebase.database.getReference("Users").child(userPhone)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                for (data in snapshot.children){
                    if(Objects.equals(data.key,"Email")) {
                        binding.helper.text = data.value.toString()
                        if (!binding.helper.text.toString().equals(mail)){
                            Toast.makeText(applicationContext,"Email not Linked with this Number ",Toast.LENGTH_SHORT).show()
                            binding.userPhoneNumber.requestFocus()
                            binding.userPhoneNumber.error = "Check Phone Number"
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}