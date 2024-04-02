package com.ryan.kpuserkom.ui.form

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ryan.kpu.R
import com.ryan.kpu.databinding.ActivityFormBinding
import com.ryan.kpuserkom.ViewModelFactory
import com.ryan.kpuserkom.ui.camera.CameraActivity
import com.ryan.kpuserkom.ui.camera.rotateBitmap
import com.ryan.kpuserkom.ui.camera.uriToFile
import com.skripsi.kpu.database.DataPemilih
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Calendar

class FormActivity : AppCompatActivity() {
    // Deklarasi variabel, konstanta, dan objek yang digunakan dalam kelas
    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val RESULT_ADD = 101
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
        const val CAMERA_X_RESULT = 200

        private var getFile: File? = null
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_MAPS = 1001
    }

    // Deklarasi variabel dan objek yang akan digunakan
    private var isEdit = false
    private var datapemilih: DataPemilih? = null
    private lateinit var formEntryViewModel: FormViewModel

    private var _activityFormEntryBinding: ActivityFormBinding? = null
    private val binding get() = _activityFormEntryBinding

    private lateinit var selectedDate: String

    // Fungsi yang dipanggil ketika Activity dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi view binding
        _activityFormEntryBinding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        // Inisialisasi ViewModel
        formEntryViewModel = obtainViewModel(this@FormActivity)
        // Pengecekan dan pengaturan mode Edit atau Tambah
        datapemilih = intent.getParcelableExtra(EXTRA_NOTE)
        if (datapemilih != null) {
            isEdit = true
        } else {
            datapemilih = DataPemilih()
        }

        // Set up ActionBar dan judul
        val actionBarTitle: String
        val btnTitle: String

        selectedDate = binding?.editTextTanggal?.text.toString()

        // Mengatur UI berdasarkan mode Edit atau Tambah
        if (isEdit) {
            // Konfigurasi UI untuk mode Edit
            actionBarTitle = "Ubah"
            btnTitle = "Update"
            if (datapemilih != null) {
                datapemilih?.let { datapemilih ->
                    binding?.editTextNIK?.setText(datapemilih.nik?.toString())
                    binding?.editTextNama?.setText(datapemilih.nama)
                    binding?.editTextNomorHP?.setText(datapemilih.nomorhp)
                    // Mengisi radio button jenis kelamin
                    when (datapemilih.jeniskelamin) {
                        "Laki-Laki" -> binding?.radioButtonLakiLaki?.isChecked = true
                        "Perempuan" -> binding?.radioButtonPerempuan?.isChecked = true
                        // Jika Anda ingin menangani nilai yang tidak sesuai
                        else -> {
                            // Tidak melakukan apapun atau mungkin memberikan nilai default
                        }
                    }
                    binding?.editTextTanggal?.setText(datapemilih.date)
                    binding?.editTextAlamat?.setText(datapemilih.alamat)
                    binding?.editTextLatitude?.setText(datapemilih.latitude.toString())
                    binding?.editTextLongitude?.setText(datapemilih.longitude.toString())
                    // Mengisi nilai gambar menggunakan Glide
                    if (datapemilih?.gambar != null) {
                        val bitmap = BitmapFactory.decodeByteArray(datapemilih?.gambar, 0, datapemilih?.gambar?.size ?: 0)
                        binding?.previewImageView?.setImageBitmap(bitmap)
                    } else {
                        Glide.with(applicationContext)
                            .load(R.drawable.baseline_image_24) // Ganti dengan resource ID placeholder yang sesuai
                            .into(binding?.previewImageView!!)
                    }
                }
            }
        } else {
            // Konfigurasi UI untuk mode Tambah
            actionBarTitle = "Tambah"
            btnTitle = "Submit"
        }

        // Set up ActionBar
        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Event listener untuk widget pada UI
        binding?.buttonTanggal?.setOnClickListener {
            showDatePicker()
        }

        // Permintaan permission untuk akses kamera
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        // Event listener untuk tombol kamera dan galeri
        binding?.btnCamera?.setOnClickListener { startCameraX() }
        binding?.btnGallery?.setOnClickListener { startGallery() }

        // Event listener untuk tombol cek lokasi
        binding?.btnCekLokasi?.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_MAPS) // Menggunakan requestCode yang telah didefinisikan
        }

        // Event listener untuk tombol submit
        binding?.btnSubmit?.text = btnTitle

        binding?.btnSubmit?.setOnClickListener {
            val nik = binding?.editTextNIK?.text.toString().trim()
            val nama = binding?.editTextNama?.text.toString().trim()
            val nomorhp = binding?.editTextNomorHP?.text.toString().trim()
            val jeniskelamin = when {
                binding?.radioButtonLakiLaki?.isChecked == true -> "Laki-Laki"
                binding?.radioButtonPerempuan?.isChecked == true -> "Perempuan"
                else -> {
                    Log.e("FormEntry", "Jenis Kelamin tidak valid")
                    ""
                }
            }
            val tanggal = selectedDate
            val alamat = binding?.editTextAlamat?.text.toString().trim()
            val latitude = binding?.editTextLatitude?.text.toString().trim()
            val longitude = binding?.editTextLongitude?.text.toString().trim()

            // Memanggil fungsi untuk mendapatkan data pemilih berdasarkan NIK
            formEntryViewModel.getDataPemilihByNIK(nik).observe(this) { existingDataPemilih ->
                if (existingDataPemilih != null && (!isEdit || existingDataPemilih.nik != datapemilih?.nik)) {
                    binding?.editTextNIK?.error = "NIK already exists"
                    showToast("NIK already exists")
                } else {
                    // Clear any previous errors
                    binding?.editTextNIK?.error = null

                    if (nik.length != 16) {
                        binding?.editTextNIK?.error = "NIK must be 16 digits"
                    } else {
                        // Clear any previous errors
                        binding?.editTextNIK?.error = null

                        if (nama.isEmpty()) {
                            binding?.editTextNama?.error = "Field can not be blank"
                        } else {
                            // Clear any previous errors
                            binding?.editTextNama?.error = null

                            if (nomorhp.isEmpty()) {
                                binding?.editTextNomorHP?.error = "Field can not be blank"
                            } else {
                                // Clear any previous errors
                                binding?.editTextNomorHP?.error = null

                                if (alamat.isEmpty()) {
                                    binding?.editTextAlamat?.error = "Field can not be blank"
                                } else {
                                    // Clear any previous errors
                                    binding?.editTextAlamat?.error = null

                                    // Lanjutkan dengan proses penyimpanan data
                                    datapemilih.let { datapemilih ->
                                        datapemilih?.nik = nik
                                        datapemilih?.nama = nama
                                        datapemilih?.nomorhp = nomorhp
                                        datapemilih?.jeniskelamin = jeniskelamin
                                        datapemilih?.date = tanggal
                                        datapemilih?.alamat = alamat
                                        datapemilih?.latitude = latitude.toDoubleOrNull()
                                        datapemilih?.longitude = longitude.toDoubleOrNull()
                                        if (getFile != null) {
                                            val imageByteArray = getFile?.readBytes()
                                            datapemilih?.gambar = imageByteArray
                                        }
                                    }

                                    if (isEdit) {
                                        formEntryViewModel.update(datapemilih as DataPemilih)
                                        showToast("Satu item berhasil diubah")
                                    } else {
                                        formEntryViewModel.insert(datapemilih as DataPemilih)
                                        showToast("Satu item berhasil ditambahkan")
                                    }
                                    finish()
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    // Fungsi untuk menampilkan DatePicker
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                selectedDate = "$year/${monthOfYear + 1}/$dayOfMonth"
                binding?.editTextTanggal?.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Fungsi untuk menangani hasil dari Activity lain
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Implementasi penanganan hasil Activity
        if (requestCode == REQUEST_CODE_MAPS && resultCode == RESULT_OK && data != null) {
            val latitude = data.getDoubleExtra("latitude", 0.0)
            val longitude = data.getDoubleExtra("longitude", 0.0)

            // Hanya mengatur nilai latitude dan longitude jika mereka valid (tidak 0.0)
            if (latitude != 0.0 && longitude != 0.0) {
                binding?.editTextLatitude?.setText(latitude.toString())
                binding?.editTextLongitude?.setText(longitude.toString())
            }
        }
    }

    // Fungsi untuk menampilkan Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk menampilkan menu opsi
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Implementasi menu
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    // Fungsi untuk menangani item menu yang dipilih
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Implementasi penanganan item menu
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    // Fungsi untuk menampilkan dialog konfirmasi
    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"
        } else {
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
            dialogTitle = "Hapus Data Pemilih"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton("Ya") { _, _ ->
                if (!isDialogClose) {
                    formEntryViewModel.delete(datapemilih as DataPemilih)
                    showToast("Satu item berhasil dihapus")
                }
                finish()
            }
            setNegativeButton("Tidak") { dialog, _ -> dialog.cancel() }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)

            val bytes = ByteArrayOutputStream()
            result.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(this@FormActivity.contentResolver, result, "Title", null)
            val uri = Uri.parse(path.toString())
            getFile = uriToFile(uri, this@FormActivity)

            binding?.previewImageView?.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@FormActivity)

            getFile = myFile

            binding?.previewImageView?.setImageURI(selectedImg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityFormEntryBinding = null
    }

    private fun obtainViewModel(activity: AppCompatActivity): FormViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(FormViewModel::class.java)
    }

    fun showDatePicker(view: View) {}
}