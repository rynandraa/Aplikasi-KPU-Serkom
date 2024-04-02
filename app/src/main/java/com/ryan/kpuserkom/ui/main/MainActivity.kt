package com.ryan.kpuserkom.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ryan.kpu.databinding.ActivityMainBinding
import com.ryan.kpuserkom.ui.datapemilih.DataPemilihActivity
import com.ryan.kpuserkom.ui.form.FormActivity
import com.ryan.kpuserkom.ui.info.InformasiActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener untuk tombol "Informasi", membuka aktivitas Informasi
        binding.buttonInfo.setOnClickListener {
            val intent = Intent(this, InformasiActivity::class.java)
            startActivity(intent)
        }

        // Listener untuk tombol "Form Entry", membuka aktivitas FormEntry
        binding.buttonForm.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }

        // Listener untuk tombol "Lihat Data", membuka aktivitas DaftarDataPemilih
        binding.buttonDataPemilih.setOnClickListener {
            val intent = Intent(this, DataPemilihActivity::class.java)
            startActivity(intent)
        }

        // Listener untuk tombol "Keluar", menutup aplikasi
        binding.buttonLogout.setOnClickListener {
            finish() // Menutup aktivitas (keluar dari aplikasi)
        }
    }
}