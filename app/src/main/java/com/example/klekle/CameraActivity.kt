/**
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.klekle

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.klekle.databinding.ActivityCameraBinding
import com.example.klekle.util.DetectHoldRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CameraActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val TAG = "TFLite - ODT"
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        const val REQUEST_IMAGE_SELECT: Int = 1
        private const val MAX_FONT_SIZE = 96F
    }
    private lateinit var binding: ActivityCameraBinding

    private lateinit var captureImageFab: View
    private lateinit var inputImageView: ImageView
    private lateinit var tvPlaceholder: TextView
    private lateinit var currentPhotoPath: String

    lateinit var wallImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        captureImageFab = findViewById(R.id.captureImageFab)
        inputImageView = findViewById(R.id.imageView)
        tvPlaceholder = findViewById(R.id.tvPlaceholder)

        captureImageFab.setOnClickListener(this)
        binding.btnSelectFromGallery.setOnClickListener(this)
        binding.btnGoToFeedback.setOnClickListener(this)
    }

    private fun moveToFeedBack() {
        // '사진 촬영' 또는 '갤러리 선택'으로 불러온 이미지가 있으면
        val image : Bitmap = binding.imageView.drawable.toBitmap()

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 50, baos)
        val bytes = baos.toByteArray()
        wallImage = Base64.encodeToString(bytes, Base64.DEFAULT)

//                intent.putExtra("inputImage", byteArrayD)
        // 원래는 화면 전환을 하면서 bitmap을 그대로 다음 activity로 보내려고 했는데,
        // todo: [BUG] bitmap 이미지가 너무 크면 작동하지 않는 현상이..
        // todo: bitmap을 그대로 건낼 게 아니라, 장치에서 저장하고, URI를 가져와서, 그 path를 넘기도록 하는 방법이 있나?

        val responseListener: com.android.volley.Response.Listener<String> =
            com.android.volley.Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")
                    val result = jsonObject.getJSONArray("results")
                    if (success) { // 변경에 성공한 경우
                        Log.d("D:Test", "$result")
                        Log.d("D:Test", "${result[0]}")
                    } else {
                        Toast.makeText(this, "서버와 통신에 실패했습니다.\n잠시 뒤에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val detectHoldRequest =
            DetectHoldRequest(wallImage, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(detectHoldRequest)

//        callDetectHold() // detect hold api 호출
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                setView(getCapturedImage())
                thereIsImage()
            }
        }
    }

    /**
     * onClick(v: View?)
     *      Detect touches on the UI components
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.captureImageFab -> {
                try {
                    dispatchTakePictureIntent()
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, e.message.toString())
                }
            }
            R.id.btn_selectFromGallery -> {
                try {
                    selectFromGallery()
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, e.message.toString())
                }
            }
            R.id.btn_goToFeedback -> {
                moveToFeedBack()
            }
        }
    }

    private fun thereIsImage() {
        binding.btnGoToFeedback.isEnabled = true
        binding.tvLetsGo.setTextColor(resources.getColor(R.color.primary_600))
        binding.ivLetsGo.setColorFilter(R.color.primary_600)
    }


    /**
     * setView(bitmap: Bitmap)
     *      Set image to view and call object detection
     */
    private fun setView(bitmap: Bitmap) {
        // Display capture image
        inputImageView.setImageBitmap(bitmap)
        tvPlaceholder.visibility = View.INVISIBLE
    }

    /**
     * getCapturedImage():
     *      Decodes and crops the captured image from camera.
     */
    private fun getCapturedImage(): Bitmap {
        // Get the dimensions of the View
        val targetW: Int = inputImageView.width
        val targetH: Int = inputImageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inMutable = true
        }
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(bitmap, 270f)
            }
            else -> {
                bitmap
            }
        }
    }

    /**
     * rotateImage():
     *     Decodes and crops the captured image from camera.
     */
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    /**
     * createImageFile():
     *     Generates a temporary image file for the Camera app to write to.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    /**
     * dispatchTakePictureIntent():
     *     Start the Camera app to take a photo.
     */
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    Log.e(TAG, e.message.toString())
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "org.tensorflow.codelabs.objectdetection.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun selectFromGallery() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        intent.type = "image/*"
        activityResult.launch(intent)
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            val uri = it.data!!.data

            Glide.with(this)
                .load(uri)
                .into(inputImageView)

            thereIsImage()
        }
    }

//    private fun callDetectHold() {
//        mRetrofit = Retrofit
//            .Builder()
//            .baseUrl(getString(R.string.flaskBaseUrl))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val result = mRetrofit.create(HoldDetectAPI::class.java)
//        val param = HoldDTO()
//        param.image = wallImage
//
//        result.getHoldList(param).enqueue(object : Callback<HoldDTO> {
//            override fun onResponse(call: Call<HoldDTO>, response: Response<HoldDTO>) {
//                val holds = response.body()
//                Log.d("D:test", "$response")
//                Log.d("D:test", "$holds")
//            }
//
//            override fun onFailure(call: Call<HoldDTO>, t: Throwable) {
//                Log.e("E:error", "${t.printStackTrace()}")
//            }
//        })
//    }
}