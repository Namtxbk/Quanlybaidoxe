package com.example.parkingqr.ui.components.registervehicle

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentVehicleDetailBinding
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.launch

class VehicleDetailFragment : BaseFragment() {

    companion object {
        const val CANCEL_SUCCESSFULLY = "CANCEL_SUCCESSFULLY"
    }

    private lateinit var binding: FragmentVehicleDetailBinding
    private val vehicleDetailViewModel: VehicleDetailViewModel by viewModels()
    private var vehicleId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vehicleId = arguments?.getString(VehicleRegistrationListFragment.VEHICLE_ID)
        vehicleId?.let {
            vehicleDetailViewModel.getVehicleDetail(it)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vehicleDetailViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        vehicleDetailViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                        vehicleDetailViewModel.showMessage()
                    }
                    if (it.vehicleDetail == null) binding.llContainerVehicleDetail.visibility =
                        View.GONE
                    it.vehicleDetail?.let { vehicle ->
                        showVehicleDetail(vehicle)
                    }
                    if (it.isDeleted) {
                        showMessage("Hủy đơn đăng ký thành công")
                        getNavController().previousBackStackEntry?.savedStateHandle?.set(
                            VehicleRegistrationListFragment.ACTION_PASS_BACK,
                            CANCEL_SUCCESSFULLY
                        )
                        getNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentVehicleDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.register_vehicle_fragment_name))
        binding.btnCancelVehicleDetail.setOnClickListener {
            showAlertDialog("Xác nhận", "Bạn có chắc muốn hủy yêu cầu đăng ký?")
        }
    }

    private fun showVehicleDetail(vehicleDetail: VehicleDetail) {

        if(vehicleDetail.state == "verified") {
            binding.btnCancelVehicleDetail.text = "Hủy đăng ký xe"
        }
        else{
            binding.btnCancelVehicleDetail.text = "Hủy yêu cầu đăng ký"
        }

        binding.llContainerVehicleDetail.visibility = View.VISIBLE
        binding.edtLicensePlateVehicleDetail.setText(vehicleDetail.licensePlate)
        binding.edtNameVehicleDetail.setText(vehicleDetail.ownerFullName)
        binding.edtAddressVehicleDetail.setText(vehicleDetail.address)
        binding.edtBrandVehicleDetail.setText(vehicleDetail.brand)
        binding.edtTypeBrandVehicleDetail.setText(vehicleDetail.type)
        binding.edtEngineNumberVehicleDetail.setText(vehicleDetail.engineNumber)
        binding.edtChassisNumberVehicleDetail.setText(vehicleDetail.chassisNumber)
        binding.edtColorVehicleDetail.setText(vehicleDetail.color)
        binding.edtRegisterDayVehicleDetail.setText(vehicleDetail.registrationDate)
        binding.edtExprireDayVehicleDetail.setText(vehicleDetail.expireDate)
        binding.edtCetificateNumberVehicleDetail.setText(vehicleDetail.certificateNumber)

        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.setColorSchemeColors(requireContext().getColor(R.color.main_color))
        circularProgressDrawable.start()

        Glide
            .with(requireContext())
            .load(vehicleDetail.images[0])
            .placeholder(circularProgressDrawable)
            .fitCenter()
            .into(binding.ivFrontVehicleDetail)

        Glide
            .with(requireContext())
            .load(vehicleDetail.images[1])
            .placeholder(circularProgressDrawable)
            .fitCenter()
            .into(binding.ivBackVehicleDetail)

    }
    private fun showAlertDialog(title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            vehicleDetailViewModel.cancelVehicleRegistration(vehicleId!!)
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}