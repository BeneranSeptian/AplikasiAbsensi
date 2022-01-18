package kki.seftian.aplikasiabsensi

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kki.seftian.aplikasiabsensi.databinding.ActivityDaftarBinding
import java.text.SimpleDateFormat
import java.util.*

class DaftarActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDaftarBinding
    private lateinit var progressDialog: ProgressDialog
    private var nama =""
    private var nim = ""
    private var asal = ""
    private var tglMasuk = ""
    private var tglKeluar = ""
    private var email = ""
    private var password = ""
    private var cPassword=""



    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        val cal = Calendar.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setMessage("Lagi Daftar..")
        progressDialog.setCanceledOnTouchOutside(false)

        val setTglMasuk =
            DatePickerDialog.OnDateSetListener { view, yearMasuk, monthOfYearMasuk, dayOfMonthMasuk ->
                cal.set(Calendar.YEAR, yearMasuk)
                cal.set(Calendar.MONTH, monthOfYearMasuk)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonthMasuk)

                val myFormat = "dd MMMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.tvMasuk.text = sdf.format(cal.time)


            }

        val setTglKeluar =

                DatePickerDialog.OnDateSetListener { view, yearKeluar, monthOfYearKeluar, dayOfMonthKeluar ->
                    cal.set(Calendar.YEAR, yearKeluar)
                    cal.set(Calendar.MONTH, monthOfYearKeluar)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonthKeluar)

                    val myFormat = "dd MMMM yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    binding.tvKeluar.text = sdf.format(cal.time)

            }

        binding.tvMasuk.setOnClickListener{
            DatePickerDialog(
                this, setTglMasuk,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.tvKeluar.setOnClickListener{
            DatePickerDialog(
                this, setTglKeluar,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()


        }
        binding.btnSave.setOnClickListener(){
            validasiData()

        }

        setContentView(binding.root)
    }

    private fun validasiData() {
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        nama = binding.etNama.text.toString().trim()
        nim = binding.etNim.text.toString().trim()
        asal = binding.etAsal.text.toString().trim()
        tglMasuk= binding.tvMasuk.text.toString().trim()
        tglKeluar = binding.tvKeluar.text.toString().trim()
        cPassword = binding.etConfirmPassword.toString().trim()

        if(email.isEmpty() || password.isEmpty() || nama.isEmpty() || nim.isEmpty() ||
            asal.isEmpty() || tglMasuk.isEmpty() || tglKeluar.isEmpty() || cPassword.isEmpty())
        {
            Toast.makeText(this,"Mohon lengkapi data Anda", Toast.LENGTH_SHORT).show()
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Format Email salah!"
        }

        else if(password.length <6 ){
            binding.etPassword.error = "Password minimal 6 karakter"
        }

        else if(binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()){
            binding.etConfirmPassword.error = "Password tidak sama"
        }
        else{
            daftarinLogin()
        }




    }

    private fun daftarinLogin() {
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
            Toast.makeText(this,"Berhasil bikin akun untuk login",Toast.LENGTH_SHORT).show()
            masukinInfoUser()
        }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to make an account due to ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun masukinInfoUser() {
        val timestamp = System.currentTimeMillis()

        //dapetin user id
        val uid = firebaseAuth.uid




        val ref = FirebaseDatabase.getInstance().getReference("Users")
        //masukin datanya

        val pendaftar = DataPendaftaran(
            uid, nama = nama,
            asal = asal,
            nim = nim,
            tglMasuk = tglMasuk,
            tglKeluar = tglKeluar,
            email = email,
            profile = "")


        ref.child(uid!!)
            .setValue(pendaftar)
            .addOnSuccessListener {
                progressDialog.dismiss()

                Toast.makeText(
                    this,
                    "Pendaftaran Berhasil, Selamat Bergabung!",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(Intent(this, DashboardActivity::class.java))

            }

            .addOnFailureListener{
                Toast.makeText(
                    this,
                    "Pendaftaran gagal, ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}