package com.example.parkingqr.ui.components.invoice

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentInvoiceDetailBinding
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.dialog.QRCodeDialog
import com.example.parkingqr.utils.FormatCurrencyUtil
import com.example.parkingqr.utils.QRcodeUtil
import com.example.parkingqr.utils.TimeUtil
import kotlinx.coroutines.launch

class InvoiceDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentInvoiceDetailBinding
    private lateinit var invoiceId: String
    private val invoiceDetailViewModel: InvoiceDetailViewModel by viewModels()
    private var isEditing = false
    private var parkingInvoice: ParkingInvoice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceId = arguments?.getString(InvoiceListFragment.INVOICE_ID_KEY) ?: "-1"
        if (invoiceId != "-1") {
            invoiceDetailViewModel.getInvoiceById(invoiceId)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                invoiceDetailViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        invoiceDetailViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                    }
                    if (it.invoice == null) binding.llWrapAllInvoiceDetail.visibility = View.GONE
                    it.invoice?.let {invoice ->
                        showInvoice(invoice)
                        parkingInvoice = invoice
                    }
                    if(it.isSaved || it.isRefused || it.isConfirmed){
                        getNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentInvoiceDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar("Xe đang gửi")
        binding.llPaymentMethodInvoiceDetail.setOnClickListener {
            if (isEditing) {
                val popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.payment_method_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    binding.edtPaymentMethodInvoiceDetail.setText(item.title)
                    true
                }
                popupMenu.show()
            }
        }
        binding.llInvoiceTypeInvoiceDetail.setOnClickListener {
            if (isEditing) {
                val popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.invoice_type_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    binding.edtInvoiceTypeInvoiceDetail.setText(item.title)
                    true
                }
                popupMenu.show()
            }
        }
        binding.btnSaveInvoiceDetail.setOnClickListener {
            handleSaveInvoice()
        }
        binding.ivQrcodeInvoiceDetail.setOnClickListener {
            QRCodeDialog(
                context!!,
                QRcodeUtil.getQrCodeBitmap(
                    invoiceDetailViewModel.stateUi.value.invoice?.id ?: ""
                )
            ).show()
        }
        binding.ivEditInvoiceDetail.setOnClickListener {
            isEditing = !isEditing
            showInvoice(parkingInvoice!!)
        }
        binding.btnConfirmInvoiceDetail.setOnClickListener {
            handleConfirmInvoice()
        }
        binding.btnRefuseInvoiceDetail.setOnClickListener {
            handleRefuseInvoice()
        }
    }

    private fun showInvoice(parkingInvoice: ParkingInvoice) {
        binding.llWrapAllInvoiceDetail.visibility = View.VISIBLE
        binding.edtTimeInInvoiceDetail.setText(TimeUtil.convertMilisecondsToDate(parkingInvoice.timeIn))
        binding.edtLicensePlateInvoiceDetail.setText(parkingInvoice.vehicle.licensePlate)
        binding.edtPaymentMethodInvoiceDetail.setText(parkingInvoice.paymentMethod)
        binding.edtInvoiceTypeInvoiceDetail.setText(parkingInvoice.type)
        binding.edtTimeOutInvoiceDetail.setText(
            TimeUtil.convertMilisecondsToDate(
                parkingInvoice.timeOut
            )
        )
        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.setColorSchemeColors(requireContext().getColor(R.color.main_color))
        circularProgressDrawable.start()

        Glide
            .with(requireContext())
            .load(parkingInvoice.imageIn)
            .placeholder(circularProgressDrawable)
            .fitCenter()
            .into(binding.ivCarInInvoiceDetail)

        if (parkingInvoice.state == "parking") {
            binding.llCarOutInvoiceDetail.visibility = View.GONE
            binding.edtStateInvoiceDetail.setText("Xe đang gửi")
            binding.llTimeOutInvoiceDetail.visibility = View.GONE
        } else {
            if (parkingInvoice.imageOut.isNotEmpty()) {
                binding.llCarOutInvoiceDetail.visibility = View.VISIBLE
                Glide
                    .with(requireContext())
                    .load(parkingInvoice.imageOut)
                    .placeholder(circularProgressDrawable)
                    .fitCenter()
                    .into(binding.ivCarOutInvoiceDetail)
            }
            binding.edtStateInvoiceDetail.setText("Đã trả xe")
            binding.llTimeOutInvoiceDetail.visibility = View.VISIBLE
        }

        binding.edtNoteInvoiceDetail.setText(parkingInvoice.note)

        binding.tvPriceInvoiceDetail.text =
            "Giá tiền: ${FormatCurrencyUtil.formatNumberCeil(parkingInvoice.calTotalPrice())} VND"

        if (!parkingInvoice.user.name.isNullOrEmpty()) {
            binding.edtIsRegisterInvoiceDetail.setText("Xe đã đăng ký")
        } else {
            binding.edtIsRegisterInvoiceDetail.setText("Xe chưa đăng ký")
        }

        if (!parkingInvoice.vehicle.type.isNullOrEmpty()) {
            binding.edtVehicleTypeInvoiceDetail.setText(parkingInvoice.vehicle.type)
        } else {
            binding.edtVehicleTypeInvoiceDetail.setText("Không có")
        }

        if (!parkingInvoice.user.name.isNullOrEmpty()) {
            binding.edtNameInvoiceDetail.setText(parkingInvoice.user.name)
        } else {
            binding.edtNameInvoiceDetail.setText("Không có")
        }
        if (parkingInvoice.getState() == ParkingInvoice.ParkingState.PARKING) {
            showParkingState()
        } else if (parkingInvoice.getState() == ParkingInvoice.ParkingState.PARKED) {
            showParkedState()
        } else {
            showRefusedState()
        }

    }
    private fun showParkingState(){
        binding.ivEditInvoiceDetail.visibility = View.VISIBLE
        if (isEditing) {
            binding.btnConfirmInvoiceDetail.visibility = View.GONE
            binding.btnRefuseInvoiceDetail.visibility = View.GONE
            binding.btnSaveInvoiceDetail.visibility = View.VISIBLE
            binding.edtNoteInvoiceDetail.inputType = InputType.TYPE_CLASS_TEXT
            binding.llPaymentMethodInvoiceDetail.setBackgroundResource(R.drawable.rounded_edit_text_white)
            binding.llInvoiceTypeInvoiceDetail.setBackgroundResource(R.drawable.rounded_edit_text_white)
            binding.edtNoteInvoiceDetail.setBackgroundResource(R.drawable.rounded_edit_text_white)
            binding.ivEditInvoiceDetail.setBackgroundResource(R.drawable.cancel)
        } else {
            binding.btnConfirmInvoiceDetail.visibility = View.VISIBLE
            binding.btnRefuseInvoiceDetail.visibility = View.VISIBLE
            binding.btnSaveInvoiceDetail.visibility = View.GONE
            binding.llPaymentMethodInvoiceDetail.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.llInvoiceTypeInvoiceDetail.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.edtNoteInvoiceDetail.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.edtNoteInvoiceDetail.inputType = InputType.TYPE_NULL
            binding.ivEditInvoiceDetail.setBackgroundResource(R.drawable.edit)
        }
    }
    private fun showParkedState(){
        binding.ivEditInvoiceDetail.visibility = View.GONE
        binding.btnConfirmInvoiceDetail.visibility = View.GONE
        binding.btnRefuseInvoiceDetail.visibility = View.GONE
        binding.btnSaveInvoiceDetail.visibility = View.GONE
    }
    private fun showRefusedState(){
        binding.ivEditInvoiceDetail.visibility = View.GONE
        binding.btnConfirmInvoiceDetail.visibility = View.GONE
        binding.btnRefuseInvoiceDetail.visibility = View.GONE
        binding.btnSaveInvoiceDetail.visibility = View.GONE
    }

    private fun handleSaveInvoice() {
        invoiceDetailViewModel.saveInvoice(
            _type = binding.edtInvoiceTypeInvoiceDetail.text.toString(),
            _paymentMethod = binding.edtPaymentMethodInvoiceDetail.text.toString(),
            _note = binding.edtNoteInvoiceDetail.text.toString()
        )
    }
    private fun handleConfirmInvoice() {
        invoiceDetailViewModel.confirmInvoice()
    }

    private fun handleRefuseInvoice() {
        invoiceDetailViewModel.refuseInvoice()
    }

}