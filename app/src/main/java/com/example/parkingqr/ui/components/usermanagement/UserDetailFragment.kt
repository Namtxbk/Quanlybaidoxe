package com.example.parkingqr.ui.components.usermanagement

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentUserDetailBinding
import com.example.parkingqr.domain.model.user.UserDetail
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentUserDetailBinding
    private val userDetailViewModel: UserDetailViewModel by viewModels()
    private var id: String? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments?.getString(UserManagementFragment.USER_DETAIL_KEY)
        id?.let {
            userDetailViewModel.getUserById(it)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userDetailViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        userDetailViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                    }
                    if (it.userDetail == null) binding.llContainerUserDetail.visibility = View.GONE
                    it.userDetail?.let { userDetail ->
                        showUserDetail(userDetail)
                    }
                    if (it.isSaved) {
                        getNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentUserDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.user_management_fragment_name))
        binding.btnSaveUserDetail.setOnClickListener {
            handleSaveUser()
        }
        binding.edtDateOfBirthUserDetail.setOnClickListener {
            showDatePickerDialog(it as EditText)
        }
    }

    private fun handleSaveUser() {
        val name = binding.edtNameUserDetail.text.toString()
        val userName = binding.edtUserNameUserDetail.text.toString()
        val address = binding.edtAddressUserDetail.text.toString()
        val email = binding.edtEmailUserDetail.text.toString()
        val identifierCode = binding.edtIdentifierCodeUserDetail.text.toString()
        val dateOfBirth = binding.edtDateOfBirthUserDetail.text.toString()
        val phone = binding.edtPhoneUserDetail.text.toString()

        userDetailViewModel.updateNewUserDetail(
            _name = name,
            _userName = userName,
            _address = address,
            _email = email,
            _identifierCode = identifierCode,
            _dateOfBirth = dateOfBirth,
            _phone = phone
        )
        userDetailViewModel.updateUser()
    }

    private fun showUserDetail(userDetail: UserDetail) {
        binding.llContainerUserDetail.visibility = View.VISIBLE
        binding.edtDateOfBirthUserDetail.inputType = InputType.TYPE_NULL
        binding.edtNameUserDetail.setText(userDetail.name)
        binding.edtUserNameUserDetail.setText(userDetail.username)
        binding.edtAddressUserDetail.setText(userDetail.address)
        binding.edtEmailUserDetail.setText(userDetail.email)
        binding.edtIdentifierCodeUserDetail.setText(userDetail.personalCode)
        binding.edtDateOfBirthUserDetail.setText(userDetail.birthday)
        binding.edtPhoneUserDetail.setText(userDetail.phoneNumber)
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

}