package com.example.parkingqr.ui.components.home

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentHomeBinding
import com.example.parkingqr.ui.base.BaseFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HomeFragment: BaseFragment() {


    private lateinit var binding: FragmentHomeBinding

    override fun observeViewModel() {

    }

    override fun initViewBinding(): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        binding.ivParkingHome.setOnClickListener{
            getNavController().navigate(R.id.parkingFragment)
        }
        binding.ivInvoiceListHome.setOnClickListener{
            getNavController().navigate(R.id.invoiceListFragment)
        }
        binding.ivSettingHome.setOnClickListener {
            showMessage("Ứng dụng hiện chưa hỗ trợ tính năng này")
        }
        binding.ivSecurityCameraHome.setOnClickListener {
            showMessage("Ứng dụng hiện chưa hỗ trợ tính năng này")
        }
    }
}