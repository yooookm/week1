package com.example.week1_5

import SwipeController
import SwipeControllerActions
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
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

        itemlist = ArrayList<contactInfo>()

        val adapter = contactAdapter(itemlist)
        val swipeController = SwipeController(adapter, object : SwipeControllerActions(){
            override fun delete_contact(position:Int){
                val id = adapter.getId(position)
                deleteContactById(id)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, adapter.itemCount)
            }

            override fun edit_contact(position:Int){
                val id = adapter.getId(position)
                showEditContactDialog(id)
                adapter.notifyItemChanged(position)
            }
        })

        val contactRV : RecyclerView = findViewById(R.id.contact_RV)
        contactRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        contactRV.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        contactRV.addItemDecoration(object : ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c)
            }
        })

        adapter.notifyDataSetChanged()
        contactRV.adapter = adapter


        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(contactRV)


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
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER),
                null,
                null,
                null)

            cursor?.let {
                if (it.moveToFirst()) {
                    do {
                        val id = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                        val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        itemlist.add(contactInfo(id,name, phone))
                    } while (it.moveToNext())
                }
                it.close()

                // Sort the contact list by name
                itemlist.sortBy { it.name }

            }
        }
    }

    fun showEditContactDialog(id:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Contact")

        val inputLayout = LinearLayout(this)
        inputLayout.orientation = LinearLayout.VERTICAL

        val nameInput = EditText(this)
        nameInput.hint = "Name"
        nameInput.setText(nameInput.text)
        inputLayout.addView(nameInput)

        val phoneInput = EditText(this)
        phoneInput.hint = "Phone number"
        phoneInput.setText(phoneInput.text)
        inputLayout.addView(phoneInput)

        builder.setView(inputLayout)

        builder.setPositiveButton("OK") { dialog, _ ->
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                editContactByID(id,name,phone)
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


    fun deleteContactById(id: String) {
        val contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id)
        val rowsDeleted = contentResolver.delete(contactUri, null, null)

        if (rowsDeleted > 0) {
            Log.i("ContactUtils", "Deleted contact with id: $id")
        } else {
            Log.i("ContactUtils", "No contact found with id: $id")
        }
        loadContacts()
    }
    fun editContactByID(id: String, newName: String, newNumber: String) {
        // For updating name
        val nameValues = ContentValues()
        nameValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)

        val nameSelection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
        val nameSelectionArgs = arrayOf(id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        val rowsEditName = contentResolver.update(ContactsContract.Data.CONTENT_URI, nameValues, nameSelection, nameSelectionArgs)

        // For updating phone number
        val phoneValues = ContentValues()
        phoneValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)

        val phoneSelection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
        val phoneSelectionArgs = arrayOf(id, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        val rowsEditPhone = contentResolver.update(ContactsContract.Data.CONTENT_URI, phoneValues, phoneSelection, phoneSelectionArgs)

        if (rowsEditName > 0 && rowsEditPhone > 0) {
            Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show()
            loadContacts()
        } else {
            Toast.makeText(this, "Contact update failed", Toast.LENGTH_SHORT).show()
        }
        loadContacts()
    }
}
