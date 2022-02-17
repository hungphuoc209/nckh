package com.example.nckh

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.sms.ezviewbinding.viewBinding
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.example.nckh.databinding.ActivityMainBinding
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import com.theartofdev.edmodo.cropper.CropImage


class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val localModel = LocalModel.Builder().setAssetFilePath("model.tflite").build()
    private val labels = arrayListOf("Lá khỏe mạnh","Bệnh đốm lá","Bệnh khảm","Bệnh mốc lá","Bệnh úa muộn","Lá khỏe mạnh","Bệnh úa muộn")

    private val customImageLabelerOptions = CustomImageLabelerOptions.Builder(localModel)
        .setConfidenceThreshold(0.5f)
        .setMaxResultCount(5)
        .build()
    private val labeler = ImageLabeling.getClient(customImageLabelerOptions)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initListeners()
    }


    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1,1)
                .getIntent(this@MainActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityLauncher: ActivityResultLauncher<Any?>

    private fun initListeners() {
        cropActivityLauncher = registerForActivityResult(cropActivityResultContract){uri ->
            uri?.let {
                var bitmapImage : Bitmap? = null
                bitmapImage = if(Build.VERSION.SDK_INT < 28) {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        uri)
                    bitmap
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    bitmap
                }
                if(bitmapImage!=null) {
                    val image = InputImage.fromBitmap(bitmapImage, 0)
                    labeler.process(image)
                        .addOnSuccessListener {
                            binding.tvResult.text = labels[it[0].index].toString()
                            if(it[0].index!=0) binding.btnDetail.visibility = View.VISIBLE
                        }
                        .addOnFailureListener {
                            binding.tvResult.text = it.localizedMessage
                        }
                    binding.imgPicture.setImageBitmap(bitmapImage)
                }
            }
        }

        binding.btnAdd.setOnClickListener{
            cropActivityLauncher.launch(null)
        }

        binding.btnDetail.setOnClickListener{
            val intent = Intent(this@MainActivity,DetailActivity::class.java)
            startActivity(intent)
        }
    }
}