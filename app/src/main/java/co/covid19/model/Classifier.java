package co.covid19.model;

import android.app.Activity;
import android.graphics.Bitmap;

public interface Classifier<T, V> {
    String getFolderName();
    String getModelPath();
    T getModel(Activity a);
    V imageToTensor(Bitmap b);
    void loadModelFromPath();
    V getOutputTensor(V inputTensor);
    DiagnosisEnum getResult(V outputTensor);
}
