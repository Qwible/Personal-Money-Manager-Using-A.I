package com.example.qwibBank

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.qwibBank.TrueLayer.truelayerLink
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File



class MainActivity : AppCompatActivity() {
    private lateinit var bankingViewModel:BankingViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getSupportActionBar()?.hide()


        setContentView(R.layout.activity_main)


        //Launch PIN-Authentication when the main activity is launched.
        val km =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (km.isKeyguardSecure) {
            val authIntent = km.createConfirmDeviceCredentialIntent("Authenticate", "Insert Passcode")
            startActivityForResult(authIntent, 5)
        }


        bankingViewModel = ViewModelProvider(this).get(BankingViewModel::class.java)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.Transactions, R.id.Accounts, R.id.Budget
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var dbpath: File = this.getDatabasePath("transaction_database")
        Log.d("Hey", dbpath.absolutePath)

        val data: Uri? = intent.data
        if (data != null) {
            navController.navigate(R.id.Accounts)
            val code = data.getQueryParameter("code")
            code?.let {
                truelayerLink(
                    code,
                    bankingViewModel,
                    null,
                    this
                )
            }
        }
    }




    // call back when password is correct
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 5) {
            if (resultCode != Activity.RESULT_OK) {
                finish()
            }
        }
    }
}









