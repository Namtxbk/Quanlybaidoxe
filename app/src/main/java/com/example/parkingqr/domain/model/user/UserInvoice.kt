package com.example.parkingqr.domain.model.user

import com.example.parkingqr.data.remote.dto.user.UserResponseFirebase
import com.example.parkingqr.data.remote.dto.invoice.UserInvoiceFirebase

class UserInvoice() {
    var id: String? = ""
    var userId: String? = ""
    var name: String? = ""
    var phoneNumber: String? = ""

    constructor(userResponseFirebase: UserResponseFirebase): this(){
        this.id = userResponseFirebase.id
        this.userId = userResponseFirebase.userId
        this.name = userResponseFirebase.name
        this.phoneNumber = userResponseFirebase.phoneNumber
    }
    constructor(userInvoiceFirebase: UserInvoiceFirebase): this(){
        this.id = userInvoiceFirebase.id
        this.userId = userInvoiceFirebase.userId
        this.name = userInvoiceFirebase.name
        this.phoneNumber = userInvoiceFirebase.phoneNumber
    }
}