package ru.nsu.morozov.qrshare.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.nsu.morozov.qrshare.R

@Entity
data class Session(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String?,
    val createdAt: Long,
)