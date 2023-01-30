package com.infolitz.samplecartapp.viewModel.agentSignUP

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.infolitz.samplecartapp.repo.AgentRegisterRepository

class AgentRegisterViewModelFactory constructor(private val repository: AgentRegisterRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AgentRegisterViewModel::class.java)) {
            AgentRegisterViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}