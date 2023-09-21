package com.mcdane.box

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import java.net.ServerSocket
import java.net.Socket

class BoxServer(
    private val nsdManager: NsdManager,
    private val listener: BoxListener
) {
    companion object {
        const val SERVICE_TYPE = "_mybox._tcp"
        const val SERVICE_NAME = "MyBoxService"
    }

    private var serviceName: String? = null
    private var serverSocket: ServerSocket? = null

    private val registrationListener = object: NsdManager.RegistrationListener {
        override fun onServiceRegistered(service: NsdServiceInfo?) {
            service?.let{
                serviceName = it.serviceName
                waitForClient()
                Log.i(TAG, "Service registered as $serviceName")
            }
        }

        override fun onRegistrationFailed(service: NsdServiceInfo?, errorCode: Int) {
            val msg = "Registration for ${service?.serviceName} failed. errorCode=$errorCode"
            Log.e(TAG, msg)
            listener.onFailure(msg)
        }

        override fun onServiceUnregistered(service: NsdServiceInfo?) {
            Log.i(TAG, "Service ${service?.serviceName} unregistered")
        }

        override fun onUnregistrationFailed(service: NsdServiceInfo?, errorCode: Int) {
            Log.e(TAG, "Failed to unregister ${service?.serviceName}. errorCode=$errorCode")
        }
    }

    fun start() {
        if (serverSocket != null) {
            val msg = "BoxServer not stopped"
            Log.e(TAG, msg)
            listener.onFailure(msg)
        }

        try {
            Log.i(TAG, "Starting BoxServer")
            serverSocket = ServerSocket(0).apply{
                registerService(localPort)
            }
        } catch(e: Exception) {
            onException(e)
        }
    }

    fun stop() {
        serverSocket?.close()
        serverSocket = null
        Log.i(TAG, "BoxServer stopped")
    }

    private fun registerService(serverPort: Int) {
        val service = NsdServiceInfo().apply{
            serviceType = SERVICE_TYPE
            serviceName = SERVICE_NAME
            port = serverPort
        }
        nsdManager.registerService(service, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    private fun waitForClient() {
        Thread{
            Log.i(TAG, "Waiting for client")
            try {
                serverSocket?.apply {
                    val socket = accept()
                    Log.i(TAG, "Client accepted: ${socket.inetAddress.hostAddress}:${socket.port}")
                    listener.onSuccess(BoxCommunicator(socket))
                }
            } catch (e: Exception) {
                onException(e)
            }
        }.start()
    }

    private fun onException(e: Exception) {
        val msg = "Exception happened: ${e.localizedMessage}"
        Log.e(TAG, msg)
        listener.onFailure(msg)
    }

}