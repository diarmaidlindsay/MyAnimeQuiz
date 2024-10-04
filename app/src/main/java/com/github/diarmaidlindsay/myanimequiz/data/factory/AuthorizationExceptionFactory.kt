package com.github.diarmaidlindsay.myanimequiz.data.factory

import android.content.Intent
import net.openid.appauth.AuthorizationException
import javax.inject.Inject

class AuthorizationExceptionFactory @Inject constructor() {
    fun fromIntent(intent: Intent): AuthorizationException? {
        return AuthorizationException.fromIntent(intent)
    }
}