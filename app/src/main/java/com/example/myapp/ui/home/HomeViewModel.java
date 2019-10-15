package com.example.myapp.ui.home;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private TextToSpeech textToSpeech;


    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}