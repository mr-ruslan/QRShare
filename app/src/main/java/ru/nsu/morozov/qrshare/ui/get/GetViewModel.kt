package ru.nsu.morozov.qrshare.ui.get

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.nsu.morozov.qrshare.R
import ru.nsu.morozov.qrshare.data.Session
import ru.nsu.morozov.qrshare.data.Sharing
import ru.nsu.morozov.qrshare.data.SharingsRepository
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetViewModel(private val repository: SharingsRepository) : ViewModel() {

    fun saveSession(sid: String) = viewModelScope.launch {
        val t = System.currentTimeMillis()
        val name = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(t))
        try {
            repository.addSession(Session(sid, "Get at $name", t))
        }
        catch (e: Exception){

        }
    }

    fun saveSharing(sid: String, data: String) = viewModelScope.launch {
        repository.addSharing(Sharing(0, sid, System.currentTimeMillis(), data))
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is get Fragment"
    }
    val text: LiveData<String> = _text

    private val _data = MutableLiveData<String>().apply {
        value = ""
    }
    val data: LiveData<String> = _data

    fun setData(data: String) {
        _data.value = data
    }


    private val _sid = MutableLiveData<String>().apply {
        value = ""
    }
    val sid: LiveData<String> = _sid

    fun setSid(sid: String) {
        _sid.value = sid
    }

    private var webSocket: Socket? = null
    private var connected = false

    fun startWebSocketConnection(
        url: String,
        onConnected: (Boolean) -> Unit,
        onShareId: (String) -> Unit,
        onShared: (String) -> Unit,
        onClosed: () -> Unit
    ) {
        if (webSocket == null) {
            webSocket = IO.socket(url).apply {
                this.on(Socket.EVENT_CONNECT) {
                    connected = true
                    onConnected(true)
                }

                this.on("share_id") { args ->
                    val shareId = args[1].toString()
                    onShareId(shareId)
                }

                this.on("text_sent") { args ->
                    val message = args[0].toString()
                    print(message)
                    val json = Json.parseToJsonElement(message).jsonObject
                    onShared(json["text"]?.jsonPrimitive?.content ?: "invalid message")
                }
                this.on(Socket.EVENT_CONNECT_ERROR) {
                    this.disconnect()
                    webSocket = null
                    connected = false
                    onConnected(false)
                }
                this.on(Socket.EVENT_DISCONNECT) {
                    webSocket = null
                    connected = false
                    onClosed()
                }
                this.connect()
            }

        }
    }

    fun closeWebSocketConnection() {
        webSocket?.close()
        connected = false
        webSocket = null
    }

    fun connected() : Boolean = webSocket != null && connected

    fun shared() : Boolean = _data.value != ""
}