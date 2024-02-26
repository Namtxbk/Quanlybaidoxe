package com.example.parkingqr.data.remote

import com.example.parkingqr.domain.model.user.UserLogin
import com.example.parkingqr.domain.model.user.UserProfile
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.domain.model.user.UserInvoice
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {
    fun searchLicensePlate(licensePlate: String): Flow<State<MutableList<VehicleInvoice>>>
    fun searchUserById(userId: String): Flow<State<MutableList<UserInvoice>>>
    fun addNewParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>>
    fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>>
    fun getNewParkingInvoiceKey(): String
    fun completeParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>>
    fun refuseParkingInvoice(id: String): Flow<State<String>>
    fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>>
    fun getParkingLotInvoiceList(): Flow<State<MutableList<ParkingInvoice>>>
    fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>>
    fun updateParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<Boolean>>
    fun signIn(email: String, password: String): Flow<State<FirebaseUser?>>
    fun getUserByEmail(email: String): Flow<State<MutableList<UserLogin>>>

    fun signUp(email: String, password: String): Flow<State<FirebaseUser?>>

    fun createNewUser(userLogin: UserLogin): Flow<State<Boolean>>
    fun getUserInformation(): Flow<State<UserProfile>>

    fun createVehicleRegistrationForm(vehicleDetail: VehicleDetail): Flow<State<Boolean>>

    fun getVehicleRegistrationList(): Flow<State<MutableList<VehicleDetail>>>
    fun getVehicleById(id: String): Flow<State<VehicleDetail>>

    fun deleteVehicleById(id: String): Flow<State<Boolean>>

    fun signOut(): Flow<State<Boolean>>

    fun getUserParkingInvoiceList(): Flow<State<MutableList<ParkingInvoice>>>

    fun searchParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>>

    fun searchParkingInvoiceParkingLot(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>>

    fun getAllUser(): Flow<State<MutableList<UserDetail>>>

    fun getUserById(id: String): Flow<State<UserDetail>>

    fun updateUser(userDetail: UserDetail): Flow<State<Boolean>>

    fun deleteUser(id: String): Flow<State<Boolean>>

    fun blockUser(id: String): Flow<State<Boolean>>

    fun activeUser(id: String): Flow<State<Boolean>>

    fun getAllVehicle(): Flow<State<MutableList<VehicleDetail>>>

    fun acceptVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>>

    fun refuseVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>>

    fun pendingVehcile(vehicleDetail: VehicleDetail): Flow<State<Boolean>>
}