package com.example.parkingqr.ui.components.usermanagement

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.ui.base.BaseViewModel
import com.example.parkingqr.ui.components.invoice.InvoiceDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(private val repository: IRepository): BaseViewModel() {

    private val _stateUi = MutableStateFlow(
        UserDetailState()
    )
    val stateUi: StateFlow<UserDetailState> = _stateUi.asStateFlow()

    fun getUserById(id: String) {
        viewModelScope.launch {
            repository.getUserById(id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                userDetail = state.data,
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

    fun updateUser() {
        viewModelScope.launch {
            repository.updateUser(_stateUi.value.userDetail!!).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                message = "Cập nhật người dùng thành công",
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


    fun updateNewUserDetail(_name: String, _userName: String, _address: String, _email: String, _identifierCode: String, _dateOfBirth: String, _phone: String){
       _stateUi.update {
           it.copy(
               userDetail = it.userDetail?.apply {
                   name = _name
                   username = _userName
                   address = _address
                   email = _email
                   personalCode = _identifierCode
                   birthday = _dateOfBirth
                   phoneNumber = _phone
               }
           )
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

    data class UserDetailState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val userDetail: UserDetail? = null,
        val isSaved: Boolean = false,
    )
}