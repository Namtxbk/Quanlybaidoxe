package com.example.parkingqr.ui.base

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.parkingqr.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mView: View
    private lateinit var navController: NavController

    abstract fun observeViewModel()
    abstract fun initViewBinding(): View
    abstract fun initListener()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return initViewBinding()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        loadingDialog = context?.let { LoadingDialog(it) }!!
        activity?.findViewById<ImageView>(R.id.iv_actionbar_back_main)?.setOnClickListener{
            getNavController().popBackStack()
        }
        mView = view
    }

    fun showLoading() {
        loadingDialog.show()
    }

    fun hideLoading() {
        loadingDialog.dismiss()
    }

    fun showError(message: String) {
        val snackbar = Snackbar.make(
            mView, message,
            Snackbar.LENGTH_LONG
        )
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.WHITE)
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.RED)
        textView.textSize = 18f

        val view: View = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
        view.layoutParams = params

        snackbar.show()
    }
    fun showMessage(message: String){
        val snackbar = Snackbar.make(
            mView, message,
            Snackbar.LENGTH_LONG
        )
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.WHITE)
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.parseColor("#02075D"))
        textView.textSize = 18f

        val view: View = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
        view.layoutParams = params

        snackbar.show()
    }

    fun showActionBar(message: String){
        activity?.findViewById<TextView>(R.id.tv_actionbar_title_main)?.text = message
        activity?.findViewById<LinearLayout>(R.id.ll_actionbar_container_main)?.visibility = View.VISIBLE
    }
    fun hideActionBar(){
        activity?.findViewById<LinearLayout>(R.id.ll_actionbar_container_main)?.visibility = View.GONE
    }
    fun showBottomNavigation(){
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationMain)?.visibility = View.VISIBLE
    }
    fun hideBottomNavigation(){
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationMain)?.visibility = View.GONE
    }
    fun getNavController(): NavController{
        return navController
    }
}