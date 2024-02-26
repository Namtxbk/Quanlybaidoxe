package com.example.parkingqr.ui.components.signup

import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentSignupBinding
import com.example.parkingqr.domain.model.user.UserLogin
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.launch

class SignUpFragment : BaseFragment() {
    private lateinit var binding: FragmentSignupBinding
    private val signUpViewModel: SignUpViewModel by viewModels()
    private var role = ""

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        signUpViewModel.showError()
                    }
                    if (it.message.isNotEmpty()) {
                        showMessage(it.message)
                        signUpViewModel.showMessage()
                    }
                    if (it.user != null && it.isCreatedUser) {
                        showMessage("Đăng ký thành công")
                        getNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        binding.rdgRoleSignUp.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rdUserRoleSignUp) {
                role = "user"
            } else {
                role = "business"
            }
        }
        binding.btnSignUpSignUp.setOnClickListener{
            handleSignUp()
        }
        binding.tvBackToLoginSignup.setOnClickListener {
            getNavController().popBackStack()
        }
    }

    private fun handleSignUp() {
        val email = binding.edtEmailSignUp.text.toString()
        val password = binding.edtPasswordSignUp.text.toString()
        val name = binding.edtNameSignUp.text.toString()
        val phoneNumber = binding.edtPhoneSignUp.text.toString()

        if (email.isEmpty()) {
            binding.edtEmailSignUp.setError("Email không được rỗng")
            return
        }
        if (!validateEmail(email)) {
            binding.edtEmailSignUp.setError("Email không đúng định dạng")
            return
        }
        if (password.isEmpty()) {
            binding.edtPasswordSignUp.setError("Mật khẩu không được rỗng")
            return
        }
        if (name.isEmpty()) {
            binding.edtNameSignUp.setError("Tên không được rỗng")
            return
        }
        if (phoneNumber.isEmpty()) {
            binding.edtPhoneSignUp.setError("Số điện thoại không được rỗng")
            return
        }
        if (!validatePhoneNumber(phoneNumber)) {
            binding.edtPhoneSignUp.setError("Số điện thoại không dúng định dạng")
            return
        }
        if(role.isEmpty()){
            showMessage("Vui lòng chọn vai trò")
            return
        }

        val userLogin = UserLogin()
        userLogin.id = ""
        userLogin.email = email
        userLogin.name = name
        userLogin.phoneNumber = phoneNumber
        userLogin.userId = ""
        userLogin.role = role

        signUpViewModel.doSignUp(email, password, userLogin)

    }

    private fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return email.matches(emailRegex.toRegex())
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        return Patterns.PHONE.matcher(phoneNumber).matches()
    }
}