package com.example.solarsystemapp.localdata

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.solarsystemapp.Tools
import kotlinx.coroutines.flow.first

object LocalDatastore {

    private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = Tools.DATA_STORE_NAME)

    suspend fun writeBoolean(context : Context, key : String, value : Boolean){
        context.dataStore.edit { database ->
            database[booleanPreferencesKey(key)] = value
        }
    }


    suspend fun readBoolean(context : Context, key : String, defaultValue : Boolean = false) : Boolean {
        val booleanKey = booleanPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[booleanKey] ?: defaultValue
    }
}