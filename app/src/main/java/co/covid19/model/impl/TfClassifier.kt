package co.covid19.model.impl

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.renderscript.Element.DataType
import co.covid19.model.Classifier
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op


abstract class TfClassifier: Classifier {
    override val folderName: String = "tensorflow/"
    private val IMAGE_MEAN = 127.0f
    private val IMAGE_STD = 128.0f

    fun initializeInputImageBuffer(): TensorImage {
        // Reads type and shape of input and output tensors, respectively.
        val imageTensorIndex = 0

        val imageDataType: DataType = tflite.getInputTensor(imageTensorIndex).dataType()

        val inputImageBuffer = new TensorImage(imageDataType)

        return inputImageBuffer
    }

    protected open fun getPreprocessNormalizeOp(): TensorOperator? {
        return NormalizeOp(IMAGE_MEAN, IMAGE_STD)
    }

    override fun imageToTensor(image: Bitmap): TensorImage {
        val inputImageBuffer = initializeInputImageBuffer()
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(image)
        // Reads type and shape of input and output tensors, respectively.
        val imageTensorIndex = 0
        val imageShape: IntArray =
            tflite.getInputTensor(imageTensorIndex).shape() // {1, height, width, 3}

        val imageSizeY = imageShape[1]
        val imageSizeX = imageShape[2]


        // Creates processor for the TensorImage.
        // Creates processor for the TensorImage.
        val cropSize: Int = Math.min(bitmap.getWidth(), bitmap.getHeight())
        val numRotation: Int = sensorOrientation / 90
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeMethod.NEAREST_NEIGHBOR))
            .add(Rot90Op(numRotation))
            .add(getPreprocessNormalizeOp())
            .build()
        return imageProcessor.process(inputImageBuffer)

    }
}