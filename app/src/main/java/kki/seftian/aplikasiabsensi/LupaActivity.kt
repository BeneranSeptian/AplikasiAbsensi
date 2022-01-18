package kki.seftian.aplikasiabsensi

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kki.seftian.aplikasiabsensi.databinding.ActivityLupaBinding

class LupaActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLupaBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var  progressDialog: ProgressDialog

    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLupaBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnSubmit.setOnClickListener(){
            validasiEmail()
        }

        setContentView(binding.root)


    }

    private fun validasiEmail() {
        email = binding.etEmailLupa.text.toString().trim()

        if(email.isEmpty()){
            Toast.makeText(this, "Mohon isi emailnya", Toast.LENGTH_SHORT).show()
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Masukin format email yg bener", Toast.LENGTH_SHORT).show()
        }
        else{
            gantiPassword()
        }
    }

    private fun gantiPassword() {

        progressDialog.setMessage("Mengirim instruksi buat reset password ke $email")
        progressDialog.show()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Instruksi terkirim, silakan cek email kamu", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity:: class.java))

            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this, "Failed because ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}