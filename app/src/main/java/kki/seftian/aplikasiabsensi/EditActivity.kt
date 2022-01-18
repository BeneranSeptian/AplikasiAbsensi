package kki.seftian.aplikasiabsensi

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kki.seftian.aplikasiabsensi.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private var absenApa =""
    private var ngapain1 = ""
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)

        var ngapain = intent.getStringExtra("ngapain")
        binding.tvWaktu.text=intent.getStringExtra("waktu")
        binding.etNgapain.setText(ngapain)


        binding.rgAbsenApa.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener{ group, checkedId ->
                val radio : RadioButton = findViewById(checkedId)
                absenApa = radio.text.toString()


            })
        binding.btnSubmitEditAbsen.setOnClickListener(){
            ngapain1 = binding.etNgapain.text.toString().trim()
            fungsiUpdate()

        }





        setContentView(binding.root)
    }

    private fun fungsiUpdate() {
        firebaseAuth = FirebaseAuth.getInstance()

        val id = firebaseAuth.uid.toString()
        val key = intent.getStringExtra("key")!!


        val ref = FirebaseDatabase.getInstance().getReference("Absensi")
        ref.child(id).child(key).child("ngapain").setValue(ngapain1!!)
            .addOnSuccessListener {
                Toast.makeText(this, "berhasil update ngapain aja", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this, "gagal karena ${it.message}", Toast.LENGTH_SHORT).show()
            }
        ref.child(id).child(key).child("absenApa").setValue(absenApa)
            .addOnSuccessListener {
                Toast.makeText(this, "berhasil update absenApa", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, RiwayatAbsenActivity::class.java))
            }
            .addOnFailureListener{
                Toast.makeText(this, "gagal karena ${it.message}", Toast.LENGTH_SHORT).show()
            }


    }
}