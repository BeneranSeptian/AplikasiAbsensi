package kki.seftian.aplikasiabsensi

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kki.seftian.aplikasiabsensi.databinding.ActivityAbsenBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AbsenActivity : AppCompatActivity() {
    private var absenApa =""
    private var waktu =""
    private var ngapain = ""
    private lateinit var progressDialog : ProgressDialog


    private lateinit var binding: ActivityAbsenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        val getDate = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMMM yyyy")
        val formatedDate = sdf.format(getDate)
        binding.tvTglJam.text= formatedDate



        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setMessage("Lagi submit absen..")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.rgAbsenApa.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener{group, checkedId ->
                val radio : RadioButton = findViewById(checkedId)
                absenApa = radio.text.toString()

            })



        val ref = FirebaseDatabase.getInstance().getReference("Absensi")
        binding.btnSubmitAbsen.setOnClickListener() {
            progressDialog.show()
            val sdfPush = SimpleDateFormat("dd MMMM yyyy,  HH:mm")
            waktu = sdfPush.format(System.currentTimeMillis())

            var idRadio = binding.rgAbsenApa.checkedRadioButtonId
            this.waktu = waktu
            this.ngapain = this.binding.etNgapain.text.toString().trim()
            val uid = firebaseAuth.uid

            if(idRadio == null || ngapain==null){
                Toast.makeText(this, "Mohon diisi dengan lengkap", Toast.LENGTH_SHORT).show()
            }


            val key =ref.child(uid!!).push().key.toString()
            val absensi = DataAbsen(absenApa=absenApa , waktu=waktu, ngapain = ngapain, uid, key)
            ref.child(uid!!).child(key).setValue(absensi)
                .addOnSuccessListener {
                    progressDialog.dismiss()

                    Toast.makeText(
                        this,
                        "Absen Berhasil",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(this, RiwayatAbsenActivity::class.java))


                }

                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Absen gagal, ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                }

        }


        setContentView(binding.root)
    }


}

