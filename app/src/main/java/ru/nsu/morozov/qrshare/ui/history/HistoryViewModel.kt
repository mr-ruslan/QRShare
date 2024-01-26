package ru.nsu.morozov.qrshare.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.nsu.morozov.qrshare.data.Session
import ru.nsu.morozov.qrshare.data.SharingsRepository

class HistoryViewModel(private val repository: SharingsRepository) : ViewModel() {

    val sessionStream: Flow<List<Session>> = repository.sessionStream

    fun deleteSession(session:Session) = viewModelScope.launch {
        repository.deleteSession(session)
    }

}