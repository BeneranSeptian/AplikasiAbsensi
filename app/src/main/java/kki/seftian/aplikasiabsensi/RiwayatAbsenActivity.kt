package kki.seftian.aplikasiabsensi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kki.seftian.aplikasiabsensi.databinding.ActivityRiwayatAbsenBinding

class RiwayatAbsenActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRiwayatAbsenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var absensiArrayList: ArrayList<ModelAbsensi>
    private lateinit var adapterAbsensi: AdapterAbsensi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatAbsenBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        loadAbsen()
        setContentView(binding.root)
    }

    private fun loadAbsen() {
        absensiArrayList = ArrayList()
        val id = firebaseAuth.uid.toString().trim()

        val ref = FirebaseDatabase.getInstance().getReference("Absensi").child("$id")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                absensiArrayList.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ModelAbsensi::class.java)
                    absensiArrayList.add(model!!)
                    val llm = LinearLayoutManager(this@RiwayatAbsenActivity)
                    llm.orientation = LinearLayoutManager.VERTICAL
                    adapterAbsensi = AdapterAbsensi(this@RiwayatAbsenActivity, absensiArrayList = absensiArrayList)
                    binding.rvAbsen.adapter = adapterAbsensi
                    binding.rvAbsen.layoutManager=llm


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}