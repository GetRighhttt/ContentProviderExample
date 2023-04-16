package com.example.contentproviderbasics

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.contentproviderbasics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    // variables necessary for permission request
    private val manifestPermissions =
        arrayOf<String>(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
    private val permissionRequestCode: Int = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        checkPermissions()
    }

    /*
   First check if permission is granted by the user. Make sure to import Manifest(Android) and
   not Java Util.)
    */
    private fun checkPermissions() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                manifestPermissions,
                permissionRequestCode
            )
        } else {
            readContacts()
        }
    }

    /*
    Method for permission results that checks if user has already granted permission and request is
    that it is passed, we will call readContacts() method.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // if permissions are granted and request code is valid, read contacts
        if (requestCode == permissionRequestCode &&
            grantResults.isNotEmpty() && equals(PackageManager.PERMISSION_GRANTED)
        ) {
            readContacts()
        }
    }

    /*
    Method to read contacts if permission is granted.
     */
    private fun readContacts() {
        TODO("Not yet implemented")
    }
}