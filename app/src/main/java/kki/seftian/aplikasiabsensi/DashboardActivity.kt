package kki.seftian.aplikasiabsensi

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kki.seftian.aplikasiabsensi.databinding.ActivityDashboardBinding
import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var progressDialog : ProgressDialog
    var nim1 = ""
    var profile1 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setMessage("Lagi Load Data..")
        progressDialog.setCanceledOnTouchOutside(false)

        loadUser()

        binding.cardLogout.setOnClickListener() {
            logoutFunction()
        }

        binding.cardABSEN.setOnClickListener(){
            val intent = Intent(this, AbsenActivity :: class.java)
            intent.putExtra("nama", binding.tvUser.text.toString().trim())
            intent.putExtra("asal", binding.tvAsal.text.toString().trim())
            intent.putExtra("masuk", binding.tvMasuk.text.toString().trim())
            intent.putExtra("keluar", binding.tvKeluar.text.toString().trim())
            intent.putExtra("nim", nim1)
            intent.putExtra("profile", profile1)
            startActivity(intent)
        }
        binding.cardKehadiran.setOnClickListener(){
            startActivity(Intent(this, RiwayatAbsenActivity::class.java))
        }

        binding.ivEditProfile.setOnClickListener(){
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        setContentView(binding.root)
    }

    private fun loadUser() {
        progressDialog.show()
        firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseDatabase.getInstance().getReference("Users")
        db.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {


                override fun onDataChange(snapshot: DataSnapshot) {
                    val nama = "${snapshot.child("nama").value}"
                    val asal = "${snapshot.child("asal").value}"
                    val nim = "${snapshot.child("nim").value}"
                    val masuk = "${snapshot.child("tglMasuk").value}"
                    val keluar = "${snapshot.child("tglKeluar").value}"
                    val profile = "${snapshot.child("profile").value}"


//                    var simpleDateFormat = SimpleDateFormat("dd.LLL.yyyy")
//                    var dateTime = simpleDateFormat.format(masuk).toString()
//                    var tvMasuk= dateTime


                    binding.tvUser.text=nama
                    binding.tvAsal.text=asal
                    binding.tvMasuk.text=masuk
                    binding.tvKeluar.text=keluar
                    nim1 = nim
                    profile1 = profile
                    progressDialog.dismiss()

                    try{
                        Glide.with(this@DashboardActivity)
                            .load(profile)
                            .placeholder(R.drawable.profile)
                            .into(binding.ivProfile)

                    }
                    catch (e:Exception){


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun logoutFunction() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user not null, berarti user udh login
            val dialogBuilder = AlertDialog.Builder(this)

            dialogBuilder.setMessage("Yakin ingin Logout ?")
                .setCancelable(false)
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialog, id ->
                    firebaseAuth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))

                })
                // negative button text and action
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Logout")
            // show alert dialog
            alert.show()

        }
    }
}