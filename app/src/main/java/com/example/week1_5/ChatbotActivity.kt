package com.example.week1_5

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.File

class ChatbotActivity : AppCompatActivity() {
    lateinit var imageProcessor: ImageProcessor
    lateinit var bitmap: Bitmap
    private val REQUEST_GALLERY_PERMISSION = 100
    private val REQUEST_GALLERY = 101
    val openAI = OpenAI("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")
    private lateinit var viewModel: ChatbotViewModel
    private lateinit var adapter: ChatAdapter

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
        viewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)

        val chatbox = findViewById<EditText>(R.id.chatbox)
        val callApiButton = findViewById<Button>(R.id.api_button)
        val chatHistoryView = findViewById<RecyclerView>(R.id.chat_history)
        val openGalleryButton = findViewById<Button>(R.id.gallery_button)
        openGalleryButton.setOnClickListener {
            openGallery()
        }
        adapter = ChatAdapter(viewModel.chatHistory.value!!)
        chatHistoryView.layoutManager = LinearLayoutManager(this)
        chatHistoryView.adapter = adapter

        callApiButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val userInput = withContext(Dispatchers.Main) {
                    val input = chatbox.text.toString()
                    chatbox.setText("") // Clear the EditText
                    viewModel.chatHistory.value!!.add(ChatMessage(ChatRole.User, input))
                    adapter.notifyItemInserted(viewModel.chatHistory.value!!.size - 1)
                    input
                }
                callOpenAI(userInput)
            }

        }
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun callOpenAI(userInput: String) {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = "You are my girlfriend. Answer me kindly like a girlfriend. But in informal speech. Sympathize with my feelings."
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = userInput
                )
            ),
            maxTokens = 50
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        withContext(Dispatchers.Main) {
            Log.d("Result", "${completion.choices.first().message?.content}")
            Toast.makeText(this@ChatbotActivity, "${completion.choices.first().message?.content}", Toast.LENGTH_SHORT).show()
            viewModel.chatHistory.value!!.add(ChatMessage(ChatRole.Assistant, completion.choices.first().message?.content ?: "")) // Store AI's response
            adapter.notifyItemInserted(viewModel.chatHistory.value!!.size - 1) // Notify the adapter that the data set has changed
        }
    }

    private fun image_process(bitmap: Bitmap): List<String> {
        // Load the model
        val model = SsdMobilenetV11Metadata1.newInstance(this)

        // Process the image
        var image = TensorImage.fromBitmap(bitmap)
        image = imageProcessor.process(image)

        // Get the detection results
        val outputs = model.process(image)
        val locations = outputs.locationsAsTensorBuffer.floatArray
        val classes = outputs.classesAsTensorBuffer.floatArray
        val scores = outputs.scoresAsTensorBuffer.floatArray
        val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer.floatArray[0].toInt()
        Log.d("num of detections", "${numberOfDetections}")
        // Load the labels from the labels file
        val labels = loadLabels()

        // Create a list to store the tags
        val tags = mutableListOf<String>()

        // For each detection, create a tag and add it to the list
        for (i in 0 until numberOfDetections) {
            val label = labels[classes[i].toInt()]
            val tag = "Object: $label, Location: ${locations[i]}, Score: ${scores[i]}"
            tags.add(tag)
        }

        // Release model resources
        model.close()

        // Return the list of tags
        return tags
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
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
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val tags = image_process(bitmap)
            Log.d("ImageProcess", "Tags: ${tags.joinToString()}")
        }
    }


}
