package com.example.parkingqr.data.remote.dto.invoice

import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

class VehicleInvoiceFirebase(
    var id: String? = "",
    var userId: String? = "",
    var licensePlate: String? = "",
    var state: String? = "",
    var brand: String? = "",
    var type: String? = "",
    var color: String? = "",
    var ownerFullName: String? = "",
){
    constructor(vehicle: VehicleInvoice): this(){
        this.id  = vehicle.id
        this.userId = vehicle.userId
        this.licensePlate = vehicle.licensePlate
        this.state = vehicle.state
        this.brand = vehicle.brand
        this.type = vehicle.type
        this.color = vehicle.color
        this.ownerFullName = vehicle.ownerFullName
    }
}
