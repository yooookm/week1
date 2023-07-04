package com.example.week1_5

import android.net.Uri

data class DiaryItem(
    val imageUri: Uri,
    val tags: MutableList<String>
)
