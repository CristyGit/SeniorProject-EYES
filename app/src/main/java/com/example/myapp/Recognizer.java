package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapp.ui.color.ColorRecognitionTask;
import com.example.myapp.ui.object.ObjectRecognitionTask;
import com.example.myapp.ui.scene.SceneRecognitionTask;
import com.example.myapp.ui.text.TextRecognitionTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayOutputStream;
import java.util.List;

// This class contains runs the API's recognition functions
public class Recognizer
{
    // Initializing UI text view component
    private TextView textView;
    private Button sendButton;

    // Initializing Main class object
    private MainActivity mainActivity;

    // Class Constructor
    public Recognizer(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        textView = mainActivity.findViewById(R.id.txt_result);
        sendButton = mainActivity.findViewById(R.id.send_button);
    }

    // Send Image to Contact Method
    public void sendImageToContact(Bitmap bitmap, String text)
    {
        // Save Image
        Uri uri = mainActivity.saveImage(bitmap);

        // Create intent to send image and text recognize to contact of selection
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "EYES recognition returned:\n" + text + ".\nCan you verify this?");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendIntent.setType("image/png");

        // Create intent to share
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        // start This intent activity
        mainActivity.startActivity(shareIntent);
    }

    // Google Firebase ML Kit API (1ST CHOICE)
    public void runTextRecognition(Bitmap bitmap)
    {
        // Create firebase vision image object with given bitmap
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        // Get an instance of Text Recognizer API
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        // Process Image
        detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>()
                {
                    // Task completed successfully
                    @Override
                    public void onSuccess(FirebaseVisionText texts)
                    {
                        // Extract text
                        String resultText = texts.getText();

                        // set display text toi result
                        textView.setText(resultText);
                        // Text-to-Speech result text
                        mainActivity.speak(resultText);

                        // send button listener
                        sendButton.setOnClickListener(new View.OnClickListener()
                        {
                            // When button is pressed, share image and text with contact
                            @Override
                            public void onClick(View v)
                            {
                                sendImageToContact(bitmap, resultText);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    // Task failed with an exception
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        e.printStackTrace();
                    }
                });
    }

    // Microsoft Computer Vision API for Text Recognition (2ND CHOICE)
    public void runTextRecognition2(Bitmap bitmap)
    {
        // Create output stream byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Compress given Bitmap to output stream and keep quality 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        // Create an Async task for object recognition
        AsyncTask<byte[], String, String> visionTask = new TextRecognitionTask(mainActivity);
        // Execute Async task and send image to task
        // Request API and display progress dialog in UI at the same time
        visionTask.execute(outputStream.toByteArray());
    }

    // Microsoft Computer Vision API for Scene Recognition (ONLY CHOICE)
    public void runSceneRecognition(Bitmap bitmap)
    {
        // Create output stream byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Compress given Bitmap to output stream and keep quality 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        // Create an Async task for scene recognition
        SceneRecognitionTask<byte[], String, String> visionTask = new SceneRecognitionTask(mainActivity);

        // Execute Async task and send image to task
        // Request API and display progress dialog in UI at the same time
        visionTask.execute(outputStream.toByteArray());

        sendButton.setOnClickListener(new View.OnClickListener() {
            // When button is pressed, share image and text with contact
            @Override
            public void onClick(View v) {
                sendImageToContact(bitmap, visionTask.resultString);
            }
        });
    }

    // Microsoft Computer Vision API for Object Recognition (1ST CHOICE)
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

    // Google Image Labels API (2ND CHOICE)
    public void runObjectRecognition2(Bitmap bitmap)
    {
        // Create firebase vision image from bitmap
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        // Get an instance of Image Labeler API
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                .getOnDeviceImageLabeler();

        // Process image
        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>()
                {
                    // Task completed successfully
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels)
                    {
                        // Build a string
                        StringBuilder stringResult = new StringBuilder();

                        // Extract text/labels
                        for (FirebaseVisionImageLabel label : labels)
                        {
                            // confidence level has to be higher than 0.8
                            if (label.getConfidence() > 0.8)
                                stringResult.append(label.getText());
                        }
                        // conert builder to string
                        String displayText = stringResult.toString();

                        // set display text toi result
                        textView.setText(displayText);
                        // Text-to-Speech result text
                        mainActivity.speak(displayText);
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    // Task failed with an exception
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        e.printStackTrace();
                    }
                });
    }

    // Microsoft Computer Vision API for Color Recognition (ONLY CHOICE)
    public void runColorRecognition(Bitmap bitmap)
    {
        // Create output stream byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Compress given Bitmap to output stream and keep quality 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        // Create an Async task for color recognition
        AsyncTask<byte[], String, String> visionTask = new ColorRecognitionTask(mainActivity);

        // Execute Async task and send image to task
        // Request API and display progress dialog in UI at the same time
        visionTask.execute(outputStream.toByteArray());
    }
}
