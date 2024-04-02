package com.skripsi.kpu.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Menandai kelas sebagai database Room dengan tabel entitas DataPemilih dan versi skema database.
@Database(entities = [DataPemilih::class], version = 1)
abstract class DataPemilihRoomDatabase : RoomDatabase() {

    // Mendeklarasikan fungsi abstrak untuk mendapatkan DAO yang terkait dengan database ini.
    abstract fun datapemilihDao(): DataPemilihDao

    // Mendefinisikan sebuah companion object untuk mengimplementasikan singleton pattern.
    companion object {
        // Variabel INSTANCE bersifat volatile untuk memastikan perubahan instance terlihat dengan baik oleh semua thread.
        @Volatile
        private var INSTANCE: DataPemilihRoomDatabase? = null

        // Fungsi untuk mendapatkan instance dari database.
        @JvmStatic
        fun getDatabase(context: Context): DataPemilihRoomDatabase {
            // Memeriksa apakah instance sudah ada; jika tidak, membuat instance baru.
            if (INSTANCE == null) {
                synchronized(DataPemilihRoomDatabase::class.java) {
                    // Membuat database baru jika belum ada dengan menggunakan Room database builder.
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        DataPemilihRoomDatabase::class.java, "datapemilih_database")
                        .build()
                }
            }
            // Mengembalikan instance database.
            return INSTANCE as DataPemilihRoomDatabase
        }
    }
}
