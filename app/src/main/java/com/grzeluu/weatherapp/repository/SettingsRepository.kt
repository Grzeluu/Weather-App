package com.grzeluu.weatherapp.repository

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val PREFERENCE_NAME = "settings"

class SettingsRepository(val application: Application) {

    private object PreferenceKeys {
        val units = stringPreferencesKey("units")
    }

    private val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

    suspend fun saveUnitsSettings(units: String) {
        application.dataStore.edit { settings ->
            settings[PreferenceKeys.units] = units
        }
    }

    val readUnitsSettings: Flow<String> = application.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val myName = preference[PreferenceKeys.units] ?: "none"
            myName
        }
}