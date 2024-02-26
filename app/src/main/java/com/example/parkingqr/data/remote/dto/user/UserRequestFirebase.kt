package com.example.parkingqr.data.remote.dto.user

import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.domain.model.user.UserLogin

data class UserRequestFirebase(
    var id: String? = null,
    var role: String? = null,
    var status: String? = null,
    var userId: String? = null,
    var personalCode: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var birthday: String? = null,
    var email: String? = null,
    var username: String? = null
){
    constructor(userLogin: UserLogin): this(){
        this.id = userLogin.id
        this.role = userLogin.role
        this.userId = userLogin.userId
        this.name = userLogin.name
        this.email = userLogin.email
        this.phoneNumber = userLogin.phoneNumber
        this.personalCode = ""
        this.address = ""
        this.birthday = ""
        this.username = ""
        this.status = "active"
    }
    constructor(userDetail: UserDetail): this(){
        id = userDetail.id
        role = userDetail.role
        status = userDetail.status
        userId = userDetail.userId
        personalCode = userDetail.personalCode
        name = userDetail.name
        phoneNumber = userDetail.phoneNumber
        address = userDetail.address
        birthday = userDetail.birthday
        email = userDetail.email
        username = userDetail.username
    }
}