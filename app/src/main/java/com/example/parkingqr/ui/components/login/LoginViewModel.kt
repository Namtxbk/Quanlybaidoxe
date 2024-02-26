package com.example.parkingqr.ui.components.login

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.ui.base.BaseViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: IRepository) : BaseViewModel() {
    private val _stateUi = MutableStateFlow(
        LoginStateViewModel()
    )
    val stateUi: StateFlow<LoginStateViewModel> = _stateUi.asStateFlow()

    private var loginJob: Job? = null

    fun doLogin(email: String, password: String) {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            repository.signIn(email, password).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {

                        if (state.data != null) {
                            _stateUi.update {
                                it.copy(
                                    user = state.data,
                                    isLoading = false
                                )
                            }
                            findUserRole(state.data.email!!)
                        } else {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    message = "Đăng nhập không thành công"
                                )
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Đăng nhập không thành công"
                            )
                        }
                    }
                }
            }
        }
    }

    fun findUserRole(email: String){
        viewModelScope.launch {
            repository.getUserByEmail(email).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {

                        if (state.data.isNotEmpty()) {

                            val role = state.data[0].role!!
                            if(role == "business"){
                                _stateUi.update {
                                    it.copy(
                                        role = LOGIN_ROLE.BUSINESS,
                                        isLoading = false
                                    )
                                }
                            }
                            else if(role == "user"){
                                _stateUi.update {
                                    it.copy(
                                        role = LOGIN_ROLE.USER,
                                        isLoading = false
                                    )
                                }
                            }
                            else{
                                _stateUi.update {
                                    it.copy(
                                        role = LOGIN_ROLE.ADMIN,
                                        isLoading = false
                                    )
                                }
                            }
                        } else {
                            _stateUi.update {
                                it.copy(
                                    isLoading = false,
                                    message = "Đăng nhập không thành công"
                                )
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                isLoading = false,
                                error = "Đăng nhập không thành công"
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

    data class LoginStateViewModel(
        val isLoading: Boolean = false,
        val user: FirebaseUser? = null,
        val role: LOGIN_ROLE? = null,
        val error: String = "",
        val message: String = "",
    )
    enum class LOGIN_ROLE{
        USER, BUSINESS, ADMIN
    }
}