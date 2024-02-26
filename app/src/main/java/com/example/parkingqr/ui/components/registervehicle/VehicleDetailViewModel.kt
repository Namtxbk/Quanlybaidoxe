package com.example.parkingqr.ui.components.registervehicle

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleDetailViewModel @Inject constructor(private val repository: IRepository) :
    BaseViewModel() {

    private val _stateUi = MutableStateFlow(VehicleDetailState())
    val stateUi = _stateUi.asStateFlow()

    fun getVehicleDetail(id: String) {
        viewModelScope.launch {
            repository.getVehicleById(id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                vehicleDetail = state.data
                            )
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = it.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun cancelVehicleRegistration(id: String) {
        viewModelScope.launch {
            repository.deleteVehicleById(id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                isDeleted = true
                            )
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = it.message
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

    fun showMessage() {
        _stateUi.update {
            it.copy(
                message = ""
            )
        }
    }

    data class VehicleDetailState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val vehicleDetail: VehicleDetail? = null,
        val isDeleted: Boolean = false
    )
}