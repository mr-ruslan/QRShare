package ru.nsu.morozov.qrshare.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class RoomSharingsRepository(
    private val sessionDao: SessionDao,
    private val sharingDao: SharingDao
) : SharingsRepository {
    override val sessionStream: Flow<List<Session>> = sessionDao.getAll()
    override fun getSession(sid: String): Flow<Session?> = sessionDao.get(sid)

    override fun getSharingStream(sid: String): Flow<List<Sharing>> = sharingDao.getAll(sid)
    override fun getSharingCount(sid: String): LiveData<Int> = sharingDao.getCount(sid)

    override suspend fun addSession(session: Session) = sessionDao.insert(session)
    override suspend fun deleteSession(session: Session) = sessionDao.delete(session)
    override suspend fun deleteSession(sid: String) = sessionDao.deleteBySid(sid)

    override suspend fun updateSession(session: Session) = sessionDao.update(session)

    override suspend fun addSharing(sharing: Sharing) = sharingDao.insert(sharing)
    override suspend fun deleteSharing(sharing: Sharing) = sharingDao.delete(sharing)
}