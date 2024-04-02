package com.ryan.kpuserkom.ui.datapemilih

import androidx.recyclerview.widget.DiffUtil
import com.skripsi.kpu.database.DataPemilih


// Definisi kelas DataPemilihDiffCallback yang menerapkan DiffUtil.Callback
class DataPemilihDiffCallback(
    private val oldDataPemilihList: List<DataPemilih>,
    private val newDataPemilihList: List<DataPemilih>
) : DiffUtil.Callback() {

    // Mengembalikan jumlah item dalam daftar data pemilih lama
    override fun getOldListSize(): Int = oldDataPemilihList.size

    // Mengembalikan jumlah item dalam daftar data pemilih baru
    override fun getNewListSize(): Int = newDataPemilihList.size

    // Memeriksa apakah item pada posisi tertentu di kedua daftar adalah 'item yang sama'
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldDataPemilihList[oldItemPosition].id == newDataPemilihList[newItemPosition].id
    }

    // Memeriksa apakah konten dari item pada posisi tertentu di kedua daftar adalah sama
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldDataPemilihList[oldItemPosition]
        val newItem = newDataPemilihList[newItemPosition]
        return oldItem.nik == newItem.nik
                && oldItem.nama == newItem.nama
                && oldItem.nomorhp == newItem.nomorhp
                && oldItem.jeniskelamin == newItem.jeniskelamin
                && oldItem.date == newItem.date
                && oldItem.alamat == newItem.alamat
                && oldItem.latitude == newItem.latitude
                && oldItem.longitude == newItem.longitude
                && oldItem.gambar.contentEquals(newItem.gambar)
    }
}
