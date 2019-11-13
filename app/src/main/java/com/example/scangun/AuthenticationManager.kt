package com.example.scangun

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
//import com.google.api.services.script.ScriptScopes
import com.google.api.services.sheets.v4.SheetsScopes

class AuthenticationManager(private val context: Lazy<Context>,
                            val googleSignInClient : GoogleSignInClient,
                            val googleAccountCredential : GoogleAccountCredential?) {

    fun getLastSignedAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context.value)
    }

    fun setUpGoogleAccountCredential(): GoogleAccountCredential {
        googleAccountCredential?.selectedAccount = getLastSignedAccount()?.account
        return googleAccountCredential!!  //fudgy
    }

    companion object {
        val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS)
    }
}