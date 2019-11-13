package com.example.scangun

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import java.util.*

interface ReadSpreadsheetContract {

    interface View : BaseView{
        //       fun showPeople(people : List<Person>)
//        fun showName(username : String)
        fun launchAuthentication(client : GoogleSignInClient)
        //
        fun loadList(po: MutableList<PO>)
        fun checkTrack(ckTrk: MutableList<trkNum>)
    }

    interface Presenter : BasePresenter {
        fun startAuthentication()
        fun loginSuccessful()
        fun loginFailed()
        fun startWritingSpreadsheet(pos:ValueRange,ssId:String,idx:Int)
        fun startCkLength()
    }



}