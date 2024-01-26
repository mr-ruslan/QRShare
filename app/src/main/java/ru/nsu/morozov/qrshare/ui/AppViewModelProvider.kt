package ru.nsu.morozov.qrshare.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.nsu.morozov.qrshare.QRShareApplication
import ru.nsu.morozov.qrshare.ui.get.GetViewModel
import ru.nsu.morozov.qrshare.ui.history.HistoryViewModel
import ru.nsu.morozov.qrshare.ui.history.SessionInfoViewModel
import ru.nsu.morozov.qrshare.ui.share.ShareViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ShareViewModel(qrShareApplication().container.trackerRepository)
        }
        initializer {
            HistoryViewModel(qrShareApplication().container.trackerRepository)
        }
        initializer {
            SessionInfoViewModel(qrShareApplication().container.trackerRepository)
        }
        initializer {
            GetViewModel(qrShareApplication().container.trackerRepository)
        }

    }
}

fun CreationExtras.qrShareApplication(): QRShareApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as QRShareApplication)
