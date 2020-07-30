package co.covid19.model;

import android.app.Activity;
import android.graphics.Bitmap;

import co.covid19.model.exceptions.ImageNotConvertedException;

public interface Classifier<T, V> {
    String getFolderName();
    String getModelPath();
    T getModel(Activity a);
    V imageToTensor(Bitmap b) throws ImageNotConvertedException;
    V getOutputTensor(V inputTensor);
    DiagnosisEnum getResult(V outputTensor);
}
