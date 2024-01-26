package ru.nsu.morozov.qrshare.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface SessionDao {
    @Query("SELECT * FROM session ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Session>>

    @Query("SELECT * FROM session WHERE id = :id")
    fun get(id: String): Flow<Session?>

    @Insert
    suspend fun insert(session: Session)

    @Delete
    suspend fun delete(session: Session)

    @Query("DELETE FROM session WHERE id = :sid")
    suspend fun deleteBySid(sid: String)

    @Update
    suspend fun update(session: Session)
}