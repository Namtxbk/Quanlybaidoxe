package com.example.parkingqr.data.remote.dto.invoice

import com.example.parkingqr.domain.model.user.UserInvoice

class UserInvoiceFirebase(
    var id: String? = "",
    var userId: String? = "",
    var name: String? = "",
    var phoneNumber: String? = ""
) {
    constructor(user: UserInvoice) : this() {
        this.id = user.id
        this.userId = user.userId
        this.name = user.name
        this.phoneNumber = user.phoneNumber
    }
}