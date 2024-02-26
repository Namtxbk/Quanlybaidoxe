package com.example.parkingqr.domain.model.invoice

import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.domain.model.user.UserInvoice
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import com.example.parkingqr.utils.TimeUtil


class ParkingInvoice() {
    var id: String = ""
    var user: UserInvoice = UserInvoice()
    var vehicle: VehicleInvoice = VehicleInvoice()
    var state: String = ""
    var imageIn: String = ""
    var imageOut: String = ""
    var timeOut: String = ""
    var price: Double = 0.0
    var timeIn: String = ""
    var paymentMethod: String = ""
    var type: String = ""
    var note: String = ""

    constructor(
        ID: String,
        user: UserInvoice,
        vehicle: VehicleInvoice,
        imageIn: String,
        timeIn: String
    ) : this() {
        this.id = ID
        this.user = user
        this.vehicle = vehicle
        this.state = "parking"
        this.imageIn = imageIn
        this.price = 0.0
        this.timeIn = timeIn
        this.paymentMethod = "Tiền mặt"
        this.type = "Theo giờ"
    }

    constructor(parkingInvoiceFirebase: ParkingInvoiceFirebase) : this() {
        this.id = parkingInvoiceFirebase.id.toString()
        this.user = parkingInvoiceFirebase.user?.let { UserInvoice(it) }!!
        this.vehicle = parkingInvoiceFirebase.vehicle?.let { VehicleInvoice(it) }!!
        this.state = parkingInvoiceFirebase.state.toString()
        this.imageIn = parkingInvoiceFirebase.imageIn.toString()
        this.imageOut = parkingInvoiceFirebase.imageOut.toString()
        this.price = parkingInvoiceFirebase.price ?: 0.0
        this.timeIn = parkingInvoiceFirebase.timeIn.toString()
        this.timeOut = parkingInvoiceFirebase.timeOut.toString()
        this.paymentMethod = parkingInvoiceFirebase.paymentMethod.toString()
        this.type = parkingInvoiceFirebase.type.toString()
        this.note = parkingInvoiceFirebase.note.toString()
    }

    fun calTotalPrice(): Double {

        val endTime: Long = if (getState() == ParkingState.PARKING) {
            TimeUtil.getCurrentTime()
        } else {
            timeOut.toLong()
        }
        val hoursParked = ((endTime - timeIn.toLong()) / (1000 * 60 * 60)).toInt()
        var parkingFee = 3000.0
        if (hoursParked > 5) {
            val additionalHours = hoursParked - 5
            parkingFee += additionalHours * 250
        }
        return parkingFee
    }

    fun getState(): ParkingState {
        return if (state == "parking") {
            ParkingState.PARKING
        } else if (state == "parked") {
            ParkingState.PARKED
        } else {
            ParkingState.REFUSED
        }
    }

    enum class ParkingState {
        PARKING, PARKED, REFUSED
    }
}