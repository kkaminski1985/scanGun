package com.example.scangun

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.async

class ReadSpreadsheetPresenter(private val view: ReadSpreadsheetContract.View,

                               private val authenticationManager: AuthenticationManager,
                               private val sheetsRepository: SheetsRepository) :
    ReadSpreadsheetContract.Presenter {

    lateinit var readSpreadsheetDisposable : Disposable
     //var idx: Int = 2
    var idx = ScanActivity.trkIdx
    override fun startAuthentication() {
        view.launchAuthentication(authenticationManager.googleSignInClient)
    }

    override fun init() {
        startAuthentication()
    }



    override fun dispose() {
        readSpreadsheetDisposable.dispose()
    }

    override fun loginSuccessful() {
        //try {
        //view.showName(authenticationManager.getLastSignedAccount()?.displayName!!)
        authenticationManager.setUpGoogleAccountCredential()

        startReadingSpreadsheet(spreadsheetId, range)
        //} catch(e:UserRecoverableAuthIOException){
        //    startActivityForResult(e.intent)
        //}
    }

    override fun startCkLength(){
        ckLength(spreadsheetId, ckRange)

    }
    override fun loginFailed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
     /*override fun commitReceiving(pos:ValueRange){
         startWritingSpreadsheet(pos, spreadsheetId)
     }*/

    private fun ckLength(spreadsheetId:String, ckRange: String) {

    readSpreadsheetDisposable =

            sheetsRepository.ckSpreadsheet(spreadsheetId, ckRange)
                .flatMapIterable { it }
                .map{
                    trkNum(it[0].toString())
                }
                .toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(return)
                .subscribe(Consumer {
                    view.checkTrack(it)
                })


    }
    private fun startReadingSpreadsheet(spreadsheetId : String, range : String){
        //lateinit var sheetData: MutableList<MutableList<PO>>
        readSpreadsheetDisposable=

            sheetsRepository.readSpreadSheet(spreadsheetId, range)
                .flatMapIterable { it -> it }
                .map {
                    PO(
                        it[0].toString(),
                        it[1].toString(),
                        it[2].toString(),
                        it[3].toString(),
                        it[4].toString(),
                        it[5].toString()
                    )
                }
                .toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { error ->
                    val po =
                        mutableListOf<PO>(PO("Error", error.toString(), "NA", "NA", "NA", "NA"))
//                        view.loadList(po)
                }
                .subscribe(Consumer {
                view.loadList(it)
                })
        //} catch(e: UserRecoverableAuthException){

        //startActivityForResult(e.intent, AUTH_REQ)


    }
    override fun startWritingSpreadsheet(pos: ValueRange,spreadsheetId: String, idx: Int){
          sheetsRepository.writeSpreadsheet(pos,spreadsheetId, idx)
    }
    companion object {
        val spreadsheetId = "1B3BOymCMmLdK3mciNhUy5PE_SLhkLRZ91bNLBEErCHQ"
        val range = "stageTracking!A1:F"
        val ckRange = "Receiving!A1:A"
        const val AUTH_REQ = 2
    }

}
