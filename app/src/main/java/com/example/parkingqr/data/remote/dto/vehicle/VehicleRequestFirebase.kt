package com.example.parkingqr.data.remote.dto.vehicle

import com.example.parkingqr.domain.model.vehicle.VehicleDetail

class VehicleRequestFirebase() {
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
        vehicleDetail: VehicleDetail
    ) : this() {
        this.id = vehicleDetail.id
        this.createAt = vehicleDetail.createAt
        this.userId = vehicleDetail.userId
        this.licensePlate = vehicleDetail.licensePlate
        this.state = vehicleDetail.state
        this.brand = vehicleDetail.brand
        this.type = vehicleDetail.type
        this.color = vehicleDetail.color
        this.registrationDate = vehicleDetail.registrationDate
        this.expireDate = vehicleDetail.expireDate
        this.chassisNumber = vehicleDetail.chassisNumber
        this.engineNumber = vehicleDetail.engineNumber
        this.ownerFullName = vehicleDetail.ownerFullName
        this.address = vehicleDetail.address
        this.certificateNumber = vehicleDetail.certificateNumber
        this.images.addAll(vehicleDetail.images)
    }
}