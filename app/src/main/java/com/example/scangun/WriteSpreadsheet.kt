package com.example.scangun

import androidx.appcompat.app.AppCompatActivity
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange

class WriteSpreadsheet(val sheetsAPIDataSource: SheetsAPIDataSource, idx:Int){
     private fun startWritingSpreadsheet(pos: ValueRange, ssId:String, idx: Int){
        sheetsAPIDataSource.writeSpreadsheet(pos,ssId, idx)
     }

    }
