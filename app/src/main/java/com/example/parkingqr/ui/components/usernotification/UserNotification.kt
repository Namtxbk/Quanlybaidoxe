package com.example.parkingqr.ui.components.usernotification

import android.view.View
import com.example.parkingqr.databinding.FragmentMyNotificationBinding
import com.example.parkingqr.ui.base.BaseFragment

class UserNotification: BaseFragment() {
    private lateinit var binding: FragmentMyNotificationBinding
    override fun observeViewModel() {

    }

    override fun initViewBinding(): View {
        binding = FragmentMyNotificationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        showBottomNavigation()
    }
}