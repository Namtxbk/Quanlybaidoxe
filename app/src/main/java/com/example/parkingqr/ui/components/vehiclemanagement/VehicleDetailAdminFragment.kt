package com.example.parkingqr.ui.components.vehiclemanagement

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
import com.example.parkingqr.databinding.FragmentVehicleDetailAdminBinding
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.vehiclemanagement.VehicleManagementFragment.Companion.ACTION_PASS_BACK
import com.example.parkingqr.ui.components.vehiclemanagement.VehicleManagementFragment.Companion.VEHICLE_ID
import kotlinx.coroutines.launch

class VehicleDetailAdminFragment : BaseFragment() {

    companion object {
        const val UPDATE_SUCCESSFULLY = "UPDATE_SUCCESSFULLY"
    }

    private lateinit var binding: FragmentVehicleDetailAdminBinding
    private val vehicleDetailAdminViewModel: VehicleDetailAdminViewModel by viewModels()
    private var vehicleId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vehicleId = arguments?.getString(VEHICLE_ID)
        vehicleId?.let {
            vehicleDetailAdminViewModel.getVehicleDetail(it)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vehicleDetailAdminViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        vehicleDetailAdminViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                    }
                    if (it.vehicleDetail == null) binding.llContainerVehicleDetailAdmin.visibility =
                        View.GONE
                    it.vehicleDetail?.let { vehicle ->
                        showVehicleDetail(vehicle)
                    }
                    if (it.isAccepted || it.isRefuse || it.isPending) {
                        getNavController().previousBackStackEntry?.savedStateHandle?.set(
                            ACTION_PASS_BACK,
                            UPDATE_SUCCESSFULLY
                        )
                        getNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentVehicleDetailAdminBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.vehicle_management_fragment_name))
        binding.btnConfirmVehicleDetailAdmin.setOnClickListener{
            vehicleDetailAdminViewModel.acceptVehicle()
        }
        binding.btnRefuseVehicleDetailAdmin.setOnClickListener{
            showAlertDialog("Xác nhận", "Bạn muốn từ chối đơn đăng ký xe?")
        }
        binding.btnRollBackVehicleDetailAdmin.setOnClickListener{
            vehicleDetailAdminViewModel.pendingVehicle()
        }
    }

    private fun showVehicleDetail(vehicleDetail: VehicleDetail) {

        if(vehicleDetail.getState() == VehicleDetail.VehicleState.PENDING){
            binding.btnConfirmVehicleDetailAdmin.visibility = View.VISIBLE
            binding.btnRefuseVehicleDetailAdmin.visibility = View.VISIBLE
            binding.btnRollBackVehicleDetailAdmin.visibility = View.GONE
        }
        else if(vehicleDetail.getState() == VehicleDetail.VehicleState.VERIFIED){
            binding.btnConfirmVehicleDetailAdmin.visibility = View.GONE
            binding.btnRefuseVehicleDetailAdmin.visibility = View.GONE
            binding.btnRollBackVehicleDetailAdmin.visibility = View.VISIBLE
        }
        else{
            binding.btnConfirmVehicleDetailAdmin.visibility = View.GONE
            binding.btnRefuseVehicleDetailAdmin.visibility = View.GONE
            binding.btnRollBackVehicleDetailAdmin.visibility = View.VISIBLE
        }

        binding.llContainerVehicleDetailAdmin.visibility = View.VISIBLE
        binding.edtLicensePlateVehicleDetailAdmin.setText(vehicleDetail.licensePlate)
        binding.edtNameVehicleDetailAdmin.setText(vehicleDetail.ownerFullName)
        binding.edtAddressVehicleDetailAdmin.setText(vehicleDetail.address)
        binding.edtBrandVehicleDetailAdmin.setText(vehicleDetail.brand)
        binding.edtTypeBrandVehicleDetailAdmin.setText(vehicleDetail.type)
        binding.edtEngineNumberVehicleDetailAdmin.setText(vehicleDetail.engineNumber)
        binding.edtChassisNumberVehicleDetailAdmin.setText(vehicleDetail.chassisNumber)
        binding.edtColorVehicleDetailAdmin.setText(vehicleDetail.color)
        binding.edtRegisterDayVehicleDetailAdmin.setText(vehicleDetail.registrationDate)
        binding.edtExprireDayVehicleDetailAdmin.setText(vehicleDetail.expireDate)
        binding.edtCetificateNumberVehicleDetailAdmin.setText(vehicleDetail.certificateNumber)

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
            .into(binding.ivFrontVehicleDetailAdmin)

        Glide
            .with(requireContext())
            .load(vehicleDetail.images[1])
            .placeholder(circularProgressDrawable)
            .fitCenter()
            .into(binding.ivBackVehicleDetailAdmin)

    }
    private fun showAlertDialog(title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("Có") { dialog, _ ->
            vehicleDetailAdminViewModel.refuseVehicle()
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Không") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}