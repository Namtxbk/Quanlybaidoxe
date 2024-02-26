package com.example.parkingqr.data.remote.dto.user

data class UserResponseFirebase(
    val id: String? = null,
    val role: String? = null,
    val status: String? = null,
    val userId: String? = null,
    val personalCode: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val birthday: String? = null,
    val email: String? = null,
    val username: String? = null
)