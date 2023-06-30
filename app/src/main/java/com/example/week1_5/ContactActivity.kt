package com.example.week1_5

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1_5.databinding.ContactViewBinding

class ContactActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding: ContactViewBinding
        lateinit var requestLauncher: ActivityResultLauncher<Intent>

        super.onCreate(savedInstanceState)
        binding = ContactViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contactRV = findViewById<RecyclerView>(R.id.contact_RV)
        val itemlist = ArrayList<contactInfo>()

        //사용자가 퍼미션 허용했는지 확인
        val status = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS")
        if (status == PackageManager.PERMISSION_GRANTED) {
            Log.d("test", "permission granted")
        } else {
            //퍼미션 요청 다이얼로그 표시
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>("android.permission.READ_CONTACTS"),
                100
            )
            Log.d("test", "permission denied")
        }

        // ActivityResultLauncher 초기화, 결과 콜백 정의
        requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    // 주소록 상세 정보 가져오기
                    val cursor = contentResolver.query(
                        it.data!!.data!!,
                        arrayOf<String>(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ),
                        null,
                        null,
                        null
                    )
                    Log.d("test", "cursor size : ${cursor?.count}")
                    if (cursor!!.moveToFirst()) {
                        val name = cursor.getString(0)
                        val phone = cursor.getString(1)
                        itemlist.add(contactInfo(name,phone))
                    }

                }


                val contactAdapter1 = contactAdapter(itemlist)
                contactAdapter1.notifyDataSetChanged()

                contactRV.adapter = contactAdapter1
                contactRV.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
    }

    // 다이얼 로그에서 퍼미션 허용했는지 확인
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("test","permission granted")
        } else {
            Log.d("test","permission denied")
        }
    }

}