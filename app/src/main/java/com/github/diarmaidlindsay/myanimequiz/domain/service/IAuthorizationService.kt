package com.github.diarmaidlindsay.myanimequiz.domain.service

import android.content.Intent
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

interface IAuthorizationService {
    fun performTokenRequest(
        request: TokenRequest,
        callback: AuthorizationService.TokenResponseCallback
    )

    fun getAuthorizationRequestIntent(request: AuthorizationRequest): Intent
}