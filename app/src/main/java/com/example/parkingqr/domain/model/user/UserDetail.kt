package com.example.parkingqr.domain.model.user

import com.example.parkingqr.data.remote.dto.user.UserResponseFirebase

class UserDetail() {
    var id: String? = ""
    var role: String? = ""
    var status: String? = ""
    var userId: String? = ""
    var personalCode: String? = ""
    var name: String? = ""
    var phoneNumber: String? = ""
    var address: String? = ""
    var birthday: String? = ""
    var email: String? = ""
    var username: String? = ""

    constructor(userResponseFirebase: UserResponseFirebase): this(){
        this.id = userResponseFirebase.id
        this.role = userResponseFirebase.role
        this.userId = userResponseFirebase.userId
        this.personalCode = userResponseFirebase.personalCode
        this.name = userResponseFirebase.name
        this.phoneNumber = userResponseFirebase.phoneNumber
        this.address = userResponseFirebase.address
        this.birthday = userResponseFirebase.birthday
        this.email = userResponseFirebase.email
        this.username = userResponseFirebase.username
        this.status = userResponseFirebase.status
    }

    fun getStatus(): UserStatus{
        return if(status == "active"){
            UserStatus.ACTIVE
        } else{
            UserStatus.BLOCKED
        }
    }

    enum class UserStatus{
        ACTIVE, BLOCKED
    }
}