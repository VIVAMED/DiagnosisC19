package co.covid19.model.impl;

import android.app.Activity;
import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import co.covid19.model.Classifier;
import co.covid19.model.DiagnosisEnum;

public abstract class PytorchClassifier implements Classifier
{

    private static final String PYTORCH_FOLDER_NAME = "pytorch/";
    private Module model;

    @Override
    public String getFolderName() {
        return PYTORCH_FOLDER_NAME;
    }

    @Override
    public Module getModel(Activity a) {
        return model;
    }

    @Override
    public Tensor imageToTensor(Bitmap b) {
        return TensorImageUtils.bitmapToFloat32Tensor(
            b,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        );
    }

    @Override
    public void loadModelFromPath() {
        this.model = Module.load(getModelPath());
    }

    @Override
    public Tensor getOutputTensor(Tensor inputTensor) {
        return getModel().forward(IValue.from(inputTensor)).toTensor();
    }

    @Override
    public DiagnosisEnum getResult(Tensor outputTensor) {
        // getting tensor content as java array of floats
        final float[] scores = outputTensor.getDataAsFloatArray();

        // searching for the index with maximum score

        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }

        if (maxScoreIdx == 0) {
            return DiagnosisEnum.NOT_HEALTHY;
        } else {
            return DiagnosisEnum.HEALTHY;
        }
    }
}
