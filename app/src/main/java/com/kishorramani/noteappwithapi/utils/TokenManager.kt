package com.kishorramani.noteappwithapi.utils

import android.content.Context
import android.content.SharedPreferences
import com.kishorramani.noteappwithapi.utils.Constants.PREFS_TOKEN_FILE
import com.kishorramani.noteappwithapi.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//Qualifier in dagger
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
}