package com.pekyurek.emircan.voicemessaging.presentation.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.pekyurek.emircan.voicemessaging.R
import com.pekyurek.emircan.voicemessaging.databinding.ActivityMainBinding
import com.pekyurek.emircan.voicemessaging.domain.model.User
import com.pekyurek.emircan.voicemessaging.extensions.showToast
import com.pekyurek.emircan.voicemessaging.presentation.ui.base.BaseActivity
import com.pekyurek.emircan.voicemessaging.presentation.ui.register.RegisterActivity
import com.pekyurek.emircan.voicemessaging.presentation.ui.userlist.UserListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<MainViewModel>()

    private var awayUser: User? = null

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }

    override fun onInit(savedInstanceState: Bundle?) {
        getArguments()
        autoLogin()
        setClickListeners()
        setObservers()
    }

    private fun getArguments() {
        awayUser = intent.getParcelableExtra(ARG_AWAY_USER)
    }

    private fun autoLogin() {
        viewModel.autoLogin()
    }

    private fun setClickListeners() {
        binding.googleSignInButton.setOnClickListener { viewModel.signInIntent() }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        viewModel.signIn(
            try {
                completedTask.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                null
            }
        )
    }

    private fun setObservers() {
        viewModel.successLogin.observe(this) {
            finish()
            startActivity(UserListActivity.newIntent(this, awayUser))
        }
        viewModel.errorMessage.observe(this) {
            awayUser = null
            showToast(it)
        }
        viewModel.requiredRegister.observe(this) {
            awayUser = null
            startActivity(RegisterActivity.newIntent(this))
        }
        viewModel.signInIntent.observe(this) {
            resultLauncher.launch(it)
        }
    }

    override fun inflateLayout(layoutInflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    companion object {
        private const val ARG_AWAY_USER = "ARG_AWAY_USER"

        fun newIntent(context: Context, awayUser: User? = null) =
            Intent(context, MainActivity::class.java).apply {
                putExtra(ARG_AWAY_USER, awayUser)
            }
    }
}