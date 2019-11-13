package com.example.scangun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.scangun.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var btnReadSpreadsheet : TextView
    lateinit var btnCreateSpreadsheet : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sign_in_button.setOnClickListener {
            val readSpreadsheetIntent = Intent(this, ReadSpreadsheetActivity::class.java)
            startActivity(readSpreadsheetIntent)
        }

    }

}