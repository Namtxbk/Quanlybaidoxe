package com.example.parkingqr.ui.components.usermanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.user.UserDetail

class UserManagementAdapter(private val userList: MutableList<UserDetail>): Adapter<UserManagementAdapter.UserManagementViewHolder>() {

    private var onClickItem: ((UserDetail)-> Unit)? = null
    private var onClickMore: ((UserDetail)-> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserManagementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_management, parent, false)
        return UserManagementViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserManagementViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    fun setEventClick(onClickItem: ((UserDetail)->Unit)){
        this.onClickItem = onClickItem
    }
    fun setOnClickMore(onClickMore: (UserDetail) -> Unit){
        this.onClickMore = onClickMore
    }

    inner class UserManagementViewHolder(itemView: View): ViewHolder(itemView){
        private val status: TextView = itemView.findViewById(R.id.tvStatusUserManagement)
        private val email: TextView = itemView.findViewById(R.id.tvEmailUserManagement)
        private val phone: TextView = itemView.findViewById(R.id.tvPhoneNumberUserManagement)
        private val role: TextView = itemView.findViewById(R.id.tvRoleUserManagement)
        private val more: ImageView = itemView.findViewById(R.id.ivMoreUserManagement)
        private var curUser: UserDetail? = null

        init {
            itemView.setOnClickListener{
                onClickItem?.invoke(curUser!!)
            }
            more.setOnClickListener {
                onClickMore?.invoke(curUser!!)
            }
        }
        fun bind(userDetail: UserDetail){
            curUser = userDetail

            email.text = userDetail.email
            phone.text = userDetail.phoneNumber
            if(userDetail.role == "user"){
                role.text = "Người dùng"
            }
            else if(userDetail.role == "business"){
                role.text = "Quản lý xe"
            }
            else{
                role.text = "Quản trị viên"
            }
            if(userDetail.status == "active"){
                status.text = "Đang hoạt động"
                status.setTextColor(itemView.resources.getColor(R.color.light_green))
            }
            else{
                status.text = "Bị chặn"
                status.setTextColor(itemView.resources.getColor(R.color.light_red))
            }
        }

    }
}