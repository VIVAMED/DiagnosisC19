package co.covid19.diagnosis.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.covid19.diagnosis.util.Constants.IMAGENET_CLASSES
import co.covid19.diagnosis.util.Constants.MODEL_NAME_1
import kotlinx.coroutines.*
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * [ViewModel]
 *
 * @author jaiber.yepes
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var context: Context = application
    private lateinit var module: Module

    private lateinit var imagePath: String

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var result = MutableLiveData<String>()
    var resultLiveData: LiveData<String> = result

    private var bitmap = MutableLiveData<Bitmap>()
    var bitmapLiveData: LiveData<Bitmap> = bitmap

    init {
        val file = assetFilePath(context, MODEL_NAME_1)
        file?.let {
            module = Module.load(it)
        }
    }

    fun setImagePath(path: String) {
        result.value = "Procesando .."
        imagePath = path
    }

    fun runModel() {
        uiScope.launch {
            bitmap.value = createBitmap()
            result.value = runModelExec()
        }
    }

    private suspend fun runModelExec(): String? {
        return withContext(Dispatchers.IO) {
            val result = executeModel()
            result
        }
    }

    private fun executeModel(): String {
        // creating bitmap from packaged into app android asset 'covid_original.jpg',
//        var bitmap = BitmapFactory.decodeStream(context.assets.open("covid_original.jpg"))
//        _bitmap.value = BitmapFactory.decodeFile(imageUri?.path)

        // Scale Image
        val bitmapInput = Bitmap.createScaledBitmap(bitmap.value!!, 224, 224, false)

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap.value!!,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )

        // running the model
        val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()

        // getting tensor content as java array of floats
        val scores = outputTensor.dataAsFloatArray

        // searching for the index with maximum score
        var maxScore = -Float.MAX_VALUE
        var maxScoreIdx = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }

        return IMAGENET_CLASSES[maxScoreIdx]
    }

    private suspend fun createBitmap(): Bitmap {
        return withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            bitmap
        }
    }

    /**
     * Copies specified asset to the file in /files app directory and returns this file absolute path.
     *
     * @return absolute file path
     */
    private fun assetFilePath(context: Context, assetName: String): String? {
        try {
            val file = File(context.filesDir, assetName)
            if (file.exists() && file.length() > 0) {
                return file.absolutePath
            }
            context.assets.open(assetName).use { `is` ->
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
        } catch (e: IOException) {
            Log.e("TAG", "Not open file", e)
        }
        return null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
