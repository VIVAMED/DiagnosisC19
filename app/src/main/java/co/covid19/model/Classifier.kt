package co.covid19.model

import android.graphics.Bitmap

interface Classifier {
    val folderName: String
    val modelPath: String
    var model: <A : Any>
    fun imageToTensor(image: Bitmap?): <T : Any>
    fun loadModelFromPath()
    fun getOutputTensor(inputTensor: <T : Any>): <T: Any>
    fun getResult(outputTensor: <T: Any>): DiagnosisEnum
}