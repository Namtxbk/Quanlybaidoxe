package com.example.parkingqr.ui.components.invoice

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
class InvoiceDetailViewModel @Inject constructor(private val repository: IRepository): BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        InvoiceDetailViewModelState()
    )
    val stateUi: StateFlow<InvoiceDetailViewModelState> = _stateUi.asStateFlow()
    private var getInvoiceJob: Job? = null
    private var saveInVoiceJob: Job? = null


    fun getInvoiceById(id: String) {
        getInvoiceJob?.cancel()
        getInvoiceJob = viewModelScope.launch {
            repository.getParkingInvoiceById(id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                invoice = state.data[0],
                                isLoading = false
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
    fun saveInvoice(_type: String, _paymentMethod: String, _note: String ) {
        saveInVoiceJob?.cancel()
        _stateUi.update {
            it.copy(
                invoice = it.invoice?.apply {
                    type = _type
                    paymentMethod = _paymentMethod
                    note = _note
                }
            )
        }
        saveInVoiceJob = viewModelScope.launch {
            repository.updateParkingInvoice(_stateUi.value.invoice!!).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                message = "Lưu hóa đơn thành công",
                                isSaved = true,
                                isLoading = false
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
    fun confirmInvoice() {
        viewModelScope.launch {
            repository.completeParkingInvoice(_stateUi.value.invoice!!).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                message = "Xác nhận trả xe thành công",
                                isConfirmed = true,
                                isLoading = false
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
    fun refuseInvoice() {
        viewModelScope.launch {
            repository.refuseParkingInvoice(_stateUi.value.invoice!!.id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                message = "Từ chối trả xe thành công",
                                isRefused = true,
                                isLoading = false
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


    fun showError() {
        _stateUi.update {
            it.copy(
                error = ""
            )
        }
    }
    fun showMessage(){
        _stateUi.update {
            it.copy(
                message = ""
            )
        }
    }

    data class InvoiceDetailViewModelState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val invoice: ParkingInvoice? = null,
        val isSaved: Boolean = false,
        val isConfirmed: Boolean = false,
        val isRefused: Boolean = false
    )
}