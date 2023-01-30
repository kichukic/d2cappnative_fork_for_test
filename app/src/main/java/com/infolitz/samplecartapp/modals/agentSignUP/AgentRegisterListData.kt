package com.infolitz.samplecartapp.modals.agentSignUP

import com.google.gson.annotations.SerializedName

data class AgentRegisterListData(

    @SerializedName("response") var response: String? = null,
    @SerializedName("username") var username: Boolean? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone") var phone: Int? = null,
    @SerializedName("pincode") var pincode: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("token") var token: String? = null

)
