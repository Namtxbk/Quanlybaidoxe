package com.example.parkingqr.ui.components.usermanagement

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(private val repository: IRepository): BaseViewModel() {

    private val _stateUi = MutableStateFlow(
        UserManagementState()
    )
    val stateUi: StateFlow<UserManagementState> = _stateUi.asStateFlow()
    private var getUserJob: Job? = null
    private var searchUserJob: Job? = null

    init {
        getUserList()
    }

    fun getUserList() {
        getUserJob?.cancel()
        getUserJob = viewModelScope.launch {
            repository.getAllUser().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                userList = state.data,
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

    fun deleteUser(userDetail: UserDetail) {
        viewModelScope.launch {
            repository.deleteUser(userDetail.id!!).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                message = "Xóa tài khoản thành công",
                                isLoading = false,
                                userList = it.userList.apply {
                                    remove(userDetail)
                                },
                                isDeleted = true
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

    fun blockUser(userDetail: UserDetail) {
        viewModelScope.launch {
            repository.blockUser(userDetail.id!!).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                message = "Chặn tài khoản thành công",
                                isLoading = false,
                                userList = it.userList.apply {
                                    find { item -> item.id == userDetail.id }?.status = "blocked"
                                }
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

    fun activeUser(userDetail: UserDetail) {
        viewModelScope.launch {
            repository.activeUser(userDetail.id!!).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                message = "Bỏ chặn tài khoản thành công",
                                isLoading = false,
                                userList = it.userList.apply {
                                    find { item -> item.id == userDetail.id }?.status = "active"
                                }
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

    fun signOut() {
        viewModelScope.launch {
            repository.signOut().collect { state ->
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
                                isSignedOut = true
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

    fun showError(){
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


    data class UserManagementState(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val isDeleted: Boolean = false,
        val isSignedOut: Boolean = false,
        val userList: MutableList<UserDetail> = mutableListOf()
    )
}