package com.example.warehouse_accounting_app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(context: Context) {
    private val ds = context.userDataStore

    private object Keys {
        val lastEmail = stringPreferencesKey("last_email")
    }

    val lastEmailFlow: Flow<String?> = ds.data.map { it[Keys.lastEmail] }

    suspend fun setLastEmail(email: String) {
        ds.edit { it[Keys.lastEmail] = email }
    }
}
