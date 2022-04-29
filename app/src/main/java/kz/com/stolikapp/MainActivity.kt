package kz.com.stolikapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.JsonToken
import android.util.Log
import android.view.View
import android.view.WindowId
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kz.com.stolikapp.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding  : ActivityMainBinding


    private var forceResendingToken:PhoneAuthProvider.ForceResendingToken?=null

    private var mCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks?=null
    private var mVerificatonId:String?=null
    private lateinit var firebaseAuth:FirebaseAuth

    private val TAG = "MAIN TAG"

    private lateinit var progressDialog:ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        binding.phonell.visibility = View.VISIBLE
        binding.codell.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(phoneAuthCredtinal: PhoneAuthCredential) {
                signInWithAuthCredentials(phoneAuthCredtinal)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity,"${e.message}",Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificatonId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG,"onCodeSent: $verificatonId")
                mVerificatonId = verificatonId
                forceResendingToken = token
                progressDialog.dismiss()

                    //hide phone layout , show code layout
                binding.phonell.visibility=View.VISIBLE
                binding.codell.visibility=View.GONE
                Toast.makeText(this@MainActivity,"Verification code sent...",Toast.LENGTH_SHORT).show()
                binding.codeSentDescription.text = "Please type the verification code we sent to ${binding.phonEt.text.toString().trim()}"

            }
        }
        binding.phoneContinueBtn.setOnClickListener{
    val phone = binding.phonEt.text.toString().trim()
            //validate phone number
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@MainActivity,"Please enter phone number",Toast.LENGTH_SHORT).show()
            }
            else{
                startPhoneNumberVerification(phone)
            }
        }
        binding.resendCodeTv.setOnClickListener {
            val phone = binding.phonEt.text.toString().trim()
            //validate phone number
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@MainActivity,"Please enter phone number",Toast.LENGTH_SHORT).show()
            }
            else{
                resendVerificationCode(phone,forceResendingToken)
            }
        }
        binding.phoneContinueBtn.setOnClickListener{
            val code = binding.codeEt.text.toString().trim()
            //validate phone number
            if(TextUtils.isEmpty(code)){
                Toast.makeText(this@MainActivity,"Please enter phone number",Toast.LENGTH_SHORT).show()
            }
            else{
                verifyPhoneNumberWithCode(mVerificatonId,code)
            }
        }

        }
    private fun startPhoneNumberVerification(phone:String){
        progressDialog.setMessage("Resending Code...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks!!)
            .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

    }
    private fun resendVerificationCode(phone: String,token: PhoneAuthProvider.ForceResendingToken?){
        progressDialog.setMessage("Resending code...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks!!)
            .setForceResendingToken(token!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun verifyPhoneNumberWithCode(verificationId: String?,code:String){
        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()
        val credential = PhoneAuthProvider.getCredential(verificationId!!,code)
        signInWithAuthCredentials(credential)

    }
    private fun signInWithAuthCredentials(credential: PhoneAuthCredential){
progressDialog.setMessage("Logging In")
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser!!.phoneNumber
                Toast.makeText(this,"Logged In as $phone",Toast.LENGTH_SHORT).show()
                    //start profile activity
                startActivity(Intent(this,ProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this,"${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

}


