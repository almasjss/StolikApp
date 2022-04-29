package kz.com.stolikapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kz.com.stolikapp.databinding.ActivityMainBinding
import kz.com.stolikapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    //view biding

    private  lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_profile)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //logOutBtn click , logout user

        binding.loggoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }
    }
    private fun checkUser(){
    val firebaseAuth = firebaseAuth.currentUser
        if(firebaseAuth == null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        else{
            //logged in get phone number of user
            val phone = firebaseAuth.phoneNumber
            //set phone number
            binding.phoneTv.text=phone
        }
    }
}