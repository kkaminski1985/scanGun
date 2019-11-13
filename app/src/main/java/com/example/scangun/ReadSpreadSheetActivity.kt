package com.example.scangun

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.scangun.R
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.identity.intents.AddressConstants.Extras.EXTRA_ADDRESS
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
//import com.google.api.services.script.ScriptScopes
import com.google.api.services.sheets.v4.SheetsScopes
import java.util.*

class ReadSpreadsheetActivity : AppCompatActivity(), ReadSpreadsheetContract.View {

    // private lateinit var tvUsername : TextView
    //private lateinit var rvSpreadsheet : RecyclerView

    private lateinit var presenter : ReadSpreadsheetContract.Presenter
    private var trkIdx = 2

    // private lateinit var sheetData:Array<PO>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //bindingViews()
        initDependencies()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_GOOGLE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {

                presenter.loginSuccessful()
                // val intent = Intent(this, ScanActivity::class.java)
                // startActivity(intent)
            } else {
                presenter.loginFailed()
            }
        }
    }

    private fun initDependencies() {
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
        presenter = ReadSpreadsheetPresenter(this, authManager, sheetsRepository)
        presenter.init()

    }

    /*private fun bindingViews() {
        tvUsername = findViewById(R.id.tv_username)
        rvSpreadsheet = findViewById(R.id.rv_spreadsheet)
    }*/

    // View related implementations
    /*override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }*/

    /*override fun showPeople(people: List<Person>) {
        val adapter = SpreadsheetAdapter(people)
        rvSpreadsheet.layoutManager = LinearLayoutManager(this)
        rvSpreadsheet.adapter = adapter
    }*/

    override fun launchAuthentication(client: GoogleSignInClient) {
        startActivityForResult(client.signInIntent, RQ_GOOGLE_SIGN_IN)

    }
     override fun checkTrack(ckTrk: MutableList<trkNum>)  {
        val trkData = ckTrk.toTypedArray()
         presenter.dispose()
        val ck :trkNum = trkNum("")
        val idx:Int = trkData.indexOf(ck)
        trkIdx = idx
    }
    override fun loadList(po: MutableList<PO>) {
        val sheetData = po.toTypedArray()

        presenter.dispose()
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra("POs",sheetData)
        //intent.putExtra("Idx",trkIdx)
        startActivity(intent)
    }
    companion object {
        const val TAG = "ReadSpreadsheetActivity"
        const val RQ_GOOGLE_SIGN_IN = 1
        const val GOOGLE_AUTH = 2
    }

}
