package com.example.parkingqr.ui.components.parking

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentParkingBinding
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.dialog.QRCodeDialog
import com.example.parkingqr.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class ParkingFragment : BaseFragment() {

    companion object {
        private const val PIC_CODE_CAR_IN = 123
        private const val PIC_CODE_CAR_OUT = 456
        private const val REQUEST_CAMERA_PERMISSION_CODE = 789
    }

    private lateinit var binding: FragmentParkingBinding
    private lateinit var auth: FirebaseAuth
    private val parkingViewModel: ParkingViewModel by hiltNavGraphViewModels(R.id.parkingFragment)
    private var carNumberIn = ""
    private var carNumberOut = ""
    private var imageCarIn: Bitmap? = null
    private var imageCarOut: Bitmap? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_CODE_CAR_IN && resultCode == RESULT_OK) {
            imageCarIn = data?.extras!!["data"] as Bitmap?
            if (imageCarIn != null) {
                binding.ivPhotoCarInParking.setImageBitmap(imageCarIn)
                TextRecognizerUtil.invoke(imageCarIn!!) {
                    carNumberIn = it
                }
            }
        }
        if (requestCode == PIC_CODE_CAR_OUT && resultCode == RESULT_OK) {
            imageCarOut = data?.extras!!["data"] as Bitmap?
            if (imageCarOut != null) {
                binding.ivPhotoCarOutParking.setImageBitmap(imageCarOut)
                TextRecognizerUtil.invoke(imageCarOut!!) {
                    carNumberOut = it
                }
            }
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parkingViewModel.stateUi.collect {
                    when (it.state) {
                        ParkingViewModel.ParkingState.BLANK -> {
                            hideLoading()
                            handleRefresh()
                        }
                        ParkingViewModel.ParkingState.LOADING -> {
                            showLoading()
                        }
                        ParkingViewModel.ParkingState.ERROR -> {
                            hideLoading()
                            showError(it.errorMessage)
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_CREATE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.messageList[it.state]}: $carNumberIn")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_FOUND_VEHICLE -> {
                            hideLoading()
                            binding.tvStateMessageParking.text = "Hóa đơn gửi xe cho xe có đăng ký"
                            binding.ivCarInParking.setBackgroundResource(R.drawable.create)
                            binding.tvCarInParking.text = "Tạo"
                            displayParkingInvoice(it.parkingInvoice!!)
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE -> {
                            hideLoading()
                            if (carNumberOut.isNotEmpty()) {
                                if (carNumberOut == it.parkingInvoice?.vehicle?.licensePlate) {
                                    showMessage("Biển số xe khớp nhau: ${it.parkingInvoice.vehicle.licensePlate}")
                                    binding.tvStateMessageParking.text = "Biển số xe khớp nhau"
                                } else {
                                    showMessage("Biển số xe không khớp nhau: ${it.parkingInvoice?.vehicle?.licensePlate} và $carNumberOut")
                                    binding.tvStateMessageParking.text =
                                        "Biển số xe không khớp nhau: ${it.parkingInvoice?.vehicle?.licensePlate} và $carNumberOut"
                                }
                            } else {
                                showMessage("${it.messageList[it.state]}: ${it.parkingInvoice?.vehicle?.licensePlate}")
                            }
                            displayParkingInvoice(it.parkingInvoice!!)
                            binding.ivCarOutParking.setBackgroundResource(R.drawable.confirm)
                            imageCarOut?.let {bm ->
                                binding.ivPhotoCarOutParking.setImageBitmap(bm)
                            }
                            binding.tvCarOutParking.text = "Trả xe"
                        }
                        ParkingViewModel.ParkingState.FAIL_CREATE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                        }
                        ParkingViewModel.ParkingState.PARKED_VEHICLE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                        }
                        ParkingViewModel.ParkingState.FAIL_FOUND_VEHICLE -> {
                            hideLoading()
                            binding.ivCarInParking.setBackgroundResource(R.drawable.create)
                            binding.tvCarInParking.text = "Tạo"
                            binding.tvStateMessageParking.text =
                                "Hóa đơn gửi xe cho xe chưa đăng ký"
                            displayParkingInvoice(it.parkingInvoice!!)
                        }
                        ParkingViewModel.ParkingState.FAIL_SEARCH_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.PARKED_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_COMPLETE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.messageList[it.state]}: ${it.parkingInvoice?.vehicle?.licensePlate}")
                            parkingViewModel.refreshData()
                        }
                        ParkingViewModel.ParkingState.FAIL_COMPLETE_PARKING_INVOICE -> {
                            hideLoading()
                            showMessage("${it.errorList[it.state]}")
                        }
                        ParkingViewModel.ParkingState.SUCCESSFUL_GET_QR_CODE -> {
                            parkingViewModel.searchParkingInvoiceById(it.qrcode)
                        }
                        ParkingViewModel.ParkingState.FAIL_GET_QR_CODE -> {
                            showMessage("${it.errorList[it.state]}")
                            parkingViewModel.refreshData()
                        }
                    }
                }

            }
        }
    }

    override fun initViewBinding(): View {
        auth = Firebase.auth
        binding = FragmentParkingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.parking_fragment_name))
        binding.edtPaymentMethodParking.inputType = InputType.TYPE_NULL
        binding.edtInvoiceTypeParking.inputType = InputType.TYPE_NULL

        binding.llPaymentMethodParking.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.menuInflater.inflate(R.menu.payment_method_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                binding.edtPaymentMethodParking.setText(item.title)
                true
            }
            popupMenu.show()
        }

        binding.llInvoiceTypeParking.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.menuInflater.inflate(R.menu.invoice_type_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                binding.edtInvoiceTypeParking.setText(item.title)
                true
            }
            popupMenu.show()
        }

        binding.ivPhotoCarInParking.setOnClickListener {
            if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.BLANK) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, PIC_CODE_CAR_IN)
            } else {
                showMessage("Vui lòng xử lí giao dịch hiện tại trước")
            }
        }
        binding.ivPhotoCarOutParking.setOnClickListener {
            if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.BLANK) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, PIC_CODE_CAR_OUT)
            } else {
                showMessage("Vui lòng xử lí giao dịch hiện tại trước")
            }
        }
        binding.ivLBlankCarParking.setOnClickListener {
            if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.BLANK) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, PIC_CODE_CAR_IN)
            } else {
                showMessage("Vui lòng xử lí giao dịch hiện tại trước")
            }
        }
        binding.llButtonCarIn.setOnClickListener {
            handleCarIn()
        }
        binding.llButtonCarOut.setOnClickListener {
            handleCarOut()
        }
        binding.ivQrcodeParking.setOnClickListener {
            showInvoiceQRCode()
        }
        binding.tvRefreshParking.setOnClickListener {
            parkingViewModel.refreshData()
            handleRefresh()
        }
    }
    private fun showInvoiceQRCode(){
        QRCodeDialog(
            context!!,
            QRcodeUtil.getQrCodeBitmap(
                parkingViewModel.stateUi.value.parkingInvoice?.id ?: ""
            )
        ).show()
    }

    private fun handleRefresh() {
        binding.ivCarInParking.setBackgroundResource(R.drawable.image_search)
        binding.tvCarInParking.text = "Tìm kiếm"
        binding.ivCarOutParking.setBackgroundResource(R.drawable.qr_scan)
        binding.tvCarOutParking.text = "Quét"
        binding.llBlankCarParking.visibility = View.VISIBLE
        binding.llContainerParking.visibility = View.INVISIBLE
        binding.llParkingInvoiceCarinParking.visibility = View.GONE
        binding.tvStateMessageParking.text = "Hóa đơn gửi xe hiện đang trống"
        binding.ivPhotoCarOutParking.setImageResource(R.drawable.img_car_out)
        binding.ivPhotoCarInParking.setImageResource(R.drawable.img_car_in)
        carNumberOut = ""
        carNumberIn = ""
        imageCarIn = null
        imageCarOut = null
    }

    private fun handleCarIn() {
        if (imageCarIn == null) {
            showMessage("Chưa có ảnh xe vào")
        } else {
            if (carNumberIn.isEmpty()) {
                showMessage("Không nhận dạng dược ảnh xe vào")
            } else {
                if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.SUCCESSFUL_FOUND_VEHICLE
                    || parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.FAIL_FOUND_VEHICLE
                    || parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.PARKED_VEHICLE
                ) {
                    createNewInvoice()
                } else {
                    searchVehicle()
                }
            }
        }
    }
    private fun createNewInvoice(){
        val note = binding.edtNoteParking.text.toString()
        val paymentMethod = binding.edtPaymentMethodParking.text.toString()
        val type = binding.edtInvoiceTypeParking.text.toString()

        parkingViewModel.updateInvoiceIn(paymentMethod, type, note)
        parkingViewModel.addNewParkingInvoice()
    }

    private fun searchVehicle(){
        if (LicensePlateUtil.checkLicensePlateValid(carNumberIn)) {
            parkingViewModel.searchVehicleAndUserByLicensePlate(
                licensePlate = carNumberIn,
                imageCarIn = imageCarIn!!
            )
        } else {
            showMessage("Biển số xe không hợp lệ: $carNumberIn")
        }
    }

    private fun handleCarOut() {
        if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE) {
            val note = binding.edtNoteParking.text.toString()
            val paymentMethod = binding.edtPaymentMethodParking.text.toString()
            val type = binding.edtInvoiceTypeParking.text.toString()
            var imgOutString = ""

            imageCarOut?.let {
                imgOutString = ImageUtil.encodeImage(it)
            }
            parkingViewModel.updateInvoiceOut(paymentMethod, type, imgOutString, note)
            parkingViewModel.completeParkingInvoice()
        } else {
            if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.BLANK) {
                handleOpenQRScan()
            } else {
                showMessage("Vui lòng xử lí giao dịch hiện tại trước")
            }
        }
    }

    private fun handleOpenQRScan() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION_CODE
            )
        } else {
            getNavController().navigate(R.id.scanFragment)
        }
    }

    private fun displayImageVehicleIn(parkingInvoice: ParkingInvoice) {
        if (parkingViewModel.stateUi.value.state == ParkingViewModel.ParkingState.SUCCESSFUL_SEARCH_PARKING_INVOICE) {
            binding.llParkingInvoiceCarinParking.visibility = View.VISIBLE
            if (parkingInvoice.imageIn.isNotEmpty()) {
                val circularProgressDrawable = CircularProgressDrawable(requireContext())
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 10f
                circularProgressDrawable.setColorSchemeColors(requireContext().getColor(R.color.main_color))
                circularProgressDrawable.start()

                Glide
                    .with(requireContext())
                    .load(parkingInvoice.imageIn)
                    .placeholder(circularProgressDrawable)
                    .fitCenter()
                    .into(binding.ivParkingInvoiceCarInParking)
            }
            else binding.ivParkingInvoiceCarInParking.setImageResource(R.drawable.img)
        } else binding.llParkingInvoiceCarinParking.visibility = View.GONE
    }

    private fun displayParkingInvoice(parkingInvoice: ParkingInvoice) {
        binding.llBlankCarParking.visibility = View.GONE
        binding.llContainerParking.visibility = View.VISIBLE
        displayImageVehicleIn(parkingInvoice)

        binding.apply {
            edtNoteParking.setText(parkingInvoice.note)
            edtNameParking.setText(parkingInvoice.user.name)
            edtLicensePlateParking.setText(parkingInvoice.vehicle.licensePlate)
            edtTimeInParking.setText(TimeUtil.convertMilisecondsToDate(parkingInvoice.timeIn))
            edtVehicleTypeParking.setText(parkingInvoice.type)
            edtPaymentMethodParking.setText(parkingInvoice.paymentMethod)
            edtPriceParking.setText("${FormatCurrencyUtil.formatNumberCeil(parkingInvoice.calTotalPrice())} VND")
            edtInvoiceTypeParking.setText(parkingInvoice.type)
            if (!parkingInvoice.user.name.isNullOrEmpty()) {
                edtNameParking.setText(parkingInvoice.user.name)
            } else {
                edtNameParking.setText("Không có")
            }
            if (!parkingInvoice.vehicle.type.isNullOrEmpty()) {
                edtVehicleTypeParking.setText(parkingInvoice.vehicle.type)
            } else {
                edtVehicleTypeParking.setText("Không có")
            }
        }
    }
}