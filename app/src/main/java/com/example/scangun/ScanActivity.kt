package com.example.scangun

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scangun.ScanActivity.Companion.REQUEST_ENABLE_BLUETOOTH
import com.example.scangun.ScanActivity.Companion.scanR
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
//import com.google.api.services.script.ScriptScopes
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_scan.*

import org.jetbrains.anko.toast
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ScanActivity : AppCompatActivity(), ReadSpreadsheetContract.View/*, LoadSpreadsheet*/ {

    //private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var adapter: RecyclerAdapter
    // lateinit var sheetLoader: LoadSpreadsheet.Load
    private lateinit var linearLayoutManager: LinearLayoutManager


    companion object {
        var trkIdx = 2
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        var m_isConnected: Boolean = false
        var m_address: String? = null
        private lateinit var comPresenter:  ReadSpreadsheetContract.Presenter
        var recPos: ArrayList<PO> = ArrayList()
        //lateinit var inbPO: MutableList<PO>
        const val REQUEST_ENABLE_BLUETOOTH = 1
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        val m_data_on: ByteArray = byteArrayOf(0x10.toByte(), 0x14.toByte(), 0x00.toByte(),0x00.toByte(),0x00.toByte())
        lateinit var scanR: String
        var scanPo: ArrayList<PO> = ArrayList()
        var outPo: ArrayList<PO> = ArrayList()
        val ssId:String = "1B3BOymCMmLdK3mciNhUy5PE_SLhkLRZ91bNLBEErCHQ"
        //lateinit var parc_pos: Array<Parcelable>
    }

    override fun launchAuthentication(client: GoogleSignInClient) {
        startActivityForResult(client.signInIntent, ReadSpreadsheetActivity.RQ_GOOGLE_SIGN_IN)

    }
    override fun loadList(po: MutableList<PO>) {/*
        val sheetData = po.toTypedArray()
        comPresenter.dispose()
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra("POs",sheetData)
        startActivity(intent)*/
    }
    override fun checkTrack(ckTrk: MutableList<trkNum>){
        var idx = 0
        val trkData = ckTrk.toTypedArray()
        comPresenter.dispose()
        val ck :trkNum = trkNum("")
        for(t in trkData){
            if(t.trkNum == ""){
             idx = trkData.indexOf(t)
            }
        }
        //val idx:Int = trkData.indexOf(ck)
        trkIdx = idx + 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        outPo = ArrayList()
        //idx = intent.getIntExtra("Idx",2)
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (m_bluetoothAdapter == null) {
            toast("Bluetooth Not Supported")
            return
        }
        if (!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }
        m_address = "00:11:22:33:44:55"
        ConnectToDevice(this).execute()
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        /* poRecyclerView.layoutManager=linearLayoutManager
        adapter = RecyclerAdapter(scanPo)
        poRecyclerView.adapter = adapter*/
        //sheetLoader.startLoad()
        val parc_pos = intent.getParcelableArrayExtra("POs")
        recPos = unParcel(parc_pos)

        scan.setOnClickListener {
            run {
                IntentIntegrator(this@ScanActivity).initiateScan()
            }
        }
        commit.setOnClickListener {
        commitInit()
            comPresenter.startCkLength()


            comToSheet(outPo).execute()

        adapter.clearView()

        finish()
            //outPo = ArrayList()
        startActivity(getIntent())
            //this.
           // outPo = ArrayList()
        }
    }


    override fun onDestroy() {
        super.onDestroy()


    }

    /*/*override*/ fun loadList(po: MutableList<PO>) {
        val sheetData = po

    }*/
    private fun sendCommand(input: ByteArray){
        if(m_bluetoothSocket != null){
            try{
                m_bluetoothSocket!!.outputStream.write(input)
                //m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException){
                e.printStackTrace()
            }
        }
    }
     fun sendMessage(input: String){
        if(m_bluetoothSocket != null){
            var initz: ByteArray = byteArrayOf(0x1b.toByte(),0x40.toByte())
            var pSize:ByteArray = byteArrayOf(0x1d.toByte(),0x21.toByte(),0x01.toByte())
            var msg: ByteArray = input.toByteArray()
            var sendPrint: ByteArray = byteArrayOf(0x1b.toByte(),0x4A.toByte(), 0x44.toByte())// byteArrayOf(0x1b.toByte(), 0x64.toByte())
            var rotate: ByteArray = byteArrayOf(0x1B.toByte(), 0x56.toByte(),0x01.toByte(),0x31.toByte())
            var unrotate: ByteArray = byteArrayOf(0x1b.toByte(),0x56.toByte(),0x00.toByte(),0x30.toByte())
            rotate.plus(msg.plus(unrotate))
            try{

                m_bluetoothSocket!!.outputStream.write(initz)
                m_bluetoothSocket!!.outputStream.write(pSize)

                //m_bluetoothSocket!!.outputStream.write(rotate)
                m_bluetoothSocket!!.outputStream.write(msg)
                //m_bluetoothSocket!!.outputStream.write(unrotate)
                m_bluetoothSocket!!.outputStream.write(sendPrint)


            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
    private fun disconnect(){
        if(m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch(e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }
    private class ConnectToDevice(c: Context) : AsyncTask<Void,Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if(m_bluetoothSocket == null || !m_isConnected){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch(e: IOException){
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Log.i("data","Couldn't connect")
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }
    }

        private fun commitInit(){
        val signInOptions: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
                //.requestScopes(Scope(ScriptScopes.SPREADSHEETS))
                .requestEmail()
                //.requestIdToken(getString(R.string.client_id))
                .build()
        val googleSignInClient = GoogleSignIn.getClient(this, signInOptions)
        val googleAccountCredential = GoogleAccountCredential
            .usingOAuth2(this, Arrays.asList(*AuthenticationManager.SCOPES))
            .setBackOff(ExponentialBackOff())



        val authManager =
            AuthenticationManager(
                lazyOf(this),
                googleSignInClient,
                googleAccountCredential)
        val sheetsApidatasource =
            SheetsAPIDataSource(authManager.setUpGoogleAccountCredential(),
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance())
        val sheetsRepository = SheetsRepository(sheetsApidatasource)
        comPresenter = ReadSpreadsheetPresenter(this,authManager,sheetsRepository)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //setContentView(R.layout.activity_scan)
        super.onActivityResult(requestCode, resultCode, data)
        poRecyclerView.layoutManager = linearLayoutManager
        adapter = RecyclerAdapter(scanPo)
        poRecyclerView.adapter = adapter
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if ((resultCode == Activity.RESULT_OK && !m_bluetoothAdapter!!.isEnabled) || resultCode != Activity.RESULT_OK) {
                toast("Bluetooth Error")
            }

        }

        var sresult: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (sresult != null) {

            if (sresult.contents != null) {
                scanR = sresult.contents.toString()
                //scanR= "1Z9Y80369047499302"  //test
                var foundPo: PO? = findScan(scanR)
                if (foundPo != null) {
                    outPo.add(foundPo)

                    adapter.notifyItemInserted(scanPo.size)
                    //var msg: String =
                    //sendMessage(scanR)
                    var sMsg = "CD#: " + foundPo.cdnum.toString() +"\n" +
                            "Name: " + foundPo.name.toString() + "\n" +
                            "Order #: " + foundPo.orid.toString() + "\n" +
                            "PO #: " + foundPo.ponum.toString()

                    sendMessage(sMsg)         //printer on/off
                    //    toast("Good Scan" + foundPo.toString()
                    /*val text : TextView = findViewById(R.id.textView)
                    text.text = foundPo.toString()*/
                } else {
                    toast("Invalid Scan.  Please scan a different code")

                    //super.onActivityResult(requestCode, resultCode, data)
                }
                /*} else {
                scanR = "0"
            }*/


           /* } else {     //do i need this?  might be bad
                super.onActivityResult(requestCode, resultCode, data)
            */}


        }
    }
        private fun unParcel(parc_pos: Array<Parcelable>?): ArrayList<PO> {
            val unparceled_Pos = (parc_pos!!.map { it as PO })
            var retPo: ArrayList<PO> = ArrayList()

            for (po in unparceled_Pos) {
                retPo.add(po)
            }
            return retPo
        }


        //private fun findPo(/*sPO:ArrayList<Parcelable>*/) : ArrayList<PO>{
        //    if(scanR!!.)
        private fun findScan(inbScan: String): PO? {

            var curPo: PO? = null
            for (po in recPos) {

                if (po.trknum == inbScan) {
                    runOnUiThread {
                        curPo = po
                        scanPo.add(po)

                    }
                    break
                } else {
                    curPo = null
                }
            }


            return curPo

        }

        private class comToSheet(outPo: ArrayList<PO> ):AsyncTask<Void, Void, String>(){



            override fun doInBackground(vararg p0: Void?):String {


                 var outList: MutableList<MutableList<Any?>> = mutableListOf(mutableListOf())
                outPo.forEach { outList.add(mutableListOf(it.trknum)) }


                var outVals: ValueRange = ValueRange().setValues(outList.subList(1, outPo.size+1))
                comPresenter.startWritingSpreadsheet(
                    outVals,
                    ReadSpreadsheetPresenter.spreadsheetId,
                    trkIdx //+2
                )

                return outList.size.toString()

            }


        }




//    }
    }
