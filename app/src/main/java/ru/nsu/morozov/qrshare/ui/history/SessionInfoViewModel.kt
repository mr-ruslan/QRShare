package ru.nsu.morozov.qrshare.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.nsu.morozov.qrshare.data.Session
import ru.nsu.morozov.qrshare.data.Sharing
import ru.nsu.morozov.qrshare.data.SharingsRepository

class SessionInfoViewModel(private val repository: SharingsRepository) : ViewModel() {

    fun getSharingStream(sid:String) : Flow<List<Sharing>> = repository.getSharingStream(sid)

    fun deleteSharing(sharing:Sharing) = viewModelScope.launch {
        repository.deleteSharing(sharing)
    }

    fun updateSession(name: String) = viewModelScope.launch {
        _sessionInfo.value?.let{
            repository.updateSession(it.copy(name = name))
        }
    }
    private val _sessionInfo = MutableLiveData<Session?>(null)
    val sessionInfo: LiveData<Session?> = _sessionInfo

    fun setSessionInfo(session: Session) {
        _sessionInfo.value = session
    }

    fun getSession(sid: String) : Flow<Session?> = repository.getSession(sid)
    fun deleteSession(sid: String) = viewModelScope.launch {
        repository.deleteSession(sid)
    }
    fun getSharingCount(sid: String): LiveData<Int> = repository.getSharingCount(sid)

}