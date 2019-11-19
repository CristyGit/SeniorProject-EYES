package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapp.ui.color.ColorRecognitionTask;
import com.example.myapp.ui.object.ObjectRecognitionTask;
import com.example.myapp.ui.scene.SceneRecognitionTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public void sendImageToContact(Bitmap bitmap, String text) throws IOException
    {
        //Write file
        String filename = "image.jpe";
        FileOutputStream stream = mainActivity.openFileOutput(filename, Context.MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        //Cleanup
        stream.close();
        bitmap.recycle();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "The EYES app recognized this image as [" + text + "]. Can you verify?");
//        sendIntent.setType("text/plain");
//        sendIntent.putExtra("image", byteArray); // too large
        sendIntent.putExtra("image", filename);
        sendIntent.setType("image/jpeg");

//        Intent shareIntent = Intent.createChooser(sendIntent, null);
        mainActivity.startActivity(sendIntent);
    }

    // Google Firebase ML Kit API
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
                        // Save the text to database
                        textView.setText(resultText);
                        mainActivity.speak(resultText);
                        sendButton.setOnClickListener(new View.OnClickListener()
                        {
                            // When button is pressed, share image with contacts.
                            @Override
                            public void onClick(View v)
                            {
                                try
                                {
                                    sendImageToContact(bitmap, resultText);
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
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
//    public void runTextRecognition2(Bitmap bitmap)
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

    // Microsoft Computer Vision API for Scene Recognition
    public void runSceneRecognition(Bitmap bitmap)
    {
        // Create output stream byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Compress given Bitmap to output stream and keep quality 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        // Create an Async task for scene recognition
        AsyncTask<byte[], String, String> visionTask = new SceneRecognitionTask(mainActivity);

        // Execute Async task and send image to task
        // Request API and display progress dialog in UI at the same time
        visionTask.execute(outputStream.toByteArray());
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

//    // Google Image Labels API
//    public void runObjectRecognition2(Bitmap bitmap)
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

    // NOT WORKING
//    // Google Product Recognition API for Product Recognition
//    public void runProductRecognition(Bitmap bitmap)
//    {
//        FirebaseVisionBarcodeDetectorOptions options =
//                new FirebaseVisionBarcodeDetectorOptions.Builder()
//                        .setBarcodeFormats(
//                                FirebaseVisionBarcode.FORMAT_EAN_13,
//                                FirebaseVisionBarcode.FORMAT_EAN_8,
//                                FirebaseVisionBarcode.FORMAT_UPC_A,
//                                FirebaseVisionBarcode.FORMAT_ITF,
//                                FirebaseVisionBarcode.FORMAT_CODABAR,
//                                FirebaseVisionBarcode.FORMAT_UPC_E)
//                        .build();
//
//         FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
//        .getVisionBarcodeDetector(options);
//
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
//
//        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
//                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
//                    @Override
//                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
//                        // Task completed successfully
//
//                        for (FirebaseVisionBarcode barcode: barcodes)
//                        {
//                            // return barcode value in raw format
//                            String displayValue = barcode.getDisplayValue();
//                            // return format type of barcode
//                            int valueType = barcode.getValueType();
//
//
//                            // See API reference for complete list of supported types
//                            switch (valueType) {
//                                case FirebaseVisionBarcode.TYPE_WIFI:
//                                    String ssid = barcode.getWifi().getSsid();
//                                    String password = barcode.getWifi().getPassword();
//                                    int type = barcode.getWifi().getEncryptionType();
//                                    break;
//                                case FirebaseVisionBarcode.TYPE_URL:
//                                    String title = barcode.getUrl().getTitle();
//                                    String url = barcode.getUrl().getUrl();
//                                    break;
//                            }
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Task failed with an exception
//                        // ...
//                    }
//                });
//    }

    // Microsoft Computer Vision API for Color Recognition
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
