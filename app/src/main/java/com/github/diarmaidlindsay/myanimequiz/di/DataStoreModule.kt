package com.github.diarmaidlindsay.myanimequiz.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

const val USER_PREFERENCES_DATA_STORE = "user_preferences"
const val NOTIFICATIONS_DATA_STORE = "notifications"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    @Named(USER_PREFERENCES_DATA_STORE)
    fun provideDefaultDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return provideDataStore(context, USER_PREFERENCES_DATA_STORE)
    }

    @Provides
    @Singleton
    @Named(NOTIFICATIONS_DATA_STORE)
    fun provideNotificationsDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return provideDataStore(context, NOTIFICATIONS_DATA_STORE)
    }

    private fun provideDataStore(context: Context, name: String) =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(name)
        }
}