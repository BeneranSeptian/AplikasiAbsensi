package kki.seftian.aplikasiabsensi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kki.seftian.aplikasiabsensi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.btnLogin.setOnClickListener(){
            startActivity(Intent(this, LoginActivity ::class.java))
        }
        setContentView(binding.root)
    }
}