package kki.seftian.aplikasiabsensi

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.net.UrlQuerySanitizer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

import kki.seftian.aplikasiabsensi.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var  progressDialog : ProgressDialog

    private var imageUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)

//        var nama = intent.getStringExtra("nama")
//        var asal = intent.getStringExtra("asal")
//        var nim = intent.getStringExtra("nim")
//        var profile= intent.getStringExtra("profile")


//        binding.etnama.setText(nama)
//        binding.etasal.setText(asal)
//        binding.etnim.setText(nim)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUser()



        binding.ivProfile.setOnClickListener(){
            showImageMenu()

        }

        binding.btnUpdateProfile.setOnClickListener(){
            validateData()
        }

        setContentView(binding.root)


    }

    private var nama = ""
    private var asal = ""
    private var  nim = ""

    private fun validateData() {
        nama = binding.etnama.text.toString().trim()
        asal = binding.etasal.text.toString().trim()
        nim = binding.etnim.text.toString().trim()

        if(nama.isEmpty() || asal.isEmpty() || nim.isEmpty()){
            Toast.makeText(this, "lengkapin datanya", Toast.LENGTH_SHORT).show()
        }
        else{
            if(imageUri==null){
                updateProfile("")
            }
            else{
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Lagi aplod foto profil")
        progressDialog.show()

        val filePathAndName = "ProfileImages/"+firebaseAuth.uid
        
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener {taskSnapshot->
                progressDialog.dismiss()
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadImageUrl = "${uriTask.result}"
                updateProfile(uploadImageUrl)
            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this,"gagal aplod gambar karena ${it.message}", Toast.LENGTH_SHORT).show()
            }


    }

    private fun updateProfile(uploadedImageURL: String) {
        progressDialog.setMessage("lagi apdet profil...")
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["nama"]= "$nama"
        hashMap["asal"]="$asal"
        hashMap["nim"]="$nim"
        val array = arrayListOf<String>("Septian", "Beneran")
        array[0]="MrGorilla"

                if(imageUri !=null){
                    hashMap["profile"] = uploadedImageURL

                }
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"berhasil apdet profil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))

            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this,"gagal apdet profile karena ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }


    private fun loadUser() {
        //progressDialog.show()
        //firebaseAuth = FirebaseAuth.getInstance()
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


                    binding.etnama.setText(nama)
                    binding.etasal.setText(asal)
//                    binding.tvMasuk.text=masuk
//                    binding.tvKeluar.text=keluar
                    binding.etnim.setText(nim)
                    //profile1 = profile
                    //progressDialog.dismiss()

                    try{
                        Glide.with(this@EditProfileActivity)
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

    private fun showImageMenu(){
        val popup= PopupMenu(this,binding.ivProfile)
        popup.menu.add(Menu.NONE, 0, 0, "Camera")
        popup.menu.add(Menu.NONE, 1, 1, "Gallery")
        popup.show()

        popup.setOnMenuItemClickListener {item ->

            val id = item.itemId
            if(id===0){
                pickImageCamera()
            }
            else{
                pickImageGallery()

            }

            true

        }
    }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback <ActivityResult>{result->

            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                //imageUri = data!!.data

                binding.ivProfile.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{result->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data

                binding.ivProfile.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }

        }
    )

}