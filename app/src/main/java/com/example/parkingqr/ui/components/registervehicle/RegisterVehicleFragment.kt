package com.example.parkingqr.ui.components.registervehicle

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.BuildConfig
import com.example.parkingqr.databinding.FragmentRegisterVehicleBinding
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.utils.LicensePlateUtil
import com.example.parkingqr.utils.TimeUtil
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class RegisterVehicleFragment : BaseFragment() {

    companion object {
        private const val PIC_CODE_FRONT = 123
        private const val PIC_CODE_BACK = 456
        private const val REQUEST_CAMERA_PERMISSION_CODE = 789
        const val ADD_SUCCESSFULLY = "ADD_SUCCESSFULLY"
    }

    private lateinit var binding: FragmentRegisterVehicleBinding
    private val registerVehicleViewModel: RegisterVehicleViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private var imageFront: Uri? = null
    private var imageBack: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PIC_CODE_FRONT) {
                imageFront = data!!.data
                binding.ivFrontRegisterVehicle.setImageURI(imageFront)
                binding.ivFrontRegisterVehicle.visibility = View.VISIBLE
            }
            if (requestCode == PIC_CODE_BACK) {
                imageBack = data!!.data
                binding.ivBackRegisterVehicle.setImageURI(imageBack)
                binding.ivBackRegisterVehicle.visibility = View.VISIBLE
            }
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerVehicleViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        registerVehicleViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                        registerVehicleViewModel.showMessage()
                    }
                    if (it.isCreated) {
                        showMessage("Tạo đơn đăng ký thành công")
                        getNavController().previousBackStackEntry?.savedStateHandle?.set(
                            VehicleRegistrationListFragment.ACTION_PASS_BACK, ADD_SUCCESSFULLY
                        )
                        getNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentRegisterVehicleBinding.inflate(layoutInflater)
        binding.edtRegisterDayRegisterVehicle.inputType = InputType.TYPE_NULL
        binding.edtExprireDayRegisterVehicle.inputType = InputType.TYPE_NULL
        return binding.root
    }

    override fun initListener() {
        showActionBar("Đăng ký xe")
        binding.edtRegisterDayRegisterVehicle.setOnClickListener {
            showDatePickerDialog(binding.edtRegisterDayRegisterVehicle)
        }
        binding.edtExprireDayRegisterVehicle.setOnClickListener {
            showDatePickerDialog(binding.edtExprireDayRegisterVehicle)
        }
        binding.ivAddFrontRegisterVehicle.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(i, PIC_CODE_FRONT)
        }
        binding.ivAddBackRegisterVehicle.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(i, PIC_CODE_BACK)
        }
        binding.btnConfirmRegisterVehicle.setOnClickListener {
            handleRegister()
        }
        if (Build.VERSION.SDK_INT < 30) {
            if (checkSelfPermission(
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PermissionChecker.PERMISSION_DENIED
            ) {
                Log.v("TAG", "Permission Denied")
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1234)
            } else
                Log.v("TAG", "Permission Granted")
        } else {
            if (!Environment.isExternalStorageManager()) {
                Log.v("TAG", "Permission Denied")
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        uri
                    )
                )
            } else {
                Log.v("TAG", "Permission Granted")
            }
        }
    }


    private fun showDatePickerDialog(editText: EditText) {
        val datePickerDialog = DatePickerDialog(
            context!!,
            { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Xử lý khi ngày được chọn
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateEditText(editText)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateEditText(editText: EditText) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        editText.setText(dateFormat.format(calendar.time))
    }

    private fun handleRegister() {
        var check = true
        val licensePlate = binding.edtLicensePlateRegisterVehicle.text.toString().uppercase()
        val name = binding.edtNameRegisterVehicle.text.toString()
        val address = binding.edtAddressRegisterVehicle.text.toString()
        val brand = binding.edtBrandRegisterVehicle.text.toString()
        val typeBrand = binding.edtTypeBrandRegisterVehicle.text.toString()
        val engineNumber = binding.edtEngineNumberRegisterVehicle.text.toString()
        val chassisNumber = binding.edtChassisNumberRegisterVehicle.text.toString()
        val color = binding.edtColorRegisterVehicle.text.toString()
        val registerDay = binding.edtRegisterDayRegisterVehicle.text.toString()
        val expireDay = binding.edtExprireDayRegisterVehicle.text.toString()
        val certificateNumber = binding.edtCetificateNumberRegisterVehicle.text.toString()

        if (!LicensePlateUtil.checkLicensePlateValid(licensePlate)) {
            binding.edtLicensePlateRegisterVehicle.error = "Biển số xe không hợp lệ"
            check = false
        }
        if (name.isEmpty()) {
            binding.edtNameRegisterVehicle.error = "Tên không được rỗng"
            check = false
        }
        if (certificateNumber.isEmpty()) {
            binding.edtCetificateNumberRegisterVehicle.error = "Số giấy chứng nhận không được rỗng"
            check = false
        }
        if (address.isEmpty()) {
            binding.edtAddressRegisterVehicle.error = "Địa chỉ không được rỗng"
            check = false
        }
        if (brand.isEmpty()) {
            binding.edtBrandRegisterVehicle.error = "Nhãn hiệu không được rỗng"
            check = false
        }
        if (typeBrand.isEmpty()) {
            binding.edtTypeBrandRegisterVehicle.error = "Số loại không được rỗng"
            check = false
        }
        if (engineNumber.isEmpty()) {
            binding.edtEngineNumberRegisterVehicle.error = "Số máy không được rỗng"
            check = false
        }
        if (chassisNumber.isEmpty()) {
            binding.edtChassisNumberRegisterVehicle.error = "Số khung không được rỗng"
            check = false
        }
        if (color.isEmpty()) {
            binding.edtColorRegisterVehicle.error = "Màu sơn không được rỗng"
            check = false
        }
        if (registerDay.isEmpty()) {
            binding.edtRegisterDayRegisterVehicle.error = "Ngày đăng ký không được rỗng"
            check = false
        }
        if (expireDay.isEmpty()) {
            binding.edtExprireDayRegisterVehicle.error = "Ngày hết hạn không được rỗng"
            check = false
        }
        if (imageFront == null || imageBack == null) {
            showError("Vui lòng chụp ảnh giấy chứng nhận xe của bạn đủ 2 mặt")
            check = false
        }
        if (check) {
        val imageList = mutableListOf<String>()
        imageList.add(imageFront.toString())
        imageList.add(imageBack.toString())
        val vehicle = VehicleDetail(
            id = "-1",
            createAt = TimeUtil.getCurrentTime().toString(),
            userId = "-1",
            licensePlate = licensePlate,
            state = "unverified",
            brand = brand,
            type = typeBrand,
            color = color,
            registrationDate = registerDay,
            expireDate = expireDay,
            chassisNumber = chassisNumber,
            engineNumber = engineNumber,
            ownerFullName = name,
            address = address,
            certificateNumber = certificateNumber,
            images = imageList
        )
        registerVehicleViewModel.createVehicleRegistrationForm(vehicle)
        }
    }

}