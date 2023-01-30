package com.infolitz.samplecartapp.modals.agentSignUP

data class ModalAgentRegister( //one that is passed to api
    val username: String,
    val address: String,
    val pincode: Int,
    val email: String,
    val password: String,
    val phone: Int
)