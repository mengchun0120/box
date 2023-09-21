package com.mcdane.box

interface BoxListener {
    fun onSuccess(communicator: BoxCommunicator)
    fun onFailure(errMsg: String)
}