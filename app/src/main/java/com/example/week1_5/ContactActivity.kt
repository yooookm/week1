package com.example.week1_5

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.OperationApplicationException
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1_5.databinding.ContactViewBinding


class ContactActivity :AppCompatActivity() {
    private val REQUEST_CONTACT_PERMISSION = 100
    lateinit var binding: ContactViewBinding
    lateinit var requestLauncher: ActivityResultLauncher<Intent>
    lateinit var contactRV: RecyclerView
    lateinit var itemlist: ArrayList<contactInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContactViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactRV = findViewById<RecyclerView>(R.id.button2)
        itemlist = ArrayList<contactInfo>()

        // Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACT_PERMISSION)
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CONTACTS), REQUEST_CONTACT_PERMISSION)
        }
        else {
            loadContacts()
        }
    }

    // Handle result of permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CONTACT_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("test", "permission granted")
                loadContacts()
            } else {
                Log.d("test", "permission denied")
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // Load contacts
    @SuppressLint("Range")
    fun loadContacts() {
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER),
            null,
            null,
            null)

        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    itemlist.add(contactInfo(name, phone))
                } while (it.moveToNext())
            }
            it.close()

            val contactAdapter1 = contactAdapter(itemlist)
            contactAdapter1.notifyDataSetChanged()

            contactRV.adapter = contactAdapter1
            contactRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
    }

}
