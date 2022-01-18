package kki.seftian.aplikasiabsensi

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import kki.seftian.aplikasiabsensi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var pass = ""

    private lateinit var  progressDialog: ProgressDialog
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setMessage("Lagi LOGIN..")
        progressDialog.setCanceledOnTouchOutside(false)


        firebaseAuth = FirebaseAuth.getInstance()



        binding.btnLoginBeneran.setOnClickListener(){
            //validasi data sebelum login
            validateData()

        }
        binding.tvDaftar.setOnClickListener(){
            startActivity(Intent(this, DaftarActivity::class.java))
        }

        binding.tvLupa.setOnClickListener(){
            startActivity(Intent(this, LupaActivity::class.java))
        }


        setContentView(binding.root)
    }

    private fun validateData() {
        email = binding.etEmail.text.toString().trim()
        pass = binding.etPassword.text.toString().trim()

        //validasi
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //format email salah
            binding.etEmail.error = "Format emailnya salah"
        }
        else if(TextUtils.isEmpty(pass)){
            binding.etPassword.error="Masukin password"
        }
        else{
            fireBaseLogin()
        }
    }

    private fun fireBaseLogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener {
            //berhasil login
            progressDialog.dismiss()
            //dapetin info user
            val fireBaseUser = firebaseAuth.currentUser
            val imel = fireBaseUser!!.email
            Toast.makeText(this, "login sebagai $imel ", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, DashboardActivity::class.java))


        }
            .addOnFailureListener{e->
                //gagal login
                progressDialog.dismiss()
                Toast.makeText(this, "login gagal karena ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun checkUser() {
        //klo user udh login
        //dapetin user yang login
        val firebaseUser = firebaseAuth.currentUser
        val imel = firebaseUser!!.email
        if(firebaseUser !=null){
            //user udh login
            Toast.makeText(this, "login sebagai $imel ", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, DashboardActivity::class.java))
        }
        else{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}