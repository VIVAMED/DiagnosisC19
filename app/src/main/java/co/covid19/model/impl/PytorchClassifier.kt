package co.covid19.model.impl

import android.graphics.Bitmap
import co.covid19.model.Classifier
import co.covid19.model.DiagnosisEnum
import org.pytorch.IValue
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import org.pytorch.Module

abstract class PytorchClassifier: Classifier {

    override val folderName: String = "pytorch/"

    override fun  imageToTensor(image: Bitmap):Tensor {
        // preparing input tensor
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            image,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB)

        return inputTensor
    }

    override fun loadModelFromPath() {
        model = Module.load(modelPath)
    }

    override fun getOutputTensor(inputTensor: Tensor): Tensor {
        val outputTensor = model.forward(IValue.from(inputTensor)).toTensor()

        return outputTensor
    }

    override fun getResult(outputTensor: Tensor): DiagnosisEnum {

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

        if (maxScoreIdx == 0) {
            return DiagnosisEnum.NOT_HEALTHY
        } else {
            return DiagnosisEnum.HEALTHY
        }
    }
}