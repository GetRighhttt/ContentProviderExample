package com.example.contentproviderbasics

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import com.example.contentproviderbasics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    /*
    Only need a single instance of each of these variables so for type safety and memory
    allocation we can put these in a companion object, which essentially means they are singletons
    for the main activity.
     */
    companion object {
        private val manifestPermissions =
            arrayOf<String>(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
        const val permissionRequestCode: Int = 111
        const val displayName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        const val phoneNumber = ContactsContract.CommonDataKinds.Phone.NUMBER
        private const val id = ContactsContract.CommonDataKinds.Phone._ID
        private val uriContacts = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        /*
        In order to get contact information we must access inbuilt class that provides inbuilt data for
        contacts. We declare a variable here so that we can use this variable in other instances
        as well for the contacts. The class name is Contacts Contract.

        Generally, we just want the name and number, however for this example we are going to look at
        the ID as well.
         */
        private val contactColumns = listOf<String>(
            displayName,
            phoneNumber,
            id
        ).toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        // method that checks permissions when activity is first created
        checkPermissions()
    }

    /*
   First check if permission is granted by the user. Make sure to import Manifest(Android) and
   not Java Util.)
    */
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                // make each permission a string with anonymous parameter
                manifestPermissions.forEach { _ -> }.toString()
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
        // if request code is valid && permissions are granted, read contacts
        if (requestCode == permissionRequestCode &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            readContacts()
        }
    }

    /*
    Method to read contacts if permission is granted.
    Content resolvers provides access to the content model.
    rs = Result set.
    Cursor adapter is how we will bind the contacts to our listView.
     */
    @SuppressLint("Recycle")
    private fun readContacts() {

        val from = listOf<String>(
            displayName,
            phoneNumber,
        ).toTypedArray()

        val to = intArrayOf(android.R.id.text1, android.R.id.text2)

        var rs = contentResolver.query(
            uriContacts, // URI - maps to the table of the information provided by content provider
            contactColumns, // projection - array of columns included for each row
            null, // selection - specifies criteria for selecting rows
            null, // selection arguments - arguments for row selection
            displayName // sort order - how the information is sorted
        )

        /*
        Cursor adapter exposes data from a cursor to a listView. This adapter would be different
        depending on how you plan on updating the screen with the content provider. We're using a
        listView here so it's necessary to use this.
         */
        val contactAdapter =
            SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                rs,
                from,
                to,
                0
            )
        mainBinding.listView.adapter = contactAdapter

        /*
        Now we must override this method in order to filter our results below.
         */
        mainBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            /*
            This method is how we will filter the results in the searchView. We pass in our result
            set and add a selection and sort condition to it based on how we want to filter.
            newText is the string that we are filtering, and we are filtering by the name.
             */
            override fun onQueryTextChange(newText: String?): Boolean {
                rs = contentResolver.query(
                    uriContacts, // URI
                    contactColumns, // projection
                    "$displayName LIKE ?", // selection
                    Array(1) { "$newText%" }, // selection arguments
                    displayName // sort order
                )
                contactAdapter.changeCursor(rs)
                return false
            }
        })
    }
}