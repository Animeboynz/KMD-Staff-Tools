package com.animeboynz.kmd

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TransferRepository(private val transferDao: TransferDao) {
    val allTransfers = transferDao.getAllTransfers()

    suspend fun insertTransfers(vararg transfers: Transfer) {
        transferDao.insertTransfers(*transfers)
    }
}

class TransferViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransferRepository
    val allTransfers: LiveData<List<Transfer>>

    init {
        val transferDao = AppDatabase.getDatabase(application).transferDao()
        repository = TransferRepository(transferDao)
        allTransfers = repository.allTransfers.asLiveData()
    }

    fun insertTransfers(vararg transfers: Transfer) = viewModelScope.launch {
        repository.insertTransfers(*transfers)
    }
}
