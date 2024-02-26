package com.example.parkingqr.ui.base

import android.app.Dialog
import android.content.Context
import com.example.parkingqr.R

class LoadingDialog(context: Context) {

    var dialog = Dialog(context)

    fun show() {
        dialog.setContentView(R.layout.loading_dialog)
        dialog.setCancelable(false)
        dialog.create()
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}