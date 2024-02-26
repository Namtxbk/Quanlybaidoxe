package com.example.parkingqr.domain.model.vehicle

import com.example.parkingqr.data.remote.dto.vehicle.VehicleResponseFirebase
import com.example.parkingqr.data.remote.dto.invoice.VehicleInvoiceFirebase

class VehicleInvoice() {

    var id: String? = ""
    var userId: String? = ""
    var licensePlate: String? = ""
    var state: String? = ""
    var brand: String? = ""
    var type: String? = ""
    var color: String? = ""
    var ownerFullName: String? = ""

    constructor(vehicleResponseFirebase: VehicleResponseFirebase): this(){
       this.id = vehicleResponseFirebase.id
       this.userId = vehicleResponseFirebase.userId
       this.licensePlate = vehicleResponseFirebase.licensePlate
       this.state = vehicleResponseFirebase.state
       this.brand = vehicleResponseFirebase.brand
       this.type = vehicleResponseFirebase.type
       this.color = vehicleResponseFirebase.color
       this.ownerFullName = vehicleResponseFirebase.ownerFullName
    }
    constructor(vehicleInvoiceFirebase: VehicleInvoiceFirebase): this(){
        this.id = vehicleInvoiceFirebase.id
        this.userId = vehicleInvoiceFirebase.userId
        this.licensePlate = vehicleInvoiceFirebase.licensePlate
        this.state = vehicleInvoiceFirebase.state
        this.brand = vehicleInvoiceFirebase.brand
        this.type = vehicleInvoiceFirebase.type
        this.color = vehicleInvoiceFirebase.color
        this.ownerFullName = vehicleInvoiceFirebase.ownerFullName
    }
    constructor(licensePlate: String): this(){
        this.licensePlate = licensePlate
    }
}