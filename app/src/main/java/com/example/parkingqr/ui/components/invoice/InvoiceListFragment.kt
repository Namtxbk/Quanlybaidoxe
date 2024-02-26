package com.example.parkingqr.ui.components.invoice

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
import com.example.parkingqr.databinding.FragmentInvoiceListBinding
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.*

class InvoiceListFragment: BaseFragment() {

    companion object{
        const val INVOICE_ID_KEY = "INVOICE_ID_KEY"
    }

    private lateinit var binding: FragmentInvoiceListBinding
    private lateinit var invoiceList: MutableList<ParkingInvoice>
    private val invoiceViewModel: InvoiceListViewModel by hiltNavGraphViewModels(R.id.invoiceListFragment)
    private lateinit var invoiceListAdapter: InvoiceListAdapter
    private var searchJob: Job? = null

    override fun observeViewModel() {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                invoiceViewModel.stateUi.collect{

                    if(it.isLoading) showLoading() else hideLoading()
                    if(it.error.isNotEmpty()) {
                        showError(it.error)
                        invoiceViewModel.showError()
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
        binding = FragmentInvoiceListBinding.inflate(layoutInflater)
        invoiceList = mutableListOf()
        invoiceListAdapter = InvoiceListAdapter(invoiceList)
        invoiceListAdapter.setEventClick {
            handleClickItem(it)
        }
        binding.rlvListInvoiceList.apply {
            adapter = invoiceListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    private fun handleClickItem(parkingInvoice: ParkingInvoice){
        val bundle = Bundle()
        bundle.putString(INVOICE_ID_KEY, parkingInvoice.id)
        getNavController().navigate(R.id.invoiceDetailFragment, bundle)
    }
    override fun initListener() {
        hideActionBar()
        binding.edtSearchInvoiceList.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(800)
                    invoiceViewModel.searchParkingInvoice(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}