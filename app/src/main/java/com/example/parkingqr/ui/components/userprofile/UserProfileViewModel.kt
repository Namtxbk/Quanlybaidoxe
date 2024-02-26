package com.example.parkingqr.ui.components.userprofile

import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.user.UserProfile
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
class UserProfileViewModel @Inject constructor(private val repository: IRepository) :
    BaseViewModel() {

    private val _stateUi = MutableStateFlow(
        MyProfileStateViewModel()
    )
    val stateUi: StateFlow<MyProfileStateViewModel> = _stateUi.asStateFlow()
    private var getUserInforJob: Job? = null

    init {
        getUserInformation()
    }

    fun getUserInformation() {
        getUserInforJob?.cancel()
        getUserInforJob = viewModelScope.launch {
            repository.getUserInformation().collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is State.Success -> {
                        _stateUi.update {
                            it.copy(
                                userProfile = state.data,
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


    data class MyProfileStateViewModel(
        val isLoading: Boolean = false,
        val error: String = "",
        val message: String = "",
        val userProfile: UserProfile? = null,
        val isSignedOut: Boolean = false
    )
}