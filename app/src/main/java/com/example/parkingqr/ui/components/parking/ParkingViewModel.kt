package com.example.parkingqr.ui.components.parking

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.user.UserInvoice
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import com.example.parkingqr.ui.base.BaseViewModel
import com.example.parkingqr.utils.ImageUtil
import com.example.parkingqr.utils.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ParkingViewModel @Inject constructor(private val repository: IRepository) : BaseViewModel() {

    companion object {
        const val SEARCH_LICENSE_PLATE = "SEARCH_LICENSE_PLATE"
        const val SEARCH_USER = "SEARCH_USER"
        const val ADD_NEW_PARKING_INVOICE = "ADD_NEW_PARKING_INVOICE"
        const val VALIDATE_VEHICLE = "VALIDATE_VEHICLE"
    }

    private var validateVehicleJob: Job? = null
    private var searchVehicleJob: Job? = null
    private var addParkingInvoiceJob: Job? = null
    private var updateParkingInvoiceJob: Job? = null
    private var searchParkingInvoiceJob: Job? = null
    private val _stateUi = MutableStateFlow(
        ParkingViewModelState()
    )
    val stateUi: StateFlow<ParkingViewModelState> = _stateUi.asStateFlow()

    fun searchVehicleAndUserByLicensePlate(licensePlate: String, imageCarIn: Bitmap) {
        searchVehicleJob?.cancel()
        searchVehicleJob = viewModelScope.launch {
            flowOf(SEARCH_LICENSE_PLATE, SEARCH_USER).flatMapConcat {
                when (it) {
                    SEARCH_LICENSE_PLATE -> repository.searchLicensePlate(licensePlate)
                    else -> {
                        val userId = _stateUi.value.vehicle?.userId
                        if (!userId.isNullOrEmpty()) {
                            repository.searchUserById(userId)
                        } else {
                            flowOf()
                        }
                    }
                }
            }.collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(state = ParkingState.LOADING)
                        }
                    }
                    is State.Success -> {

                        if (state.data.isEmpty()) {
                            _stateUi.update {
                                it.copy(
                                    state = ParkingState.FAIL_FOUND_VEHICLE,
                                    parkingInvoice = createParkingInvoiceForUnRegisterVehicle(licensePlate, imageCarIn)
                                )
                            }
                        }

                        for (value in state.data) {
                            if (value is VehicleInvoice) {
                                _stateUi.update {
                                    it.copy(vehicle = value)
                                }
                            } else if (value is UserInvoice) {
                                _stateUi.update {
                                    it.copy(
                                        state = ParkingState.SUCCESSFUL_FOUND_VEHICLE,
                                        user = value,
                                        parkingInvoice = ParkingInvoice(
                                            ID = repository.getNewParkingInvoiceKey(),
                                            user = value,
                                            vehicle = it.vehicle!!,
                                            imageIn = ImageUtil.encodeImage(imageCarIn),
                                            timeIn = TimeUtil.getCurrentTime().toString()
                                        ),
                                    )
                                }
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                state = ParkingState.FAIL_FOUND_VEHICLE,
                                errorMessage = state.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun createParkingInvoiceForUnRegisterVehicle(licensePlate: String, imageCarIn: Bitmap): ParkingInvoice{
        return ParkingInvoice(
            ID = repository.getNewParkingInvoiceKey(),
            user = UserInvoice(),
            vehicle = VehicleInvoice(licensePlate),
            imageIn = ImageUtil.encodeImage(imageCarIn),
            timeIn = TimeUtil.getCurrentTime().toString()
        )
    }

    fun addNewParkingInvoice() {
        var available = false
        addParkingInvoiceJob?.cancel()
        addParkingInvoiceJob = viewModelScope.launch {
            flowOf(VALIDATE_VEHICLE, ADD_NEW_PARKING_INVOICE).flatMapConcat {
                when (it) {
                    VALIDATE_VEHICLE -> repository.searchParkingInvoiceByLicensePlateAndStateParking(_stateUi.value.parkingInvoice?.vehicle?.licensePlate!!)
                    else -> {
                        if (!available) {
                            repository.addNewParkingInvoice(_stateUi.value.parkingInvoice!!)
                        } else {
                            flowOf()
                        }
                    }
                }
            }.collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(state = ParkingState.LOADING)
                        }
                    }
                    is State.Success -> {
                        if(state.data is Boolean){
                            available = state.data
                            if(available){
                                _stateUi.update {
                                    it.copy(state = ParkingState.PARKED_VEHICLE)
                                }
                            }
                        }
                        else{
                            _stateUi.update {
                                it.copy(state = ParkingState.SUCCESSFUL_CREATE_PARKING_INVOICE)
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                state = ParkingState.FAIL_CREATE_PARKING_INVOICE,
                                errorMessage = it.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }

    fun refreshData() {
        _stateUi.update {
            it.copy(
                state = ParkingState.BLANK,
                errorMessage = "",
                parkingInvoice = null,
                user = null,
                vehicle = null,
            )
        }
    }

    fun searchParkingInvoiceById(id: String) {
        searchParkingInvoiceJob?.cancel()
        searchParkingInvoiceJob = viewModelScope.launch {
            repository.searchParkingInvoiceById(id).collect { state ->
                when (state) {
                    is State.Loading -> {
                        _stateUi.update {
                            it.copy(state = ParkingState.LOADING)
                        }
                    }
                    is State.Success -> {
                        if (state.data.isNotEmpty()) {
                            if(state.data[0].state == "parked"){
                                _stateUi.update {
                                    it.copy(
                                        state = ParkingState.PARKED_PARKING_INVOICE,
                                    )
                                }
                            }
                            else{
                                _stateUi.update {
                                    it.copy(
                                        state = ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE,
                                        parkingInvoice = state.data[0],
                                    )
                                }
                            }
                        } else {
                            _stateUi.update {
                                it.copy(
                                    state = ParkingState.FAIL_SEARCH_PARKING_INVOICE,
                                )
                            }
                        }
                    }
                    is State.Failed -> {
                        _stateUi.update {
                            it.copy(
                                state = ParkingState.ERROR,
                                errorMessage = it.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }


    fun completeParkingInvoice() {
        updateParkingInvoiceJob?.cancel()
        updateParkingInvoiceJob = viewModelScope.launch {
            repository.completeParkingInvoice(_stateUi.value.parkingInvoice!!)
                .collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _stateUi.update {
                                it.copy(state = ParkingState.LOADING)
                            }
                        }
                        is State.Success -> {
                            _stateUi.update {
                                it.copy(
                                    state = ParkingState.SUCCESSFUL_COMPLETE_PARKING_INVOICE,
                                )
                            }
                        }
                        is State.Failed -> {
                            _stateUi.update {
                                it.copy(
                                    state = ParkingState.FAIL_COMPLETE_PARKING_INVOICE,
                                    errorMessage = it.errorMessage
                                )
                            }
                        }
                    }
                }
        }
    }

    fun updateInvoiceOut(_paymentMethod: String, _type: String, _imgOutString: String, _note: String){
        val newParkingInvoice = _stateUi.value.parkingInvoice
        newParkingInvoice?.apply {
            paymentMethod = _paymentMethod
            type = _type
            imageOut = _imgOutString
            note = _note
            timeOut = TimeUtil.getCurrentTime().toString()
        }
        _stateUi.update {
            it.copy(
                parkingInvoice = newParkingInvoice
            )
        }
    }
    fun updateInvoiceIn(_paymentMethod: String, _type: String, _note: String){
        val newParkingInvoice = _stateUi.value.parkingInvoice
        newParkingInvoice?.apply {
            paymentMethod = _paymentMethod
            type = _type
            note = _note
        }
        _stateUi.update {
            it.copy(
                parkingInvoice = newParkingInvoice
            )
        }
    }

    fun getDataFromQRCode(result: String){
        _stateUi.update {
            it.copy(
                state = ParkingState.SUCCESSFUL_GET_QR_CODE,
                qrcode = result
            )
        }
    }


    data class ParkingViewModelState(
        val errorMessage: String = "",
        val user: UserInvoice? = null,
        val vehicle: VehicleInvoice? = null,
        val parkingInvoice: ParkingInvoice? = null,
        val state: ParkingState = ParkingState.BLANK,
        val errorList: MutableMap<ParkingState, String> = hashMapOf(),
        val messageList: MutableMap<ParkingState, String> = hashMapOf(),
        val qrcode: String = ""
    ) {
        init {
            errorList[ParkingState.FAIL_FOUND_VEHICLE] =
                "Không tìm thấy phương tiện tương ứng có biển số"
            errorList[ParkingState.FAIL_CREATE_PARKING_INVOICE] =
                "Tạo hóa đơn thất bại"
            errorList[ParkingState.FAIL_SEARCH_PARKING_INVOICE] =
                "Không tìm thấy hóa đơn tương ứng"
            errorList[ParkingState.FAIL_COMPLETE_PARKING_INVOICE] =
                "Trả hóa đơn xe không thành công"
            errorList[ParkingState.FAIL_GET_QR_CODE] =
                "Không tìm thấy QRCODE"
            errorList[ParkingState.PARKED_PARKING_INVOICE] =
                "Hóa đơn không hợp lệ"
            errorList[ParkingState.PARKED_VEHICLE] =
                "Xe đã được gửi"

            messageList[ParkingState.SUCCESSFUL_FOUND_VEHICLE] =
                "Tìm thấy phương tiện tương ứng có biển số"
            messageList[ParkingState.SUCCESSFUL_CREATE_PARKING_INVOICE] =
                "Tạo hóa đơn gửi xe thành công cho xe có biển số"
            messageList[ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE] =
                "Tìm thấy hóa đơn xe có biển số"
            messageList[ParkingState.SUCCESSFUL_COMPLETE_PARKING_INVOICE] =
                "Trả hóa đơn xe thành công cho xe có biển số"

        }
    }

    enum class ParkingState {
        BLANK,
        LOADING,
        ERROR,
        SUCCESSFUL_FOUND_VEHICLE,
        PARKED_VEHICLE,
        FAIL_FOUND_VEHICLE,
        SUCCESSFUL_CREATE_PARKING_INVOICE,
        FAIL_CREATE_PARKING_INVOICE,
        SUCCESSFUL_SEARCH_PARKING_INVOICE,
        FAIL_SEARCH_PARKING_INVOICE,
        SUCCESSFUL_COMPLETE_PARKING_INVOICE,
        PARKED_PARKING_INVOICE,
        FAIL_COMPLETE_PARKING_INVOICE,
        SUCCESSFUL_GET_QR_CODE,
        FAIL_GET_QR_CODE
    }
}