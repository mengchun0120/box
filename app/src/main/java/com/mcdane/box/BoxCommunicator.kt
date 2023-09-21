package com.mcdane.box

import android.util.Log
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class BoxCommunicator(private val socket: Socket) {

    interface Listener {
        fun onMessageReceived(msg: BoxMessage)

        fun onFailure(msg: String)
    }

    companion object {
        const val OUTPUT_QUEUE_LEN = 5
        const val TAG = "BoxCommunicator"
    }

    private val inBuffer = BoxMessage()
    private val outBuffer = BoxMessage()
    private var outAction: (()->Unit)? = null
    private val outBufferFull = AtomicBoolean(false)
    private val running = AtomicBoolean(true)

    var listener: Listener? = null

    val outBufferReady: Boolean
        get() = !outBufferFull.get()

    val isRunning: Boolean
        get() = running.get()

    init {
        startReaderThread()
        startWriterThread()
    }

    fun send(msg: BoxMessage, action: ()->Unit): Boolean =
        when {
            !running.get() -> {
                Log.e(TAG, "Communicator not running")
                false
            }
            outBufferFull.get() -> {
                Log.e(TAG, "Output buffer is full")
                false
            }
            else -> {
                outAction = action
                outBuffer.copy(msg)
                outBufferFull.set(true)
                true
            }
        }

    fun stop() {
        running.set(false)
        socket.close()
    }

    private fun startReaderThread() {
        Thread {
            try {
                val reader = socket.getInputStream()
                reader.use { stream ->
                    while (running.get()) {
                        readInBuffer(stream)
                    }
                }
            } catch (e: Exception) {
                if (running.get()) {
                    val msg = "Exception happened: ${e.localizedMessage}"
                    Log.e(TAG, msg)
                    listener?.onFailure(msg)
                }
            }
        }.start()
    }

    private fun startWriterThread() {
        Thread{
            try {
                val writer = socket.getOutputStream()
                writer.use { stream ->
                    while (running.get()) {
                        writeOutBuffer(stream)
                    }
                }
            } catch (e: Exception) {
                if (running.get()) {
                    val msg = "Exception happened: ${e.localizedMessage}"
                    Log.e(TAG, msg)
                    listener?.onFailure(msg)
                }
            }
        }.start()
    }

    private fun readInBuffer(stream: InputStream) {
        inBuffer.apply {
            read(stream)
            listener?.onMessageReceived(this)
        }
    }

    private fun writeOutBuffer(stream: OutputStream) {
        if (outBufferFull.get()) {
            outBuffer.write(stream)
            outBuffer.clear()
            outAction?.invoke()
            outBufferFull.set(false)
        }
    }
}