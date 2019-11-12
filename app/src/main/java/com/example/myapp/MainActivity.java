package com.example.myapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CameraKitEventListener, TextToSpeech.OnInitListener
{
    // UI Components
    private CameraView cameraView;
    private Button cameraButton;
    private BottomNavigationView bottomNavigationView;

    // Text to Speech Element
    private TextToSpeech textToSpeech;
    // Recognizer Class Object
    private Recognizer recognizer;

    // When the Activity is Created
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initUI();
        initRecognitionElements();
    }

    // Initialize Main Activity View, Navigation Bar, and Camera
    private void initUI()
    {
        setContentView(R.layout.activity_main);
        initNav();
        initCamera();
    }

    // Initialize Navigation Bar
    private void initNav()
    {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        bottomNavigationView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_text, R.id.navigation_scene, R.id.navigation_object, R.id.navigation_color)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    // Initialize Camera and Button
    private void initCamera()
    {
        cameraButton = findViewById(R.id.cameraBtn);
        cameraView = findViewById(R.id.camView);
        // Button Listener;
        cameraButton.setOnClickListener(new View.OnClickListener()
        {
            // When button is pressed, image is capture. It also stops text to speech if available
            @Override
            public void onClick(View v)
            {
                // Image is taken
                cameraView.start();
                cameraView.captureImage();

                // Text to speech stops if it is working
//                if (textToSpeech != null)
//                {
//                    textToSpeech.stop();
//                    textToSpeech.shutdown();
//                }
                //TODO change condition
            }
        });
    }

    // On Image taken, Convert it to a bitmap and run the corresponding API
    @Override
    public void onImage(CameraKitImage cameraKitImage)
    {
        // Create bitmap of image taken
        Bitmap bitmap = cameraKitImage.getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);

        // Stop camera meanwhile recognition is happening
        cameraView.stop();

        // Get selected Menu Option ID
        int number = bottomNavigationView.getSelectedItemId();

        // Depending on selected option, we run the corresponding API
        switch (number)
        {
            // if item selected is text, then run text recognition
            case R.id.navigation_text:
                recognizer.runTextRecognition(bitmap);
                break;
            // if item selected is scene, then run scene recognition
            case R.id.navigation_scene:
                recognizer.runSceneRecognition(bitmap);
                break;
            // if item selected is object, then run object recognition
            case R.id.navigation_object:
                recognizer.runObjectRecognition(bitmap);
                break;
            // if item selected is notifications, then run object recognition
            case R.id.navigation_color:
                recognizer.runColorRecognition(bitmap);
                break;
        }
    }

    // Initialize Recognition Elements: text to speech, recognizers (APIs) and Camera Listener
    private void initRecognitionElements()
    {
        initTextToSpeech();
        recognizer = new Recognizer(this);
        cameraView.addCameraKitListener(this);
    }

    // Initialize Text to Speech
    private void initTextToSpeech()
    {
        textToSpeech = new TextToSpeech(this, this);
    }

    // Initialize Text to Speech
    @Override
    public void onInit(int status)
    {
        // Set Text to Speech Language
        int lanResult = textToSpeech.setLanguage(Locale.US);

        // Check if language is supported
        if (lanResult == TextToSpeech.LANG_MISSING_DATA || lanResult == TextToSpeech.LANG_NOT_SUPPORTED)
        {
            Log.e("Language", "This Language is not supported");
        } else {
            Log.i("Language", "Language set to english");
        }

        // Check if Initialization Status is good
        if (status == TextToSpeech.ERROR)
        {
            Log.e("Initialization", "Text to speech initialization Failed!");
        } else
        {
            Log.i("Initialization", "Text to speech was initialized.");
        }
    }

    // Text to Speech: Receives a String, converts it to speech
    public void speak(String text) {
        if (textToSpeech == null || "".equals(text))
        {
            textToSpeech.speak("Recognition Failed", TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
        } else
        {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
        }
    }

    // When Activity is resumed, start the camera
    @Override
    public void onResume()
    {
        super.onResume();
        cameraView.start();
    }

    // When Activity is Paused, stop the camera and text to speech
    @Override
    public void onPause()
    {
        cameraView.stop();
        if (textToSpeech != null)
        {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onEvent(CameraKitEvent cameraKitEvent)
    {
    }

    @Override
    public void onError(CameraKitError cameraKitError)
    {
    }

    @Override
    public void onVideo(CameraKitVideo cameraKitVideo)
    {
    }
}
