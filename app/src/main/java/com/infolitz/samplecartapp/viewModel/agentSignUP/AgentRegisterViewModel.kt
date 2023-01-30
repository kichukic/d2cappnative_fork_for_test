package com.infolitz.samplecartapp.viewModel.agentSignUP

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.infolitz.samplecartapp.modals.agentSignUP.ModalAgentRegister
import com.infolitz.samplecartapp.modals.agentSignUP.AgentRegisterListData
import com.infolitz.samplecartapp.repo.AgentRegisterRepository
import retrofit2.Call
import retrofit2.Response

class AgentRegisterViewModel constructor(private val repository: AgentRegisterRepository) : ViewModel() {
    val dataList = MutableLiveData<AgentRegisterListData>()
    val errorMessage = MutableLiveData<String>()
    fun getAgentRegister(profileReq: MutableList<ModalAgentRegister>) {
        val mReqModel = ModalAgentRegister(
            profileReq[0].username,
            profileReq[0].address,
            profileReq[0].pincode,
            profileReq[0].email,
            profileReq[0].password,
            profileReq[0].phone
        )
        val response = repository.getAgentRegister(mReqModel)
        response.enqueue(object : retrofit2.Callback<AgentRegisterListData> {

            override fun onResponse(
                call: Call<AgentRegisterListData>,
                response: Response<AgentRegisterListData>
            ) {
                Log.e("truee", "onrepose sucess")
                dataList.postValue(response.body())
            }

            override fun onFailure(call: Call<AgentRegisterListData>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }
}