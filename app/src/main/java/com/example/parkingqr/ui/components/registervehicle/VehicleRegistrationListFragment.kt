package com.example.parkingqr.ui.components.registervehicle

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentVehicleRegistrationListBinding
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.launch

class VehicleRegistrationListFragment : BaseFragment() {

    companion object {
        const val VEHICLE_ID = "VEHICLE_ID"
        const val ACTION_PASS_BACK = "ACTION_PASS_BACK"
    }

    private lateinit var binding: FragmentVehicleRegistrationListBinding
    private val vehicleRegistrationListViewModel: VehicleRegistrationListViewModel by viewModels()
    private lateinit var vehicleRegistrationListAdapter: VehicleRegistrationListAdapter
    private val registrationList = mutableListOf<VehicleDetail>()

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vehicleRegistrationListViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        vehicleRegistrationListViewModel.showError()
                    }
                    if (registrationList.isEmpty()) registrationList.addAll(it.registrationList)
                    else {
                        registrationList.clear()
                        registrationList.addAll(it.registrationList)
                    }
                    vehicleRegistrationListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentVehicleRegistrationListBinding.inflate(layoutInflater)
        vehicleRegistrationListAdapter = VehicleRegistrationListAdapter(registrationList)
        vehicleRegistrationListAdapter.setClickEvent {
            onClickItem(it)
        }
        binding.rlvVehicleRegistrationList.apply {
            adapter = vehicleRegistrationListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.vehicle_registration_list_fragment_name))

        getNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
            ACTION_PASS_BACK
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            if (result == RegisterVehicleFragment.ADD_SUCCESSFULLY) {
                vehicleRegistrationListViewModel.getRegistrationList()
            } else if (result == VehicleDetailFragment.CANCEL_SUCCESSFULLY) {
                vehicleRegistrationListViewModel.getRegistrationList()
            }
            getNavController().currentBackStackEntry?.savedStateHandle?.remove<String>(
                ACTION_PASS_BACK
            )
        }
        binding.tvRegisterVehicleRegistrationList.setOnClickListener {
            getNavController().navigate(R.id.registerVehicleFragment)
        }
    }

    private fun onClickItem(vehicleDetail: VehicleDetail) {
        val bundle = Bundle()
        bundle.putString(VEHICLE_ID, vehicleDetail.id)
        getNavController().navigate(R.id.vehicleDetailFragment, bundle)
    }

}