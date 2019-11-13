package com.example.scangun

import android.os.AsyncTask
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.scangun.ReadSpreadsheetActivity.Companion.GOOGLE_AUTH
import com.example.scangun.ReadSpreadsheetPresenter.Companion.ckRange
import com.example.scangun.ReadSpreadsheetPresenter.Companion.range

import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import io.reactivex.Observable
import io.reactivex.Scheduler
import org.jetbrains.anko.async
import java.util.concurrent.Future
import kotlin.concurrent.thread

class SheetsAPIDataSource(val credential: GoogleAccountCredential,
                          val transport : NetHttpTransport,
                          val jsonFactory: JsonFactory
) : SheetsDataSource {




     val sheetsAPI : Sheets
        get() {
            return Sheets.Builder(transport,jsonFactory,credential)
                .setApplicationName("scangun")
                .build()
        }



    override fun readSpreadSheet(spreadsheetId: String,
                                 spreadsheetRange: String): Observable<MutableList<MutableList<Any>>> {
      //  try{
        return Observable
            .fromCallable {

                val response = sheetsAPI.spreadsheets().values()
                    .get(spreadsheetId, spreadsheetRange)
                    .execute()

                response.getValues()
            }
   // } catch(e: UserRecoverableAuthException){
    //    startActivityForResult(e.intent, GOOGLE_AUTH)
    }

     fun writeSpreadsheet(pos: ValueRange, spreadsheetId: String, Idx: Int) {
         var idx = ScanActivity.trkIdx

             val range: String = "Receiving!A" + (idx/*Idx+2*/).toString() + ":A" + (pos.getValues().size/*+2 + Idx*/+idx).toString()
             val updateValues = sheetsAPI.spreadsheets().values()

                 .update(spreadsheetId, range, pos)
                 .setValueInputOption("RAW")
                 .execute()



    }
    /*override*/ fun getTrkSize(ssID:String, ckRange:String) : Observable<MutableList<MutableList<Any>>>{
        return Observable
            .fromCallable {
                val res: ValueRange = sheetsAPI
                    .spreadsheets().values()
                    .get(ssID, ckRange)
                    .execute()
                res.getValues()
            }
    }

}