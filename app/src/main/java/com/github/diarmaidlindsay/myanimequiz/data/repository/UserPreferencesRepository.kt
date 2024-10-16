package com.github.diarmaidlindsay.myanimequiz.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.diarmaidlindsay.myanimequiz.data.model.AccessToken
import com.github.diarmaidlindsay.myanimequiz.di.USER_PREFERENCES_DATA_STORE
import com.github.diarmaidlindsay.myanimequiz.ui.base.ThemeStyle
import com.github.diarmaidlindsay.myanimequiz.utils.extensions.getValue
import com.github.diarmaidlindsay.myanimequiz.utils.extensions.setValue
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class UserPreferencesRepository @Inject constructor(@Named(USER_PREFERENCES_DATA_STORE) private val dataStore: DataStore<Preferences>) {

    val accessToken = dataStore.getValue(ACCESS_TOKEN_KEY)
    val refreshToken = dataStore.getValue(REFRESH_TOKEN_KEY)
    val tokenExpiryDate = dataStore.getValue(TOKEN_EXPIRY_DATE_KEY)

    val theme = dataStore.getValue(THEME_KEY, ThemeStyle.FOLLOW_SYSTEM.name)
        .map { ThemeStyle.valueOfOrNull(it) ?: ThemeStyle.FOLLOW_SYSTEM }

    suspend fun setTheme(value: ThemeStyle) {
        dataStore.setValue(THEME_KEY, value.name)
    }

    suspend fun saveTokens(value: AccessToken) {
        dataStore.edit {
            if (value.accessToken != null) it[ACCESS_TOKEN_KEY] = value.accessToken
            if (value.refreshToken != null) it[REFRESH_TOKEN_KEY] = value.refreshToken
            it[TOKEN_EXPIRY_DATE_KEY] = value.expiryDate.time
        }
    }

    suspend fun removeTokens() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN_KEY)
            it.remove(REFRESH_TOKEN_KEY)
            it.remove(TOKEN_EXPIRY_DATE_KEY)
        }
    }

    companion object {

        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val TOKEN_EXPIRY_DATE_KEY = longPreferencesKey("token_expiry_date")
        private val THEME_KEY = stringPreferencesKey("theme")
    }
}
