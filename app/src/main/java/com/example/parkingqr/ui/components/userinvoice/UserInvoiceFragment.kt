package com.example.parkingqr.ui.components.userinvoice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentMyInvoiceBinding
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.*

class UserInvoiceFragment: BaseFragment() {

    companion object{
        const val INVOICE_ID = "INVOICE_ID"
    }

    private lateinit var binding: FragmentMyInvoiceBinding
    private lateinit var invoiceList: MutableList<ParkingInvoice>
    private val userInvoiceViewModel: UserInvoiceViewModel by hiltNavGraphViewModels(R.id.myinvoiceFragment)
    private lateinit var invoiceListAdapter: UserInvoiceListAdapter
    private var searchJob: Job? = null

    override fun observeViewModel() {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                userInvoiceViewModel.stateUi.collect{
                    if(it.isLoading) showLoading() else hideLoading()
                    if(it.error.isNotEmpty()) {
                        showError(it.error)
                        userInvoiceViewModel.showError()
                    }
                    if(invoiceList.isEmpty()) invoiceList.addAll(it.invoiceList)
                    else{
                        invoiceList.clear()
                        invoiceList.addAll(it.invoiceList)
                    }
                    invoiceListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentMyInvoiceBinding.inflate(layoutInflater)
        invoiceList = mutableListOf()
        invoiceListAdapter = UserInvoiceListAdapter(invoiceList)
        invoiceListAdapter.setEventClick {
            handleClickItem(it)
        }
        binding.rlvListMyInvoice.apply {
            adapter = invoiceListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        binding.edtSearchMyInvoice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(800)
                    userInvoiceViewModel.searchParkingInvoice(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}

        })
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        showBottomNavigation()
    }

    private fun handleClickItem(parkingInvoice: ParkingInvoice){
        val bundle = Bundle()
        bundle.putString(INVOICE_ID, parkingInvoice.id)
        getNavController().navigate(R.id.myInvoiceDetailFragment, bundle)
        hideBottomNavigation()
    }
}