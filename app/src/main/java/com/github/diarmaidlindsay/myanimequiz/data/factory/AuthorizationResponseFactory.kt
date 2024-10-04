package com.github.diarmaidlindsay.myanimequiz.data.factory

import android.content.Intent
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

class AuthorizationResponseFactory @Inject constructor() {
    fun fromIntent(intent: Intent): AuthorizationResponse? {
        return AuthorizationResponse.fromIntent(intent)
    }
}