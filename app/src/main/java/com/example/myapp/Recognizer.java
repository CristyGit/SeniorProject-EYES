package com.example.myapp;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import edmt.dev.edmtdevcognitivevision.VisionServiceClient;
import edmt.dev.edmtdevcognitivevision.VisionServiceRestClient;

// This class contains runs the API's recognition functions
public class Recognizer
{
    // API Key and link for Microsoft Computer Vision API
    private final String API_KEY = "e142c9270906485a9dc505c268ffa409";
    private final String API_LINK = "https://visuallyimpairedapp.cognitiveservices.azure.com/vision/v1.0";

    // Creates connection with API
    VisionServiceClient visionServiceClient = new VisionServiceRestClient(API_KEY, API_LINK);

    // Initializing UI text view component
    private TextView textView;
    // Initializing Main class object
    private MainActivity mainActivity;

    // Class Constructor
    public Recognizer(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        textView = mainActivity.findViewById(R.id.txt_result);
    }

    public void runTextRecognition(Bitmap bitmap)
    {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>()
                {
                    @Override
                    public void onSuccess(FirebaseVisionText texts)
                    {
                        String resultText = texts.getText();
                        textView.setText(resultText);
                        mainActivity.speak(resultText);
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        // Task failed with an exception
                        e.printStackTrace();
                    }
                });
    }

//    // Microsoft Computer Vision API for Text Recognition
//    public void runTextRecognition(Bitmap bitmap)
//    {
//        // Create output stream byte array
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        // Compress given Bitmap to output stream and keep quality 100%
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//
//        // Create an Async task for object recognition
//        AsyncTask<byte[], String, String> visionTask = new TextRecognitionTask(mainActivity);
//
//        // Execute Async task and send image to task
//        // Request API and display progress dialog in UI at the same time
//        visionTask.execute(outputStream.toByteArray());
//    }

    // Google Image Labels API
//    public void runObjectRecognition(Bitmap bitmap)
//    {
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
//
//        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
//                .getOnDeviceImageLabeler();
//
//        labeler.processImage(image)
//                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>()
//                {
//                    @Override
//                    public void onSuccess(List<FirebaseVisionImageLabel> labels)
//                    {
//                        // Task completed successfully
//                        // ...
//                        StringBuilder stringResult = new StringBuilder();
//
//                        for (FirebaseVisionImageLabel label : labels)
//                        {
//                            if (label.getConfidence() > 0.8)
//                                stringResult.append(label.getText());
//                        }
//                        String displayText = stringResult.toString();
//                        mainActivity.speak(displayText);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener()
//                {
//                    @Override
//                    public void onFailure(@NonNull Exception e)
//                    {
//                        // Task failed with an exception
//                        // ...
//                    }
//                });
//    }

    // Microsoft Computer Vision API (Separate Github Library) for Scene Recognition
    public void runSceneRecognition(Bitmap bitmap)
    {
        // Convert Bitmap to ByteArray
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Use Async to request API
        AsyncTask<InputStream, String, String> visionTask = new SceneRecognitionTask(visionServiceClient, mainActivity);
        visionTask.execute(inputStream);
    }

    // Microsoft Computer Vision API for Object Recognition
    public void runObjectRecognition(Bitmap bitmap)
    {
        // Create output stream byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Compress given Bitmap to output stream and keep quality 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        // Create an Async task for object recognition
        AsyncTask<byte[], String, String> visionTask = new ObjectRecognitionTask(mainActivity);

        // Execute Async task and send image to task
        // Request API and display progress dialog in UI at the same time
        visionTask.execute(outputStream.toByteArray());
    }
}
