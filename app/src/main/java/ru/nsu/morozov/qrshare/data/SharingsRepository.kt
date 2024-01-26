package ru.nsu.morozov.qrshare.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Future

interface SharingsRepository {
    val sessionStream: Flow<List<Session>>

    fun getSession(sid: String): Flow<Session?>
    fun getSharingStream(sid: String): Flow<List<Sharing>>
    fun getSharingCount(sid: String): LiveData<Int>


    suspend fun addSession(session: Session)
    suspend fun deleteSession(session: Session)
    suspend fun deleteSession(sid: String)
    suspend fun updateSession(session: Session)

    suspend fun addSharing(sharing: Sharing)
    suspend fun deleteSharing(sharing: Sharing)
}