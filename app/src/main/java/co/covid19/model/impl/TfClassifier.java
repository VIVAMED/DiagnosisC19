package co.covid19.model.impl;

import android.app.Activity;
import android.graphics.Bitmap;
import co.covid19.model.Classifier;
import co.covid19.model.DiagnosisEnum;
import co.covid19.model.exceptions.ImageNotConvertedException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.nio.MappedByteBuffer;

public abstract class TfClassifier implements Classifier {
    private static final String TENSORFLOW_FOLDER_NAME = "tensorflow/";

    private static final float IMAGE_MEAN = 127.0f;
    private static final float IMAGE_STD = 128.0f;

    /** Input image TensorBuffer. */
    private TensorImage inputImageBuffer;

    /** Input image TensorBuffer. */
    private TensorImage inputImageBuffer;

    /** Output probability TensorBuffer. */
    private TensorBuffer outputProbabilityBuffer;

    private Interpreter model;

    public TfClassifier(final Activity activity)
    {
        this.model = initializeModel(activity);

        this.inputImageBuffer = initializeInputImageBuffer(this.model);
        this.outputProbabilityBuffer = initializeOutputTensorBuffer(this.model);
    }

    @NotNull
    @Override
    public String getFolderName() {
        return TENSORFLOW_FOLDER_NAME;
    }

    @NotNull
    @Override
    public Interpreter getModel(Activity a) {
        return this.model;
    }

    private Interpreter initializeModel(Activity a) {
        final MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(a, getModelPath());

        final Interpreter.Options tfliteOptions = new Interpreter.Options();

        tfliteOptions.setNumThreads(2);

        return new Interpreter(tfliteModel, tfliteOptions);
    }

    private int[] getImageShape(final int imageTensorIndex, @NotNull Interpreter model) {
        return model.getInputTensor(imageTensorIndex).shape();
    }

    private TensorImage initializeInputImageBuffer(@NotNull Interpreter model)
    {
        // Reads type and shape of input and output tensors, respectively.
        int imageTensorIndex = 0;
        int[] imageShape = model.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}

        final DataType imageDataType = model.getInputTensor(imageTensorIndex).dataType();

        return new TensorImage(imageDataType);
    }

    private TensorBuffer initializeOutputTensorBuffer(@NotNull Interpreter model)
    {
        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                model.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType probabilityDataType = model.getOutputTensor(probabilityTensorIndex).dataType();

        // Creates the output tensor and its processor.
        return TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
    }

    @NotNull
    @Override
    public TensorImage imageToTensor(@Nullable Bitmap image) throws ImageNotConvertedException {

        try {
            final int[] imageShape = getImageShape(0, getModel(null));

            final int imageSizeY = imageShape[1];
            final int imageSizeX = imageShape[2];
            // Creates processor for the TensorImage.
            int cropSize = Math.min(image.getWidth(), image.getHeight());

            ImageProcessor imageProcessor =
                    new ImageProcessor.Builder()
                            .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                            .add(new ResizeOp(imageSizeX, imageSizeY, ResizeMethod.NEAREST_NEIGHBOR))
                            .add(new NormalizeOp(IMAGE_MEAN, IMAGE_STD))
                            .build();
            return imageProcessor.process(inputImageBuffer);
        } catch (Exception e) {
            throw new ImageNotConvertedException(
                    "Image was not converted successfully: " + e.getMessage(),
                    e.getCause()
            );
        }

    }


    @NotNull
    @Override
    public Object getOutputTensor(@NotNull Object inputTensor) {
        return null;
    }

    @NotNull
    @Override
    public DiagnosisEnum getResult(@NotNull Object outputTensor) {
        return null;
    }
}
