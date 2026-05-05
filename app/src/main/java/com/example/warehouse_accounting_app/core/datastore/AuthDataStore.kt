package com.example.warehouse_accounting_app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.warehouse_accounting_app.core.network.AuthTokenProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** Имя хранилища «auth» → файл `datastore/auth.preferences_pb` (относительно app files); исключён из auto-backup в res/xml. */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthDataStore(
    context: Context,
) : AuthTokenProvider {
    private val ds = context.dataStore

    private object Keys {
        val token = stringPreferencesKey("jwt_token")
    }

    fun observeToken(): Flow<String?> = ds.data.map { it[Keys.token] }

    suspend fun getTokenOnce(): String? = observeToken().first()

    suspend fun saveToken(token: String) {
        ds.edit { it[Keys.token] = token }
    }

    suspend fun clearToken() {
        ds.edit { it.remove(Keys.token) }
    }

    override suspend fun getToken(): String? = getTokenOnce()
}
