package com.example.parkingqr.domain.model.user

import com.example.parkingqr.data.remote.dto.user.UserResponseFirebase


class UserProfile() {
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
}