package com.example.parkingqr.ui.components.registervehicle

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
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
class RegisterVehicleViewModel @Inject constructor(private val repository: IRepository): BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        RegisterVehicleStateViewModel()
    )
    val stateUi: StateFlow<RegisterVehicleStateViewModel> = _stateUi.asStateFlow()

    private var createRegistrationFormJob: Job? = null

    fun createVehicleRegistrationForm(vehicleDetail: VehicleDetail) {
        createRegistrationFormJob?.cancel()
        createRegistrationFormJob = viewModelScope.launch {
            repository.createVehicleRegistrationForm(vehicleDetail).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                isCreated = true,
                                isLoading = false
                            )
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Lỗi không xác định"
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

    data class RegisterVehicleStateViewModel(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val isCreated: Boolean = false
    )
}