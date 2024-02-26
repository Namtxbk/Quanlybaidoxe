package com.example.parkingqr.data.remote.dto.invoice

import com.example.parkingqr.domain.model.invoice.ParkingInvoice

data class ParkingInvoiceFirebase(
    var id: String? = null,
    var parkingLotId: String? = null,
    var user: UserInvoiceFirebase? = null,
    var vehicle: VehicleInvoiceFirebase? = null,
    var state: String? = null,
    var imageIn: String? = null,
    var imageOut: String? = null,
    var price: Double? = null,
    var timeIn: String? = null,
    var timeOut: String? = null,
    var paymentMethod: String? = null,
    var type: String? = null,
    var note: String? = null
    ){
    constructor(parkingInvoice: ParkingInvoice): this(){
        this.id = parkingInvoice.id
        this.imageIn = parkingInvoice.imageIn
        this.state = parkingInvoice.state
        this.imageOut = parkingInvoice.imageOut
        this.price = parkingInvoice.price
        this.paymentMethod = parkingInvoice.paymentMethod
        this.type = parkingInvoice.type
        this.note = parkingInvoice.note
        this.timeIn = parkingInvoice.timeIn
        this.timeOut = parkingInvoice.timeOut
        this.user = UserInvoiceFirebase(parkingInvoice.user)
        this.vehicle = VehicleInvoiceFirebase(parkingInvoice.vehicle)
    }
}