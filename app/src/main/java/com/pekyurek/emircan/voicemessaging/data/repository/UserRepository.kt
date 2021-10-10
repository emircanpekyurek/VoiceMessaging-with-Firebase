package com.pekyurek.emircan.voicemessaging.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pekyurek.emircan.voicemessaging.domain.model.User

class UserRepository {
    var account: GoogleSignInAccount? = null

    var user: User? = null
}