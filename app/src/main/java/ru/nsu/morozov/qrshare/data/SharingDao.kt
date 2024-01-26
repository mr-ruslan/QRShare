package ru.nsu.morozov.qrshare.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface SharingDao {
    @Query("SELECT * FROM sharing WHERE sid = :sid ORDER BY sentAt DESC")
    fun getAll(sid: String): Flow<List<Sharing>>

    @Query("SELECT COUNT(*) FROM sharing WHERE sid = :sid")
    fun getCount(sid: String): LiveData<Int>

    @Insert
    suspend fun insert(sharing: Sharing)

    @Delete
    suspend fun delete(sharing: Sharing)

    @Update
    suspend fun update(sharing: Sharing)
}