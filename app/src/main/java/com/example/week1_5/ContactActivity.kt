package com.example.week1_5

import android.Manifest

import android.annotation.SuppressLint
import android.app.AlertDialog

import android.content.ContentProviderOperation
import android.content.Intent
import android.content.OperationApplicationException
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1_5.databinding.ContactViewBinding

class ContactActivity : AppCompatActivity() {

    private val REQUEST_CONTACT_PERMISSION = 100
    lateinit var binding: ContactViewBinding
    lateinit var requestLauncher: ActivityResultLauncher<Intent>
    lateinit var contactRV: RecyclerView
    lateinit var itemlist: ArrayList<contactInfo>

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContactViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactRV = findViewById<RecyclerView>(R.id.button2)
        itemlist = ArrayList<contactInfo>()

        // Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACT_PERMISSION)

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CONTACTS), REQUEST_CONTACT_PERMISSION)
        } else {

            loadContacts()
        }

        // Set button click listener
        binding.button2.setOnClickListener {
            showAddContactDialog()
        }
    }

    // Load contacts
    @SuppressLint("Range")
    fun loadContacts() {
        // Add check for WRITE_CONTACTS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CONTACTS), REQUEST_CONTACT_PERMISSION)
        } else {
            // Clear existing contact list
            itemlist.clear()

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

                // Sort the contact list by name
                itemlist.sortBy { it.name }

                val contactAdapter1 = contactAdapter(itemlist)
                contactAdapter1.notifyDataSetChanged()

                contactRV.adapter = contactAdapter1
                contactRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    fun showAddContactDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Contact")

        val inputLayout = LinearLayout(this)
        inputLayout.orientation = LinearLayout.VERTICAL

        val nameInput = EditText(this)
        nameInput.hint = "Name"
        inputLayout.addView(nameInput)

        val phoneInput = EditText(this)
        phoneInput.hint = "Phone number"
        inputLayout.addView(phoneInput)

        builder.setView(inputLayout)

        builder.setPositiveButton("OK") { dialog, _ ->
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                addContact(name, phone)
                dialog.dismiss()
            } else {
                // Show error message
                Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    fun addContact(name: String, phone: String) {
        // Check if WRITE_CONTACTS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CONTACTS), REQUEST_CONTACT_PERMISSION)
            return
        }

        val operations = ArrayList<ContentProviderOperation>()
        val rawContactInsertIndex = operations.size

        operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            .build())

        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            .build())

        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            .build())

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
            Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show()
            loadContacts()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
