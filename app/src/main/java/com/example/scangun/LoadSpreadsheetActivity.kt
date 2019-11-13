/*package com.example.scangun

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
class loadSpreadsheet(private val load : LoadSpreadsheet.Load,
                      private val sheetsRepository: SheetsRepository){
private fun startReadingSpreadsheet(spreadsheetId : String, range : String){
    //lateinit var sheetData: MutableList<MutableList<PO>>

    lateinit var readSpreadsheetDisposable: Disposable
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
                load.loadList(it)
            })
    //} catch(e: UserRecoverableAuthException){

    //startActivityForResult(e.intent, AUTH_REQ)


}
     fun startLoad(){
        startReadingSpreadsheet(spreadsheetId, range)
    }

companion object {
    val spreadsheetId = "1B3BOymCMmLdK3mciNhUy5PE_SLhkLRZ91bNLBEErCHQ"
    val range = "stageTracking!A1:F"
    const val AUTH_REQ = 2
}
}
*/