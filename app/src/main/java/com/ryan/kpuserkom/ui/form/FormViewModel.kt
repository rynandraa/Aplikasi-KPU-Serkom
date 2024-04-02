package com.ryan.kpuserkom.ui.form

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ryan.kpuserkom.repository.DataPemilihRepository
import com.skripsi.kpu.database.DataPemilih

class FormViewModel (application: Application) : AndroidViewModel(application){
    private val mDataPemilihRepository: DataPemilihRepository = DataPemilihRepository(application)

    fun insert(datapemilih: DataPemilih) {
        mDataPemilihRepository.insert(datapemilih)
    }

    fun update(datapemilih: DataPemilih) {
        mDataPemilihRepository.update(datapemilih)
    }

    fun getDataPemilihByNIK(nik: String): LiveData<DataPemilih> {
        return mDataPemilihRepository.getDataPemilihByNIK(nik)
    }

    fun delete(datapemilih: DataPemilih) {
        mDataPemilihRepository.delete(datapemilih)
    }
}