package com.infolitz.samplecartapp.repo

import com.infolitz.samplecartapp.helper.RetrofitService
import com.infolitz.samplecartapp.modals.agentAuth.ModalAgentLogin

class AgentLoginRepository constructor(private val retrofitService: RetrofitService) {
    fun getProfile(profileReq: ModalAgentLogin) =
        retrofitService.getAuthAgentLogin(profileReq)
}