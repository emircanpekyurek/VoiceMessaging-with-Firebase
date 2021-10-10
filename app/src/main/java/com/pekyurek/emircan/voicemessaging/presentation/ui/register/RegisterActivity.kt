package com.pekyurek.emircan.voicemessaging.presentation.ui.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.pekyurek.emircan.voicemessaging.R
import com.pekyurek.emircan.voicemessaging.databinding.ActivityRegisterBinding
import com.pekyurek.emircan.voicemessaging.presentation.ui.base.BaseActivity
import com.pekyurek.emircan.voicemessaging.presentation.ui.userlist.UserListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onInit(savedInstanceState: Bundle?) {
        setToolbar()
        setClickListeners()
        setObservers()
    }

    private fun setToolbar() {
        supportActionBar?.setTitle(R.string.label_register)
    }

    private fun setClickListeners() {
        binding.btnRegister.setOnClickListener {
            viewModel.register(binding.etNickname.text?.toString())
        }
    }

    private fun setObservers() {
        viewModel.errorMessage.observe(this) {
            binding.etNickname.error = getString(it)
        }
        viewModel.loggedIn.observe(this) {
            finishAffinity()
            startActivity(UserListActivity.newIntent(this))
        }
    }

    override fun inflateLayout(layoutInflater: LayoutInflater) =
        ActivityRegisterBinding.inflate(layoutInflater)

    companion object {
        fun newIntent(context: Context) = Intent(context, RegisterActivity::class.java)
    }
}