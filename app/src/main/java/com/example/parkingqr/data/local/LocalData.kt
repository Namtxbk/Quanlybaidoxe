package com.example.parkingqr.data.local

import android.content.Context
import javax.inject.Inject

class LocalData @Inject constructor(val context: Context): ILocalData {

    override fun getUserId(): String {
        TODO("Not yet implemented")
    }

    override fun setUserId(userId: String) {
        TODO("Not yet implemented")
    }
}