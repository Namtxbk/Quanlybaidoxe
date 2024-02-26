package com.example.parkingqr.domain.model.user

import com.example.parkingqr.data.remote.dto.user.UserResponseFirebase


class UserLogin() {
    var id: String? = ""
    var role: String? = ""
    var userId: String? = ""
    var name: String? = ""
    var phoneNumber: String? = ""
    var email: String? = ""

    constructor(userResponseFirebase: UserResponseFirebase): this(){
        this.id = userResponseFirebase.id
        this.userId = userResponseFirebase.userId
        this.name = userResponseFirebase.name
        this.phoneNumber = userResponseFirebase.phoneNumber
        this.role = userResponseFirebase.role
        this.email = userResponseFirebase.email
    }
}