package com.infolitz.samplecartapp.viewModel.agentAuth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.infolitz.samplecartapp.repo.AgentLoginRepository

class AgentLoginViewModelFactory constructor(private val repository: AgentLoginRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(AgentLoginViewModel::class.java)) {
            AgentLoginViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }

}