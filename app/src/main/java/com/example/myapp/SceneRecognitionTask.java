package com.example.myapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import edmt.dev.edmtdevcognitivevision.Contract.AnalysisResult;
import edmt.dev.edmtdevcognitivevision.Contract.Caption;
import edmt.dev.edmtdevcognitivevision.Rest.VisionServiceException;
import edmt.dev.edmtdevcognitivevision.VisionServiceClient;

// This class allows you to perform background operations and publish results on the UI thread
// Basically it shows the progress dialog meanwhile scene recognition API is recognizing
public class SceneRecognitionTask extends AsyncTask<InputStream, String, String>
{
    // Main Activity Class Object
    private MainActivity mainActivity;
    // Microsoft Computer Vision Client
    private VisionServiceClient visionServiceClient;
    // Progress Popup
    private ProgressDialog progressDialog;

    // Class Constructor
    public SceneRecognitionTask(VisionServiceClient visionServiceClient, MainActivity mainActivity)
    {
        this.visionServiceClient = visionServiceClient;
        this.mainActivity = mainActivity;
        this.progressDialog = new ProgressDialog(mainActivity);
    }

    // Invokes the UI thread before the task is executed
    @Override
    protected void onPreExecute()
    {
        // Shows Progress Bar in the UI
        progressDialog.show();
    }

    // Invokes on the background thread immediately after onPreExecute() finishes executing
    @Override
    protected String doInBackground(InputStream... inputStreams)
    {
        try
        {
            // Publish message in progress dialog
            publishProgress("Recognizing");
            String[] features = {"Description"}; // Get Description from API return result
            String[] details = {};

            // Send image(input stream) to API and get description and details
            AnalysisResult result = visionServiceClient.analyzeImage(inputStreams[0], features, details);

            String JSONResult = new Gson().toJson(result);
            return JSONResult;

        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (VisionServiceException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    // Invokes on the UI thread after the background computation finishes
    @Override
    protected void onPostExecute(String s)
    {
        if (TextUtils.isEmpty(s))
        {
            Toast.makeText(mainActivity, "API returned empty result", Toast.LENGTH_SHORT);
        } else
        {
            progressDialog.dismiss();

            AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
            StringBuilder stringResult = new StringBuilder();

            for (Caption caption : result.description.captions)
            {
                stringResult.append(caption.text);
            }

            String displayText = stringResult.toString();
            TextView textView = mainActivity.findViewById(R.id.txt_result);
            textView.setText(displayText);

            mainActivity.speak(displayText);

        }
    }

    // Invokes on the UI thread after a call to publishProgress(Progress...).
    @Override
    protected void onProgressUpdate(String... values)
    {
        // Sets message to "Recognizing"
        progressDialog.setMessage(values[0]);
    }
}
