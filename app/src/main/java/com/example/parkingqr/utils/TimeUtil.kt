package com.example.parkingqr.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    const val HOUR_TO_MILISECONDS: Long = 3600000
    const val MINUTE_TO_MILISECONDS: Long = 60000

    fun convertToLocalTime(systemTime: Long, offSet: Long): Long {
        return systemTime + HOUR_TO_MILISECONDS * offSet
    }

    fun convertMilisecondsToDate(miliseconds: String): String {
        if(miliseconds.isEmpty()) return ""
        try {
            val res = Date(miliseconds.toLong())
            val dateFormat: DateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")
            return dateFormat.format(res)
        }catch (e: Exception){
            throw Exception(e)
        }
    }

    fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    fun getDateCurrentTime(): String{
        return convertMilisecondsToDate(getCurrentTime().toString())
    }

    fun convertToMilliseconds(timeString: String?): String {
        val format = "HH:mm dd/MM/yyyy"
        var milliseconds: Long = 0
        try {
            val sdf = SimpleDateFormat(format)
            val date = sdf.parse(timeString)
            milliseconds = date.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return milliseconds.toString()
    }
}