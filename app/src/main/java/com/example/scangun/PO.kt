package com.example.scangun

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PO(val cdnum : String, val orid : String, val ponum : String, val quant : String, val trknum : String, val name: String) : Parcelable{
     fun getCdNum() :String{return cdnum}
     fun getOrder() :String{return orid}
     fun getPo_num() : String{return ponum}
     fun getQty() : String{return quant}
     fun getTrkNum() : String{return trknum}
     fun get_Name() : String{return name}
}