package com.infolitz.samplecartapp.repo

import com.infolitz.samplecartapp.helper.RetrofitService
import com.infolitz.samplecartapp.modals.agentSignUP.ModalAgentRegister

class AgentRegisterRepository constructor(private val retrofitService: RetrofitService) {
    fun getAgentRegister(profileReq: ModalAgentRegister) =
        retrofitService.agentRegister(profileReq)
}