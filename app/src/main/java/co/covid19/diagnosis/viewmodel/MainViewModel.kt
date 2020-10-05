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
    private var imageName = MutableLiveData<String>()
    var bitmapLiveData: LiveData<Bitmap> = bitmap
    var imageNameLiveData: LiveData<String> = imageName

    private var isProcessing = MutableLiveData<Boolean>()
    var isProcessingLiveData: LiveData<Boolean> = isProcessing

    var currentSelectedAlbum = 0

    init {
        loadModel(MODEL_NAME_1)
    }

    private fun loadModel(fileName: String) {
        uiScope.launch {
            val file = loadFile(fileName)
            module = Module.load(file)
        }
    }

    private suspend fun loadFile(fileName: String): String? {
        return withContext(Dispatchers.IO) {
            val file = assetFilePath(context, fileName)
            file
        }
    }

    fun setImagePath(path: String) {
        imagePath = path
    }

    fun runModel() {
        result.value = "Procesando .."
        isProcessing.value = true
        uiScope.launch {
            val image = createBitmap()
            result.value = runModelExec(image)
            isProcessing.value = false
            bitmap.value = image
            imageName.value = getImageName()
        }
    }

    private fun getImageName(): String {
        val uri = Uri.parse(imagePath);
        return uri.lastPathSegment.toString()
    }

    private suspend fun runModelExec(bitmap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            val result = executeModel(bitmap)
            result
        }
    }

    private fun executeModel(bitmap: Bitmap): String {
        // Scale Image
        val bitmapInput = Bitmap.createScaledBitmap(bitmap, 224, 224, false)

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmapInput,
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

    // creating bitmap from packaged into app android asset 'covid_original.jpg',
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
