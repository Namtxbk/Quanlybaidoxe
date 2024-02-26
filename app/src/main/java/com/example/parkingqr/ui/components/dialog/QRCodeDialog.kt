package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.example.parkingqr.R

class QRCodeDialog(context: Context, bitmap: Bitmap) {

    var dialog = Dialog(context)
    var bm = bitmap

    fun show() {
        dialog.setContentView(R.layout.qrcode_dialog)
        dialog.findViewById<ImageView>(R.id.ivQrcodeQrcodeDialog).setImageBitmap(bm)
        dialog.setCancelable(true)
        dialog.create()
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}