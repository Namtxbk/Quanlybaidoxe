package com.example.parkingqr.data

import com.example.parkingqr.data.local.ILocalData
import com.example.parkingqr.data.remote.IRemoteDataSource
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.user.UserLogin
import com.example.parkingqr.domain.model.user.UserProfile
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.domain.model.user.UserInvoice
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(private val remoteDataSource: IRemoteDataSource, private val localData: ILocalData) : IRepository {

    override fun searchLicensePlate(licensePlate: String): Flow<State<MutableList<VehicleInvoice>>> {
        return remoteDataSource.searchLicensePlate(licensePlate)
    }

    override fun searchUserById(userId: String): Flow<State<MutableList<UserInvoice>>> {
        return remoteDataSource.searchUserById(userId)
    }

    override fun addNewParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>> {
        return remoteDataSource.addNewParkingInvoice(parkingInvoice)
    }

    override fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>> {
        return remoteDataSource.searchParkingInvoiceById(id)
    }

    override fun getNewParkingInvoiceKey(): String {
        return remoteDataSource.getNewParkingInvoiceKey()
    }

    override fun completeParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>> {
        return remoteDataSource.completeParkingInvoice(parkingInvoice)
    }

    override fun refuseParkingInvoice(id: String): Flow<State<String>> {
        return remoteDataSource.refuseParkingInvoice(id)
    }

    override fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>> {
        return remoteDataSource.searchParkingInvoiceByLicensePlateAndStateParking(licensePlate)
    }

    override fun getParkingLotInvoiceList(): Flow<State<MutableList<ParkingInvoice>>> {
        return remoteDataSource.getParkingLotInvoiceList()
    }

    override fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>> {
        return remoteDataSource.getParkingInvoiceById(id)
    }

    override fun updateParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<Boolean>> {
        return remoteDataSource.updateParkingInvoice(parkingInvoice)
    }

    override fun signIn(email: String, password: String): Flow<State<FirebaseUser?>> {
        return remoteDataSource.signIn(email, password)
    }

    override fun getUserByEmail(email: String): Flow<State<MutableList<UserLogin>>> {
        return remoteDataSource.getUserByEmail(email)
    }

    override fun signUp(email: String, password: String): Flow<State<FirebaseUser?>> {
        return remoteDataSource.signUp(email, password)
    }

    override fun createNewUser(userLogin: UserLogin): Flow<State<Boolean>> {
        return remoteDataSource.createNewUser(userLogin)
    }

    override fun getUserInformation(): Flow<State<UserProfile>> {
        return remoteDataSource.getUserInformation()
    }

    override fun createVehicleRegistrationForm(vehicleDetail: VehicleDetail): Flow<State<Boolean>> {
        return remoteDataSource.createVehicleRegistrationForm(vehicleDetail)
    }

    override fun getVehicleRegistrationList(): Flow<State<MutableList<VehicleDetail>>> {
        return remoteDataSource.getVehicleRegistrationList()
    }

    override fun getVehicleById(id: String): Flow<State<VehicleDetail>> {
        return remoteDataSource.getVehicleById(id)
    }

    override fun deleteVehicleById(id: String): Flow<State<Boolean>> {
        return remoteDataSource.deleteVehicleById(id)
    }

    override fun signOut(): Flow<State<Boolean>> {
        return remoteDataSource.signOut()
    }

    override fun getUserParkingInvoiceList(): Flow<State<MutableList<ParkingInvoice>>> {
        return remoteDataSource.getUserParkingInvoiceList()
    }

    override fun searchParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>> {
        return remoteDataSource.searchParkingInvoiceUser(licensePlate)
    }

    override fun searchParkingInvoiceParkingLot(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>> {
        return remoteDataSource.searchParkingInvoiceParkingLot(licensePlate)
    }

    override fun getAllUser(): Flow<State<MutableList<UserDetail>>> {
        return remoteDataSource.getAllUser()
    }

    override fun getUserById(id: String): Flow<State<UserDetail>> {
        return remoteDataSource.getUserById(id)
    }

    override fun updateUser(userDetail: UserDetail): Flow<State<Boolean>> {
        return remoteDataSource.updateUser(userDetail)
    }

    override fun deleteUser(id: String): Flow<State<Boolean>> {
        return remoteDataSource.deleteUser(id)
    }

    override fun blockUser(id: String): Flow<State<Boolean>> {
        return remoteDataSource.blockUser(id)
    }

    override fun activeUser(id: String): Flow<State<Boolean>> {
        return remoteDataSource.activeUser(id)
    }

    override fun getAllVehicle(): Flow<State<MutableList<VehicleDetail>>> {
        return remoteDataSource.getAllVehicle()
    }

    override fun acceptVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>> {
        return remoteDataSource.acceptVehicle(vehicleDetail)
    }

    override fun refuseVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>> {
        return remoteDataSource.refuseVehicle(vehicleDetail)
    }

    override fun pendingVehcile(vehicleDetail: VehicleDetail): Flow<State<Boolean>> {
        return remoteDataSource.pendingVehcile(vehicleDetail)
    }
}