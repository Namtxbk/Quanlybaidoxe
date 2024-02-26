package com.example.parkingqr.data.remote

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.example.parkingqr.data.remote.dto.user.UserRequestFirebase
import com.example.parkingqr.data.remote.dto.vehicle.VehicleRequestFirebase
import com.example.parkingqr.data.remote.dto.user.UserResponseFirebase
import com.example.parkingqr.data.remote.dto.vehicle.VehicleResponseFirebase
import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.domain.model.user.UserLogin
import com.example.parkingqr.domain.model.user.UserProfile
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.domain.model.user.UserInvoice
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import com.example.parkingqr.utils.ImageUtil
import com.example.parkingqr.utils.TimeUtil
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class RemoteDataSource @Inject constructor(val context: Context) : IRemoteDataSource {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    override fun searchLicensePlate(licensePlate: String): Flow<State<MutableList<VehicleInvoice>>> =
        flow {
            val vehicleRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
            val query = vehicleRef.whereEqualTo("licensePlate", licensePlate)
            emit(State.loading())
            val querySnapshot = query.get().await()
            val vehicleList: MutableList<VehicleInvoice> = mutableListOf()
            for (document in querySnapshot.documents) {
                document.toObject(VehicleResponseFirebase::class.java)
                    ?.let { vehicleList.add(VehicleInvoice(it)) }
            }
            emit(State.success(vehicleList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun searchUserById(userId: String): Flow<State<MutableList<UserInvoice>>> = flow {
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val query = userRef.whereEqualTo("userId", userId)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val userList: MutableList<UserInvoice> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(UserResponseFirebase::class.java)?.let { userList.add(UserInvoice(it)) }
        }
        emit(State.success(userList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun addNewParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>> =
        flow {
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val parkingInvoiceFirebase = ParkingInvoiceFirebase(parkingInvoice)
            emit(State.loading())

            val storageRef = storage.reference
            val bitmap = ImageUtil.decodeImage(parkingInvoice.imageIn)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val vehicleRegisterRef =
                storageRef.child("${auth.currentUser?.uid}/${Params.PARKING_INVOICE_STORAGE_PATH}/${parkingInvoiceFirebase.id}/${TimeUtil.getCurrentTime()}")
            var uploadTask = vehicleRegisterRef.putBytes(data).await()
            val url = vehicleRegisterRef.downloadUrl.await()

            parkingInvoiceFirebase.imageIn = url.toString()
            parkingInvoiceFirebase.parkingLotId = auth.currentUser?.uid
            parkingInvoiceRef.document(parkingInvoiceFirebase.id!!).set(parkingInvoiceFirebase)
                .await()
            emit(State.success("${parkingInvoiceRef.path}/${parkingInvoiceFirebase.id!!}"))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>> =
        flow {
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query = parkingInvoiceRef.whereEqualTo("id", id)
            emit(State.loading())
            val querySnapshot = query.get().await()
            val parkingInvoiceList: MutableList<ParkingInvoice> = mutableListOf()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)
                    ?.let { parkingInvoiceList.add(ParkingInvoice(it)) }
            }
            emit(State.success(parkingInvoiceList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getNewParkingInvoiceKey(): String {
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        return parkingInvoiceRef.document().id
    }

    override fun completeParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>> =
        flow {
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            emit(State.loading())
            val storageRef = storage.reference
            var url = ""
            if (parkingInvoice.imageOut.isNotEmpty()) {
                val bitmap = ImageUtil.decodeImage(parkingInvoice.imageOut)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val vehicleRegisterRef =
                    storageRef.child("${auth.currentUser?.uid}/${Params.PARKING_INVOICE_STORAGE_PATH}/${parkingInvoice.id}/${TimeUtil.getCurrentTime()}")
                var uploadTask = vehicleRegisterRef.putBytes(data).await()
                url = vehicleRegisterRef.downloadUrl.await().toString()
            }
            parkingInvoice.imageIn = url

            parkingInvoiceRef
                .document(parkingInvoice.id)
                .update(
                    "state",
                    "parked",
                    "imageOut",
                    url,
                    "note",
                    parkingInvoice.note,
                    "paymentMethod",
                    parkingInvoice.paymentMethod,
                    "type",
                    parkingInvoice.type,
                    "timeOut",
                    parkingInvoice.timeOut
                )
                .await()

            emit(State.success("${parkingInvoiceRef.path}/${parkingInvoice.id}"))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun refuseParkingInvoice(id: String): Flow<State<String>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        parkingInvoiceRef.document(id).update("state", "refused").await()
        emit(State.success("${parkingInvoiceRef.path}/${id}"))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query: Query = parkingInvoiceRef.whereEqualTo("state", "parking")
                .whereEqualTo("vehicle.licensePlate", licensePlate)
            val querySnapshot = query.get().await()
            if (querySnapshot.documents.isNotEmpty()) {
                emit(State.success(true))
            } else emit(State.success(false))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getParkingLotInvoiceList(): Flow<State<MutableList<ParkingInvoice>>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query: Query = parkingInvoiceRef.whereEqualTo("parkingLotId", auth.currentUser?.uid)
            val querySnapshot = query.get().await()
            val parkingInvoiceList = mutableListOf<ParkingInvoice>()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                    parkingInvoiceList.add(ParkingInvoice(it))
                }
            }
            emit(State.success(parkingInvoiceList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>> =
        flow {
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query = parkingInvoiceRef.whereEqualTo("id", id)
            emit(State.loading())
            val querySnapshot = query.get().await()
            val parkingInvoiceList: MutableList<ParkingInvoice> = mutableListOf()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                    parkingInvoiceList.add(
                        ParkingInvoice(it)
                    )
                }
            }
            emit(State.success(parkingInvoiceList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun updateParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            parkingInvoiceRef.document(parkingInvoice.id).update(
                "type",
                parkingInvoice.type,
                "note",
                parkingInvoice.note,
                "paymentMethod",
                parkingInvoice.paymentMethod
            ).await()
            emit(State.success(true))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun signIn(email: String, password: String): Flow<State<FirebaseUser?>> = flow {
        emit(State.loading())
        val snapshot = auth.signInWithEmailAndPassword(email, password).await()
        emit(State.success(snapshot.user))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getUserByEmail(email: String): Flow<State<MutableList<UserLogin>>> = flow {
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val query = userRef.whereEqualTo("email", email)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val userList: MutableList<UserLogin> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(UserResponseFirebase::class.java)?.let { userList.add(UserLogin(it)) }
        }
        emit(State.success(userList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun signUp(email: String, password: String): Flow<State<FirebaseUser?>> = flow {
        emit(State.loading())
        val snapshot = auth.createUserWithEmailAndPassword(email, password).await()
        emit(State.success(snapshot.user))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun createNewUser(userLogin: UserLogin): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val key = userRef.document().id
        userLogin.id = key
        val snapshot = userRef.document(userLogin.id!!).set(UserRequestFirebase(userLogin)).await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getUserInformation(): Flow<State<UserProfile>> = flow {
        val user = auth.currentUser
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val query = userRef.whereEqualTo("userId", user?.uid)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val userList: MutableList<UserProfile> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(UserResponseFirebase::class.java)?.let { userList.add(UserProfile(it)) }
        }
        if (userList.isNotEmpty()) {
            emit(State.success(userList[0]))
        } else {
            emit(State.failed("Không tìm thấy người dùng"))
        }
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createVehicleRegistrationForm(vehicleDetail: VehicleDetail): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            // Create reference to fire store
            val vehicleRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
            val key = vehicleRef.document().id
            vehicleDetail.id = key
            vehicleDetail.userId = auth.currentUser?.uid

            // Upload file to fire store
            val storageRef = storage.reference
            val uriList = mutableListOf<String>()

            vehicleDetail.images.asFlow().flatMapMerge { filePath ->
                flow {
                    val file = filePath.toUri()
                    val vehicleRegisterRef =
                        storageRef.child("${vehicleDetail.userId}/${Params.VEHICLE_REGISTRATION_STORAGE_PATH}/${vehicleDetail.id}/${file.lastPathSegment}")
                    vehicleRegisterRef.putFile(file).await()
                    val url = vehicleRegisterRef.downloadUrl.await()
                    emit(url.toString())
                }
            }.collect {
                uriList.add(it)
            }

            vehicleDetail.images.clear()
            vehicleDetail.images.addAll(uriList)

            // Set data to fire store
            val snapshot =
                vehicleRef.document(vehicleDetail.id!!)
                    .set(VehicleRequestFirebase(vehicleDetail)).await()
            emit(State.success(true))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getVehicleRegistrationList(): Flow<State<MutableList<VehicleDetail>>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.VEHICLE_PATH_COLLECTION)
        val query: Query = ref.whereEqualTo("userId", auth.currentUser?.uid)
        val querySnapshot = query.get().await()
        val vehicleList = mutableListOf<VehicleDetail>()
        for (document in querySnapshot.documents) {
            document.toObject(VehicleResponseFirebase::class.java)?.let {
                vehicleList.add(VehicleDetail(it))
            }
        }
        emit(State.success(vehicleList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getVehicleById(id: String): Flow<State<VehicleDetail>> = flow {
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        val query = parkingInvoiceRef.whereEqualTo("id", id)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val vehicleList: MutableList<VehicleDetail> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(VehicleResponseFirebase::class.java)?.let {
                vehicleList.add(
                    VehicleDetail(it)
                )
            }
        }
        if (vehicleList.isNotEmpty()) {
            emit(State.success(vehicleList[0]))
        } else emit(State.failed("Lỗi không xác định"))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun deleteVehicleById(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        parkingInvoiceRef.document(id).delete().await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun signOut(): Flow<State<Boolean>> = flow {
        emit(State.loading())
        auth.signOut()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.Main)

    override fun getUserParkingInvoiceList(): Flow<State<MutableList<ParkingInvoice>>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query: Query = parkingInvoiceRef.whereEqualTo("user.userId", auth.currentUser?.uid)
            val querySnapshot = query.get().await()
            val parkingInvoiceList = mutableListOf<ParkingInvoice>()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                    parkingInvoiceList.add(ParkingInvoice(it))
                }
            }
            emit(State.success(parkingInvoiceList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query: Query = parkingInvoiceRef.whereEqualTo("user.userId", auth.currentUser?.uid)
                .whereGreaterThanOrEqualTo("vehicle.licensePlate", licensePlate.uppercase())
                .whereLessThanOrEqualTo("vehicle.licensePlate", "${licensePlate.uppercase()}~")
            val querySnapshot = query.get().await()
            val parkingInvoiceList = mutableListOf<ParkingInvoice>()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                    parkingInvoiceList.add(ParkingInvoice(it))
                }
            }
            emit(State.success(parkingInvoiceList))
        }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceParkingLot(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query: Query = parkingInvoiceRef.whereEqualTo("parkingLotId", auth.currentUser?.uid)
                .whereGreaterThanOrEqualTo("vehicle.licensePlate", licensePlate.uppercase())
                .whereLessThanOrEqualTo("vehicle.licensePlate", "${licensePlate.uppercase()}~")
            val querySnapshot = query.get().await()
            val parkingInvoiceList = mutableListOf<ParkingInvoice>()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                    parkingInvoiceList.add(ParkingInvoice(it))
                }
            }
            emit(State.success(parkingInvoiceList))
        }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun getAllUser(): Flow<State<MutableList<UserDetail>>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.USER_PATH_COLLECTION)
        val query: Query = ref
        val querySnapshot = query.get().await()
        val userList = mutableListOf<UserDetail>()
        for (document in querySnapshot.documents) {
            document.toObject(UserResponseFirebase::class.java)?.let {
                userList.add(UserDetail(it))
            }
        }
        emit(State.success(userList))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun getUserById(id: String): Flow<State<UserDetail>> = flow {
        val userRef = db.collection(Params.USER_PATH_COLLECTION).document(id)
        emit(State.loading())
        val querySnapshot = userRef.get().await()
        querySnapshot.toObject(UserResponseFirebase::class.java)?.let {
            emit(State.success(UserDetail(it)))
        }
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun updateUser(userDetail: UserDetail): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val snapshot = userRef.document(userDetail.id!!).set(UserRequestFirebase(userDetail)).await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun deleteUser(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val snapshot = userRef.document(id).delete().await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun blockUser(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val snapshot = userRef.document(id).update("status", "blocked").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun activeUser(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val snapshot = userRef.document(id).update("status", "active").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun getAllVehicle(): Flow<State<MutableList<VehicleDetail>>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.VEHICLE_PATH_COLLECTION)
        val query: Query = ref.orderBy("createAt")
        val querySnapshot = query.get().await()
        val vehicleList = mutableListOf<VehicleDetail>()
        for (document in querySnapshot.documents) {
            document.toObject(VehicleResponseFirebase::class.java)?.let {
                vehicleList.add(VehicleDetail(it))
            }
        }
        emit(State.success(vehicleList))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun acceptVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        parkingInvoiceRef.document(vehicleDetail.id!!).update("state","verified").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun refuseVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        parkingInvoiceRef.document(vehicleDetail.id!!).update("state","refused").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun pendingVehcile(vehicleDetail: VehicleDetail): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        parkingInvoiceRef.document(vehicleDetail.id!!).update("state","unverified").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)
}