package com.example.parkingqr.ui.components.userinvoice

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserInvoiceViewModel @Inject constructor(private val repository: IRepository) :
    BaseViewModel() {

    private val _stateUi = MutableStateFlow(
        MyInvoiceState()
    )
    val stateUi: StateFlow<MyInvoiceState> = _stateUi.asStateFlow()
    private var getInvoiceListJob: Job? = null
    private var searchInvoiceListJob: Job? = null

    init {
        getParkingInvoiceList()
    }

    fun getParkingInvoiceList() {
        getInvoiceListJob?.cancel()
        getInvoiceListJob = viewModelScope.launch {
            repository.getUserParkingInvoiceList().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                invoiceList = state.data,
                                isLoading = false
                            )
                        }
                        Log.d("BUGGGG", state.data.size.toString())
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = state.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun searchParkingInvoice(licensePlate: String) {
        searchInvoiceListJob?.cancel()
        searchInvoiceListJob = viewModelScope.launch {
            repository.searchParkingInvoiceUser(licensePlate).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                invoiceList = state.data,
                            )
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = state.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun showError(){
        _stateUi.update {
            it.copy(
                error = ""
            )
        }
    }
    fun showMessage() {
        _stateUi.update {
            it.copy(
                message = ""
            )
        }
    }


    data class MyInvoiceState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val invoiceList: MutableList<ParkingInvoice> = mutableListOf()
    )
}