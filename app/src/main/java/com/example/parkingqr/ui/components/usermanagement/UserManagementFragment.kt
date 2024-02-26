package com.example.parkingqr.ui.components.usermanagement

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentUserManagementBinding
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.invoice.InvoiceListFragment
import com.example.parkingqr.ui.components.invoice.InvoiceListViewModel
import kotlinx.coroutines.launch

class UserManagementFragment : BaseFragment() {

    companion object {
        const val USER_DETAIL_KEY = "USER_DETAIL_KEY"
    }

    private lateinit var binding: FragmentUserManagementBinding
    private val userList = mutableListOf<UserDetail>()
    private lateinit var userAdapter: UserManagementAdapter
    private val userManagementViewModel: UserManagementViewModel by hiltNavGraphViewModels(R.id.userManagementFragment)

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userManagementViewModel.stateUi.collect {

                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        userManagementViewModel.showError()
                    }
                    if(it.message.isNotEmpty()){
                        showMessage(it.message)
                        userManagementViewModel.showMessage()
                    }

                    if(it.isSignedOut){
                        getNavController().navigate(R.id.loginFragment)
                    }

                    if (userList.isEmpty()) userList.addAll(it.userList)
                    else {
                        userList.clear()
                        userList.addAll(it.userList)
                    }
                    userAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentUserManagementBinding.inflate(layoutInflater)
        userAdapter = UserManagementAdapter(userList)
        userAdapter.setEventClick {
            handleClickItem(it)
        }
        userAdapter.setOnClickMore {
            handleClickMore(it)
        }
        binding.rlvUserListUserManagement.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        binding.ivApplication.setOnClickListener {
            handleShowMenu()
        }
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        userManagementViewModel.getUserList()
    }

    private fun handleShowMenu(){
        val options = arrayOf("Quản lý tài khoản", "Quản lý đăng ký xe", "Đăng xuất")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Chọn tính năng")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    getNavController().navigate(R.id.userManagementFragment)
                }
                1 -> {
                    getNavController().navigate(R.id.vehicleManagementFragment)
                }
                2 ->{
                    handleSignOut()
                }
            }
        }
        builder.setNegativeButton("Hủy") { dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun handleClickMore(userDetail: UserDetail) {

        val options = if (userDetail.getStatus() == UserDetail.UserStatus.ACTIVE) arrayOf(
            "Xóa người dùng",
            "Chặn người dùng",
        ) else arrayOf("Xóa người dùng", "Bỏ chặn người dùng")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Chọn hành động")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    handleDeleteUser(userDetail)
                }
                1 -> {
                    if (userDetail.getStatus() == UserDetail.UserStatus.ACTIVE) {
                        handleBlockUser(userDetail)
                    } else {
                        handleUnBlockUser(userDetail)
                    }
                }
                2 ->{
                    handleSignOut()
                }
            }
        }
        builder.setNegativeButton("Hủy") { dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun handleSignOut(){
        userManagementViewModel.signOut()
    }

    private fun handleDeleteUser(userDetail: UserDetail) {
        userManagementViewModel.deleteUser(userDetail)
    }

    private fun handleBlockUser(userDetail: UserDetail) {
        userManagementViewModel.blockUser(userDetail)
    }

    private fun handleUnBlockUser(userDetail: UserDetail) {
        userManagementViewModel.activeUser(userDetail)
    }

    private fun handleClickItem(userDetail: UserDetail) {
        val bundle = Bundle()
        bundle.putString(USER_DETAIL_KEY, userDetail.id)
        getNavController().navigate(R.id.userDetailFragment, bundle)
    }

}