package co.covid19.model.impl;
import android.R.attr.bitmap;
import android.graphics.Bitmap;
import android.renderscript.Element.DataType;
import co.covid19.model.Classifier;
import co.covid19.model.DiagnosisEnum;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.Interpreter;

import java.nio.MappedByteBuffer;

public abstract class TfClassifier implements Classifier {
    private static final String TENSORFLOW_FOLDER_NAME = "pytorch/";

    private MappedByteBuffer tfliteModel;
    @NotNull
    @Override
    public String getFolderName() {
        return TENSORFLOW_FOLDER_NAME;
    }

    @NotNull
    @Override
    public Interpreter getModel() {
        return null;
    }

    @Override
    public void setModel(@NotNull Interpreter model) {

    }

    @NotNull
    @Override
    public Object imageToTensor(@Nullable Bitmap image) {
        return null;
    }

    @Override
    public void loadModelFromPath() {

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
