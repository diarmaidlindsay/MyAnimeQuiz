package com.github.diarmaidlindsay.myanimequiz.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.diarmaidlindsay.myanimequiz.di.USER_PREFERENCES_DATA_STORE
import com.github.diarmaidlindsay.myanimequiz.extensions.getValue
import javax.inject.Inject
import javax.inject.Named

class UserPreferencesRepository @Inject constructor(@Named(USER_PREFERENCES_DATA_STORE) private val dataStore: DataStore<Preferences>) {

    val accessToken = dataStore.getValue(ACCESS_TOKEN_KEY)
    val refreshToken = dataStore.getValue(REFRESH_TOKEN_KEY)

    suspend fun saveTokens(value: String) {
        dataStore.edit {
            it[ACCESS_TOKEN_KEY] = value
        }
//        dataStore.edit {
//            if (value.accessToken != null) it[ACCESS_TOKEN_KEY] = value.accessToken
//            if (value.refreshToken != null) it[REFRESH_TOKEN_KEY] = value.refreshToken
//        }
    }

    suspend fun removeTokens() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN_KEY)
            it.remove(REFRESH_TOKEN_KEY)
        }
    }

    companion object {

        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

    }
}
