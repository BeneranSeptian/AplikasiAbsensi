package kki.seftian.aplikasiabsensi

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kki.seftian.aplikasiabsensi.databinding.ItemAbsenBinding

class AdapterAbsensi : RecyclerView.Adapter<AdapterAbsensi.HolderAbsensi> {
    private lateinit var binding: ItemAbsenBinding
    private val context: Context
    private val absensiArrayList: ArrayList<ModelAbsensi>
    private lateinit var builder: AlertDialog.Builder
    private lateinit var firebaseAuth: FirebaseAuth


    constructor(context: Context, absensiArrayList: ArrayList<ModelAbsensi>) : super() {
        this.context = context
        this.absensiArrayList = absensiArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAbsensi {
        binding = ItemAbsenBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAbsensi(binding.root)

    }

    override fun onBindViewHolder(holder: HolderAbsensi, position: Int) {
        val model = absensiArrayList[position]
        val id = model.uid
        val absenApa = model.absenApa
        val waktu = model.waktu
        val ngapain = model.ngapain

        holder.ngapain.text = ngapain
        holder.waktuAbsen.text = waktu
        holder.absenApa.text = absenApa

        holder.btnDelete.setOnClickListener {
            builder = AlertDialog.Builder(context)
            builder.setTitle("Hapus")
                .setMessage("Yakin mau dihapus?")
                .setPositiveButton("Ya") { a, d ->
                    Toast.makeText(context, "Menghapus...", Toast.LENGTH_SHORT).show()
                    fungsiHapus(model, holder)

                }
                .setNegativeButton("Batal") { a, d ->
                    a.dismiss()
                }

            builder.create()
            builder.show()

            //fungsiHapus(model, holder)
        }
        holder.btnEdit.setOnClickListener() {
            //val dialogEdit = BottomSheetDialog(context)
            val waktuAbsen = holder.waktuAbsen.text.toString().trim()
            val ngapain = holder.ngapain.text.toString().trim()
            val key = model.key.toString()

            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra("waktu", waktuAbsen)
            intent.putExtra("ngapain", ngapain)
            intent.putExtra("key",key)

            startActivity(context, intent, null)

//            dialogEdit.setContentView(R.layout.activity_edit)
//            dialogEdit.show()
        }


    }


    private fun fungsiHapus(model: ModelAbsensi, holder: HolderAbsensi) {
        firebaseAuth = FirebaseAuth.getInstance()

        val id = firebaseAuth.uid.toString()
        val key = model.key

        val ref = FirebaseDatabase.getInstance().getReference("Absensi")

        ref.child(id).child(key).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Berhasil dihapus ", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener {

            }
    }

    override fun getItemCount(): Int {
        return absensiArrayList.size
    }


    inner class HolderAbsensi(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var waktuAbsen: TextView = binding.tvWaktuAbsen
        var ngapain: TextView = binding.tvNgapain
        var absenApa: TextView = binding.tvAbsenApa
        var btnDelete: ImageView = binding.ivDelete
        var btnEdit: ImageView = binding.ivEdit
    }


}