package com.mcdane.box

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import java.net.InetAddress
import java.net.Socket

class BoxClient(
    private val nsdManager: NsdManager,
    private val listener: BoxListener
) {

    companion object {
        const val TAG = "BoxClient"
    }

    fun start() {
        nsdManager.discoverServices(BoxServer.SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stop() {
        nsdManager.stopServiceDiscovery(discoveryListener)
    }

    private val discoveryListener = object: NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(serviceType: String?) {
            Log.i(TAG, "Started discovery for $serviceType")
        }

        override fun onServiceFound(service: NsdServiceInfo?) {
            service?.apply {
                Log.i(TAG, "onServiceFound: $this")

                if (!serviceType.startsWith(BoxServer.SERVICE_TYPE)) {
                    Log.w(TAG, "Unknown service $serviceType found")
                    return
                }

                if (serviceName.contains(BoxServer.SERVICE_NAME)){
                    Log.i(TAG, "Service discovery succeeded: $service")
                    nsdManager.resolveService(service, resolveListener)
                }
            }
        }

        override fun onServiceLost(service: NsdServiceInfo?) {
            Log.e(TAG, "Service lost: $service")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onDiscoveryStopped(serviceType: String?) {
            Log.i(TAG, "Discovery of $serviceType stopped")
        }

        override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
            Log.e(TAG, "Failed to start discovery of $serviceType. errorCode=$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
            Log.e(TAG, "Failed to stop discovery of $serviceType. errorCode=$errorCode")
        }
    }

    private val resolveListener = object: NsdManager.ResolveListener {
        override fun onServiceResolved(service: NsdServiceInfo?) {
            Log.i(TAG, "Service resolved for $service")
            service?.apply {
                connectToServer(host, port)
            }
        }

        override fun onResolveFailed(service: NsdServiceInfo?, errorCode: Int) {
            Log.e(TAG, "Failed to resolve service $service. errorCode=$errorCode")
        }
    }

    private fun connectToServer(addr: InetAddress, port: Int) {
        Thread{
            try {
                val socket = Socket(addr, port)
                Log.i(TAG, "Connected to ${addr.hostAddress}:$port")
                listener.onSuccess(BoxCommunicator(socket))
            } catch (e: Exception) {
                Log.e(TAG, "Exception happened when connecting to server: ${e.localizedMessage}")

            }
        }.start()
    }

    private fun onException(e: Exception) {
        val msg = "Exception happened: ${e.localizedMessage}"
        Log.e(com.mcdane.box.TAG, msg)
        listener.onFailure(msg)
    }
}