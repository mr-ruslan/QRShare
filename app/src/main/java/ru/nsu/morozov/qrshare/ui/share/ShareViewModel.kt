package ru.nsu.morozov.qrshare.ui.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nsu.morozov.qrshare.data.Session
import ru.nsu.morozov.qrshare.data.Sharing
import ru.nsu.morozov.qrshare.data.SharingsRepository
import java.lang.Exception
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShareViewModel(private val repository: SharingsRepository) : ViewModel() {

    fun saveSession(sid: String) = viewModelScope.launch {
        val t = System.currentTimeMillis()
        val name = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(t))
        try {
            repository.addSession(Session(sid, "Share at $name", t))
        }
        catch (e: Exception){

        }
    }

    fun saveSharing(sid: String, data: String) = viewModelScope.launch {
        repository.addSharing(Sharing(0, sid, System.currentTimeMillis(), data))
    }

    private val _shareId = MutableLiveData<String>().apply {
        value = "Scan the qr code first"
    }
    val shareId: LiveData<String> get() = _shareId

    private val _scanned = MutableLiveData<Boolean>().apply {
        value = false
    }
    val scanned: LiveData<Boolean> get() = _scanned

    private val _shareData = MutableLiveData<String>().apply {
        value = ""
    }
    val shareData: LiveData<String> get() = _shareData

    fun setShareId(id: String) {
        if (id != "") {
            _shareId.value = id
            _scanned.value = true;
        }
        else {
            reset()
        }
    }

    fun setShareData(data: String) {
        _shareData.value = data
    }

    private fun reset() {
        _shareId.value = "Scan the qr code first"
        _scanned.value = false;
    }
}