package com.pekyurek.emircan.voicemessaging.presentation.ui.userlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pekyurek.emircan.voicemessaging.databinding.ItemUserBinding
import com.pekyurek.emircan.voicemessaging.domain.model.User
import com.pekyurek.emircan.voicemessaging.extensions.toSearchFilter

@SuppressLint("NotifyDataSetChanged")
class UserAdapter(private val userId:String, private val onItemClicked: (user: User) -> Unit) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val list = mutableListOf<User>()
    private val filterList = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filterList[position])
    }

    override fun getItemCount(): Int = filterList.size

    fun setData(userList: List<User>) {
        list.clear()
        list.addAll(userList)
    }

    fun filter(text: String?, onWarningMessage: (searchType : SearchType) -> Unit) {
        val searchType =
            SearchType.values().find { it.searchKey == text } ?: SearchType.FILTER_USERS
        when (searchType) {
            SearchType.ALL_USERS -> showAllUser()
            SearchType.CHAT_USERS -> {
                getPreviousChattedUserList()
            }
            SearchType.FILTER_USERS -> filterUser(text.toString())
        }
        onWarningMessage.invoke(searchType)
    }

    private fun getPreviousChattedUserList() {
        filterList.clear()
        filterList.addAll(list.filter { user -> user.chatUserIds.contains(userId) })
        notifyDataSetChanged()
    }

    private fun filterUser(text: String) {
        filterList.clear()
        filterList.addAll(list.filter {
            it.email.toSearchFilter().contains(text.toSearchFilter())
                    || it.nickname.toSearchFilter().contains(text.toSearchFilter())
        })
        notifyDataSetChanged()
    }

    private fun showAllUser() {
        filterList.clear()
        filterList.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(user: User) {
        list.add(user)
    }

    fun updateUser(user: User) {
        val index = list.indexOfFirst { it.id == user.id }
        if (index >= 0) list[index] = user
    }

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvEmail.text = user.email
            binding.tvNickname.text = user.nickname
            itemView.setOnClickListener { onItemClicked.invoke(user) }
        }
    }
}