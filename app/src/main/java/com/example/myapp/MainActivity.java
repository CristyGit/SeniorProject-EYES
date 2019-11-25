package com.example.myapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CameraKitEventListener, TextToSpeech.OnInitListener
{
    // UI Components
    private CameraView cameraView;
    private Button cameraButton;
    private BottomNavigationView bottomNavigationView;
    private Button sendButton;
    private Button speechButton;

    // Text to Speech Element
    private TextToSpeech textToSpeech;
    // Recognizer Class Object
    private Recognizer recognizer;

    private SpeechRecognizer mySpeechRecognizer;
   // private final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private boolean speech_recog = false;
    private boolean voice_text_recog = false;
    private boolean voice_scene_recog = false;
    private boolean voice_object_recog = false;
    private boolean voice_color_recog = false;

    // When the Activity is Created
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initUI();
        initRecognitionElements();


    }



    // Initialize Main Activity View, Navigation Bar,send button, and Camera.
    private void initUI()
    {
        setContentView(R.layout.activity_main);
        sendButton = findViewById(R.id.send_button);
        speechButton = findViewById(R.id.speech_button);
        speechButton.setOnClickListener((view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What type of recognition would you like to do?");
            mySpeechRecognizer.startListening(intent);
        }));
        initNav();
        initCamera();
       // startSpeechRecog();
        initializeSpeechRecognizer();
    }

    private void initializeSpeechRecognizer() {
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            mySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
                    List<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        }
    }

    private void processResult(String command) {
        command = command.toLowerCase();
        speech_recog = true;
        if (command.indexOf("text") != -1) {
            voice_text_recog = true;
            // Capture picture
            cameraView.captureImage();
        } else if (command.indexOf("scene") != -1) {
            voice_scene_recog = true;
            // Capture picture
            cameraView.captureImage();
        } else if (command.indexOf("object") != -1) {
            voice_object_recog = true;
            // Capture picture
            cameraView.captureImage();
        } else if (command.indexOf("color") != -1) {
            voice_color_recog = true;
            // Turn flash ON
            cameraView.setFlash(CameraKit.Constants.FLASH_ON);
            // Capture picture
            cameraView.captureImage();
        }
    }


 /*   private void startSpeechRecog() {
        speechButton = findViewById(R.id.speech_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.captureImage();

                startVoiceRecognitionActivity();
            }
        });
    }*/

   /* private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "What type of recognition would you like?");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            speech_recog = true;


            super.onActivityResult(requestCode, resultCode, data);

        }
    }*/


    // Initialize Navigation Bar
    private void initNav()
    {
        bottomNavigationView = findViewById(R.id.nav_view);
        // Identifying each menu choice
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_text, R.id.navigation_scene, R.id.navigation_object, R.id.navigation_color)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    // Initialize Camera and Button. When button is pressed logic
    private void initCamera()
    {
        cameraButton = findViewById(R.id.cameraBtn);
        cameraView = findViewById(R.id.camView);

        // Button Listener;
        cameraButton.setOnClickListener(new View.OnClickListener()
        {
            // When button is pressed, image is capture.
            // It also stops text to speech if available
            // Removes send button if needed.
            // Change camera button's text
            // if color recognition is selected, set flash to on
            @Override
            public void onClick(View v)
            {
                // check if speech still there, if it is stop it
                if (textToSpeech != null)
                {
                    textToSpeech.stop();
                }

                // check if send button is there, if it is remove it
                if(sendButton.getVisibility() == View.VISIBLE)
                {
                    sendButton.setVisibility(View.INVISIBLE);
                }

                // Start camera view
                cameraView.start();

                // if button is stop, change text
                if (cameraButton.getText().equals("Stop"))
                {
                    cameraButton.setText("Recognize");
                }
                else // else change it to stop
                {
                    cameraButton.setText("Stop");
                }

                // Get selected Menu Option ID
                int number = bottomNavigationView.getSelectedItemId();

                // If color recognition is selected turn on flash
                if (number == R.id.navigation_color)
                {
                    // Turn flash ON
                    cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                    // Capture picture
                    cameraView.captureImage();
                }
                else
                {
                    // Capture picture

                    cameraView.captureImage();
                }
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

        // Stop camera preview while recognition is happening
        cameraView.stop();

        // make send button visible
        sendButton.setVisibility(View.VISIBLE);

        // Get selected Menu Option ID
        int number = bottomNavigationView.getSelectedItemId();

        // Depending on selected option, we run the corresponding API
        if(!speech_recog) {
            switch (number) {
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
        else{
            if (voice_text_recog){
                recognizer.runTextRecognition(bitmap);
                speech_recog = false;
            }
            else if(voice_scene_recog){
                recognizer.runSceneRecognition(bitmap);
                speech_recog = false;
            }
            else if(voice_object_recog){
                recognizer.runObjectRecognition(bitmap);
                speech_recog = false;
            }
            else if(voice_color_recog){
                recognizer.runColorRecognition(bitmap);
                speech_recog = false;
            }
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


    // Saves bitmap as PNG to the app's cache directory. Returns Uri of the saved file
    public Uri saveImage(Bitmap image) {
        // Get image folder
        File imagesFolder = new File(getCacheDir(), "images");
        // create uri to store
        Uri uri = null;

        try {
            // Creates directory and parent directories
            imagesFolder.mkdirs();
            // create image file
            File file = new File(imagesFolder, "shared_image.png");

            // creates outstream of file
            FileOutputStream stream = new FileOutputStream(file);
            // compress bitmap into stream
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);

            // flush aand close stream
            stream.flush();
            stream.close();

            // get URI for file
            uri = FileProvider.getUriForFile(this, "com.mydomain.fileprovider", file);

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return uri;
    }

    // When Activity is resumed, start the camera, change button text
    @Override
    public void onResume()
    {
        super.onResume();
        cameraView.start();
        cameraButton.setText("Recognize");
    }

    // When Activity is Paused, stop the camera and text to speech, hide send button
    @Override
    public void onPause()
    {
        cameraView.stop();
        if (textToSpeech != null)
        {
            textToSpeech.stop();
        }
        sendButton.setVisibility(View.INVISIBLE);
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
