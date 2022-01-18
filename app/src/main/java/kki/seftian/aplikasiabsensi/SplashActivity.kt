package kki.seftian.aplikasiabsensi

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(Runnable {
            startActivity(Intent(this, LoginActivity::class.java))
            checkUser()
        },2000)
    }
    val firebaseAuth = FirebaseAuth.getInstance()

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

            Toast.makeText(this, "Silahkan Login ", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}