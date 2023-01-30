package com.infolitz.samplecartapp.viewModel.agentAuth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.infolitz.samplecartapp.modals.agentAuth.ModalAgentLogin
import com.infolitz.samplecartapp.modals.agentAuth.AgentLoginListData
import com.infolitz.samplecartapp.repo.AgentLoginRepository
import retrofit2.Call
import retrofit2.Response

class AgentLoginViewModel constructor(private val repository: AgentLoginRepository) : ViewModel() {
    val dataList = MutableLiveData<AgentLoginListData>()
    val errorMessage = MutableLiveData<String>()
    fun getProfile(profileReq: MutableList<ModalAgentLogin>) {
        val mReqModel = ModalAgentLogin(
            profileReq[0].username,
            profileReq[0].password,
        )
        val response = repository.getProfile(mReqModel)
        response.enqueue(object : retrofit2.Callback<AgentLoginListData> {

            override fun onResponse(
                call: Call<AgentLoginListData>,
                response: Response<AgentLoginListData>
            ) {
                Log.e("truee", "on repose success")
                dataList.postValue(response.body())
            }

            override fun onFailure(call: Call<AgentLoginListData>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }
}