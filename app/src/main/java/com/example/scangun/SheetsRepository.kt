package com.example.scangun

import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.model.ValueRange
import io.reactivex.Observable

class SheetsRepository(private val sheetsAPIDataSource: SheetsAPIDataSource) {

    //val REQUEST_AUTHORIZATION: Int = 1
    fun readSpreadSheet(spreadsheetId : String,
                        spreadsheetRange : String): Observable<MutableList<MutableList<Any>>> {
    //    try {
            return sheetsAPIDataSource.readSpreadSheet(spreadsheetId, spreadsheetRange)
      //  } catch (e: UserRecoverableAuthIOException) {
        //    startActivityForResult(e., REQUEST_AUTHORIZATION);
        //}
        }
     fun writeSpreadsheet(pos:ValueRange,ssId:String,idx:Int) {
        return sheetsAPIDataSource.writeSpreadsheet(pos,ssId,idx)
    }

    fun ckSpreadsheet(ssID:String,ckRange:String): Observable<MutableList<MutableList<Any>>>{
        return sheetsAPIDataSource.getTrkSize(ssID, ckRange)
    }



}