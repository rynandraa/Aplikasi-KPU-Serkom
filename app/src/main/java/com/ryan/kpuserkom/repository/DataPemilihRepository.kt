package com.ryan.kpuserkom.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.skripsi.kpu.database.DataPemilih
import com.skripsi.kpu.database.DataPemilihDao
import com.skripsi.kpu.database.DataPemilihRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Definisi kelas DataPemilihRepository
class DataPemilihRepository(application: Application) {
    // Deklarasi dan inisialisasi DataPemilihDao
    private val mDataPemilihDao: DataPemilihDao

    // ExecutorService untuk menjalankan operasi database secara asinkron
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    // Blok inisialisasi untuk mendapatkan instance database dan DAO
    init {
        val db = DataPemilihRoomDatabase.getDatabase(application)
        mDataPemilihDao = db.datapemilihDao()
    }

    // Fungsi untuk mendapatkan semua data pemilih
    fun getAllDataPemilih(): LiveData<List<DataPemilih>> = mDataPemilihDao.getAllDataPemilih()

    // Fungsi untuk menyisipkan data pemilih ke dalam database
    fun insert(datapemilih: DataPemilih) {
        executorService.execute { mDataPemilihDao.insert(datapemilih) }
    }

    // Fungsi untuk mendapatkan data pemilih berdasarkan NIK
    fun getDataPemilihByNIK(nik: String): LiveData<DataPemilih> {
        return mDataPemilihDao.getDataPemilihByNIK(nik)
    }

    // Fungsi untuk menghapus data pemilih dari database
    fun delete(datapemilih: DataPemilih) {
        executorService.execute { mDataPemilihDao.delete(datapemilih) }
    }

    // Fungsi untuk memperbarui data pemilih dalam database
    fun update(datapemilih: DataPemilih) {
        executorService.execute { mDataPemilihDao.update(datapemilih) }
    }
}