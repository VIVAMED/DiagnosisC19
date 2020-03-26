package co.covid19.diagnosis

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var context: Context = application as Context
    private var module: Module

    private var imageUri: Uri? = null

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _result = MutableLiveData<String>()
    var result: LiveData<String> = _result

    private var _bitmap = MutableLiveData<Bitmap>()
    var bitmap: LiveData<Bitmap> = _bitmap

    init {
        module = Module.load(
            assetFilePath(
                context,
                "chk_resnet_50_epoch_14.pt"
            )
        )
    }

    fun setImagePath(path: String){
        _result.value = "Procesando .."
        imageUri = Uri.fromFile(File(path))
        _bitmap.value = createBitmap()
        getResult()
    }

    private fun getResult() {
        uiScope.launch {
            _result.value = runModelExec()
        }
    }

    private suspend fun runModelExec(): String? {
        return withContext(Dispatchers.IO) {
            val result= runModel()
            result
        }
    }

    private fun runModel(): String {
        // preparing input tensor
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            _bitmap.value,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )

        // running the model
        val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()

        // getting tensor content as java array of floats
        val scores = outputTensor.dataAsFloatArray

        // searching for the index with maximum score

        // searching for the index with maximum score
        var maxScore = -Float.MAX_VALUE
        var maxScoreIdx = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }

//        val className: String = ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx]
        return ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx]

        // showing className on UI
//        result_view.text = className
    }

    private fun createBitmap(): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

        } catch (e: IOException) {
            Log.e(TAG, "Error creating bitmap", e)
        }
        return bitmap
    }


    /**
     * Copies specified asset to the file in /files app directory and returns this file absolute path.
     *
     * @return absolute file path
     */
    @Throws(IOException::class)
    fun assetFilePath(
        context: Context,
        assetName: String?
    ): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName!!).use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.absolutePath
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object{
        private const val TAG = "MainActivity"
    }

    object ImageNetClasses {
        var IMAGENET_CLASSES = arrayOf(
            "Covid (Unhealthy)",
            "Normal (Healthy)"
        )
    }
}
