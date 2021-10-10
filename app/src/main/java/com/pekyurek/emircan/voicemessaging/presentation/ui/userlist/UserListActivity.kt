package com.pekyurek.emircan.voicemessaging.presentation.ui.userlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.pekyurek.emircan.voicemessaging.R
import com.pekyurek.emircan.voicemessaging.databinding.ActivityUserListBinding
import com.pekyurek.emircan.voicemessaging.presentation.ui.message.MessageActivity
import com.pekyurek.emircan.voicemessaging.domain.model.User
import com.pekyurek.emircan.voicemessaging.presentation.ui.base.BaseActivity
import com.pekyurek.emircan.voicemessaging.presentation.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListActivity : BaseActivity<ActivityUserListBinding>() {

    private val viewModel by viewModels<UserListViewModel>()

    private lateinit var userAdapter: UserAdapter

    override fun onInit(savedInstanceState: Bundle?) {
        getArguments()
        setClickListeners()
        setObservers()
    }

    private fun getArguments() {
        intent.getParcelableExtra<User>(ARG_AWAY_USER)?.let {
            startActivity(MessageActivity.newIntent(this, it))
        }
    }

    private fun initViews() {
        binding.rvUser.adapter = userAdapter

        binding.etSearch.addTextChangedListener {
            filterAdapter(it.toString())
        }
    }

    private fun filterAdapter(text: String?) {
        userAdapter.filter(text) {
            changeUi(it)
        }
    }

    private fun setClickListeners() {
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
        }

        binding.btnShowAllUsers.setOnClickListener {
            showAllUsers()
        }
    }

    private fun setObservers() {
        viewModel.getUserId.observe(this) { id ->
            userAdapter =
                UserAdapter(id) { user -> startActivity(MessageActivity.newIntent(this, user)) }
            initViews()
        }
        viewModel.userList.observe(this) {
            userAdapter.setData(it)
            filterAdapter(binding.etSearch.text.toString())
        }
        viewModel.addedNewMessage.observe(this) {
            userAdapter.addData(it)
        }
        viewModel.updatedUser.observe(this) {
            userAdapter.updateUser(it)
        }
        viewModel.signOut.observe(this) {
            finishAffinity()
            startActivity(MainActivity.newIntent(this))
        }
    }

    private fun showAllUsers() {
        filterAdapter(SearchType.ALL_USERS.searchKey)
    }

    private fun changeUi(searchType: SearchType) {
        val warningTextVisibility = userAdapter.itemCount == 0

        binding.tvWarning.isVisible = warningTextVisibility
        binding.btnShowAllUsers.isVisible = warningTextVisibility || searchType == SearchType.CHAT_USERS
        binding.rvUser.isVisible = warningTextVisibility.not()

        binding.tvWarning.setText(searchType.warningTextId)
        supportActionBar?.setTitle(searchType.titleResId)
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadUsers()
    }

    override fun inflateLayout(layoutInflater: LayoutInflater) =
        ActivityUserListBinding.inflate(layoutInflater)

    companion object {
        private const val ARG_AWAY_USER = "ARG_AWAY_USER"

        fun newIntent(context: Context, awayUser: User? = null) =
            Intent(context, UserListActivity::class.java).apply {
                putExtra(ARG_AWAY_USER, awayUser)
            }
    }
}