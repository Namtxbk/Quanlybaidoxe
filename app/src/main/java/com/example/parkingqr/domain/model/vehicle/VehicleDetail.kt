package com.example.parkingqr.domain.model.vehicle

import com.example.parkingqr.data.remote.dto.vehicle.VehicleResponseFirebase

class VehicleDetail() {
    var id: String? = null
    var createAt: String? = null
    var userId: String? = null
    var licensePlate: String? = null
    var state: String? = null
    var brand: String? = null
    var type: String? = null
    var color: String? = null
    var registrationDate: String? = null
    var expireDate: String? = null
    var chassisNumber: String? = null
    var engineNumber: String? = null
    var ownerFullName: String? = null
    var address: String? = null
    var certificateNumber: String? = null
    var images: MutableList<String> = mutableListOf()

    constructor(
        id: String?,
        createAt: String?,
        userId: String?,
        licensePlate: String?,
        state: String?,
        brand: String?,
        type: String?,
        color: String?,
        registrationDate: String?,
        expireDate: String?,
        chassisNumber: String?,
        engineNumber: String?,
        ownerFullName: String?,
        address: String?,
        certificateNumber: String?,
        images: MutableList<String>
    ) : this() {
        this.id = id
        this.createAt = createAt
        this.userId = userId
        this.licensePlate = licensePlate
        this.state = state
        this.brand = brand
        this.type = type
        this.color = color
        this.registrationDate = registrationDate
        this.expireDate = expireDate
        this.chassisNumber = chassisNumber
        this.engineNumber = engineNumber
        this.ownerFullName = ownerFullName
        this.address = address
        this.certificateNumber = certificateNumber
        this.images.addAll(images)
    }

    constructor(
        vehicleResponseFirebase: VehicleResponseFirebase
    ) : this() {
        this.id = vehicleResponseFirebase.id
        this.createAt = vehicleResponseFirebase.createAt
        this.userId = vehicleResponseFirebase.userId
        this.licensePlate = vehicleResponseFirebase.licensePlate
        this.state = vehicleResponseFirebase.state
        this.brand = vehicleResponseFirebase.brand
        this.type = vehicleResponseFirebase.type
        this.color = vehicleResponseFirebase.color
        this.registrationDate = vehicleResponseFirebase.registrationDate
        this.expireDate = vehicleResponseFirebase.expireDate
        this.chassisNumber = vehicleResponseFirebase.chassisNumber
        this.engineNumber = vehicleResponseFirebase.engineNumber
        this.ownerFullName = vehicleResponseFirebase.ownerFullName
        this.address = vehicleResponseFirebase.address
        this.certificateNumber = vehicleResponseFirebase.certificateNumber
        vehicleResponseFirebase.images?.let { this.images.addAll(it) }
    }

    fun getState(): VehicleState{
        return if(state == "unverified"){
            VehicleState.PENDING
        } else if(state == "verified"){
            VehicleState.VERIFIED
        } else{
            VehicleState.REFUSED
        }
    }

    enum class VehicleState{
        PENDING, VERIFIED, REFUSED
    }
}