package com.example.parkingqr.ui.components.userinvoice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.utils.FormatCurrencyUtil
import com.example.parkingqr.utils.TimeUtil

class UserInvoiceListAdapter(private val invoiceList: MutableList<ParkingInvoice>): Adapter<UserInvoiceListAdapter.InvoiceViewHolder>() {

    private var onClickItem: ((ParkingInvoice)-> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invoice_list, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return invoiceList.size
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.bind(invoiceList[position])
    }

    fun setEventClick(callback : ((ParkingInvoice) -> Unit)){
        onClickItem = callback
    }

    inner class InvoiceViewHolder(itemView: View): ViewHolder(itemView){

        private val container: LinearLayout = itemView.findViewById(R.id.llContainerItemInvoiceList)
        private val licensePlate: TextView = itemView.findViewById(R.id.tvLicensePlateInvoiceList)
        private val price: TextView = itemView.findViewById(R.id.tvPriceInvoiceList)
        private val status: TextView = itemView.findViewById(R.id.tvStatusInvoiceList)
        private val timeIn: TextView = itemView.findViewById(R.id.tvTimeInInvoiceList)
        private lateinit var curInvoice: ParkingInvoice

        init {
            container.setOnClickListener {
                onClickItem?.invoke(curInvoice)
            }
        }
        fun bind(invoice: ParkingInvoice){
            curInvoice = invoice
            licensePlate.text = invoice.vehicle.licensePlate
            price.text = FormatCurrencyUtil.formatNumberCeil(invoice.calTotalPrice())
            timeIn.text = TimeUtil.convertMilisecondsToDate(invoice.timeIn)
            bindState()
        }
        private fun bindState(){
            if(curInvoice.state == "parking"){
                status.text = "Xe đang gửi"
                status.setTextColor(itemView.resources.getColor(R.color.light_red))
            }
            else if(curInvoice.state == "parked"){
                status.text = "Đã trả xe"
                status.setTextColor(itemView.resources.getColor(R.color.light_green))
            }
        }
    }
}