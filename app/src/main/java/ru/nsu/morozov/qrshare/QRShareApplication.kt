package ru.nsu.morozov.qrshare

import android.app.Application
import ru.nsu.morozov.qrshare.data.AppContainer
import ru.nsu.morozov.qrshare.data.AppDataContainer

class QRShareApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
