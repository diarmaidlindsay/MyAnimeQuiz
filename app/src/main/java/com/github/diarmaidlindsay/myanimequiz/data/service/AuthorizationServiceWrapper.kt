package com.github.diarmaidlindsay.myanimequiz.data.service

import android.content.Intent
import com.github.diarmaidlindsay.myanimequiz.domain.service.IAuthorizationService
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

class AuthorizationServiceWrapper(
    private val authorizationService: AuthorizationService
) : IAuthorizationService {
    override fun performTokenRequest(
        request: TokenRequest,
        callback: AuthorizationService.TokenResponseCallback
    ) {
        authorizationService.performTokenRequest(request, callback)
    }

    override fun getAuthorizationRequestIntent(request: AuthorizationRequest): Intent {
        return authorizationService.getAuthorizationRequestIntent(request)
    }
}