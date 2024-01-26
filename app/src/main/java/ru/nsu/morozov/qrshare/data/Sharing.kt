package ru.nsu.morozov.qrshare.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.nsu.morozov.qrshare.R

@Entity(
    foreignKeys = [ForeignKey(
        entity = Session::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("sid"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sharing(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val sid: String,
    val sentAt: Long,
    val data: String
)

