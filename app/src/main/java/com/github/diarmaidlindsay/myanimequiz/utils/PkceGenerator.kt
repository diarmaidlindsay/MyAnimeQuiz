package com.github.diarmaidlindsay.myanimequiz.utils

import java.security.SecureRandom

object PkceGenerator {
    private val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + '-' + '.' + '_' + '~'
    private val secureRandom = SecureRandom()

    fun generateVerifier(length: Int = 128): String {
        require(length in 43..128) { "PKCE verifier length must be between 43 and 128 characters" }
        return (1..length)
            .map { allowedChars[secureRandom.nextInt(allowedChars.size)] }
            .joinToString("")
    }
}