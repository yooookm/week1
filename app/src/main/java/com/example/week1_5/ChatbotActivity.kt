package com.example.week1_5

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.week1_5.ml.SsdMobilenetV11Metadata1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatbotActivity : AppCompatActivity() {
    lateinit var imageProcessor: ImageProcessor
    lateinit var bitmap: Bitmap
    private val REQUEST_GALLERY_PERMISSION = 100
    private val REQUEST_GALLERY = 101
    val openAI = OpenAI("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")
    var accumulatedInput = ""
    private var keywordAdapter: DiaryItemAdapter? = null
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("test", "READ permission granted")
                } else {
                    Log.d("test", "READ permission denied")
                }
            }
        }
    }

    @OptIn(BetaOpenAI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.free_view)

        imageProcessor = ImageProcessor.Builder().add(ResizeOp(300,300, ResizeOp.ResizeMethod.BILINEAR)).build()

        val openGalleryButton = findViewById<Button>(R.id.gallery_button)

        val completeButton = findViewById<Button>(R.id.complete_button)

        keywordAdapter = DiaryItemAdapter(mutableListOf()) // 초기화
        val keywordRecyclerView = findViewById<RecyclerView>(R.id.keyword_list)
        keywordRecyclerView.layoutManager = LinearLayoutManager(this)
        keywordRecyclerView.adapter = keywordAdapter



        openGalleryButton.setOnClickListener {
            openGallery()
        }

        completeButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val imageUriList = keywordAdapter?.items?.map { it.imageUri }

                val keywordsList = keywordAdapter?.items?.map {
                    async {
                        callOpenAI(it.tags.joinToString(", "))?.split("\n")?.firstOrNull()
                    }
                }?.awaitAll()

                withContext(Dispatchers.Main) {
                    val intent = Intent(this@ChatbotActivity, QuestionActivity::class.java)
                    Log.d("constructor","${ArrayList(keywordsList)[0]}")
                    intent.putParcelableArrayListExtra("imageUriList", ArrayList(imageUriList))
                    intent.putStringArrayListExtra("keywordsList", ArrayList(keywordsList))
                    startActivity(intent)
                }
            }
        }




    }
    @OptIn(BetaOpenAI::class)
    private suspend fun diaryWithMyResponse(userInput: String) {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = "I will give you an answer with your question about what you asked. Please take a good look and write a diary about my day."
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = userInput
                )
            ),
            maxTokens = 300
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        withContext(Dispatchers.Main) {
            Log.d("Result", "${completion.choices.first().message?.content}")
        }
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun callOpenAI(userInput: String): String? {
        Log.d("tags", "${userInput}")
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = "I will give you a keyword related to my day. " +
                            "Create just only one interview question about my thinking or emotion about today related to the keywords or related about some informations that are not included with keywords to write a diary." +
                            "After this QnA, you will use this Question and Answer Pair to write a daily record of me. So please focus on 'today'." +
                            "The format is as follows.\n" +
                            "Number. Content"
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = userInput
                )
            ),
            maxTokens = 100
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        Log.d("response", "${completion.choices.first().message?.content}")
        return completion.choices.first().message?.content
    }

    private fun image_process(bitmap: Bitmap): List<String> {
        Log.d("image_ process", "작동 함")
        // Load the model
        val model = SsdMobilenetV11Metadata1.newInstance(this)

        // Process the image
        var image = TensorImage.fromBitmap(bitmap)
        image = imageProcessor.process(image)

        // Get the detection results
        val outputs = model.process(image)
        val classes = outputs.classesAsTensorBuffer.floatArray
        val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer.floatArray[0].toInt()

        // Load the labels from the labels file
        val labels = loadLabels()

        // Create a list to store the objects
        val objects = mutableListOf<String>()

        // For each detection, create an object and add it to the list
        for (i in 0 until numberOfDetections) {
            val label = labels[classes[i].toInt()]
            objects.add(label)
        }

        // Release model resources
        model.close()

        // Return the list of objects without duplicates
        return objects.distinct()
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)  // 여러 이미지를 선택할 수 있도록 설정
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_GALLERY)
    }


    private fun loadLabels(): List<String> {
        val labelsFile = "labels.txt"
        val inputStream = assets.open(labelsFile)
        return inputStream.bufferedReader().readLines()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                val keywords = processImage(imageUri).toMutableList()
                Log.d("ImageProcess", "Results: \n$keywords")

                val diaryItem = DiaryItem(imageUri, keywords)
                keywordAdapter?.addItem(diaryItem)
//                keywords.forEach { keywordAdapter?.addKeyword(it) } // 여기서 null-safe 호출 사용
                keywordAdapter?.notifyDataSetChanged() // 이미지 처리가 끝난 후에 갱신하도록 이동
            }
        }
    }



    private fun processImage(imageUri: Uri): List<String> {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        val tags = image_process(bitmap)
        accumulatedInput += " " + tags.joinToString()

        // 날짜 및 태그를 포함하는 결과 List<String> 반환
        return tags
    }


}
