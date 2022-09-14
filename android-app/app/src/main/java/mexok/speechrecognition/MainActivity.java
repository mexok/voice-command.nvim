package mexok.speechrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final Integer RECORD_AUDIO_REQ_CODE = 6001;

    private boolean isStarted = false;
    private Button myButton;

    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myButton = findViewById(R.id.button);
        myButton.setOnClickListener(this);
        myButton.setText("Start");
        isStarted = false;
        setupSpeechRecognizer();
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
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
                // Use a dirty hack for continuation of listening
                if (isStarted) {
                    stopSpeechRecognition();
                    startSpeechRecognition();
                }
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (results.size() > 0)
                    Toast.makeText(MainActivity.this, String.join("\n", results), Toast.LENGTH_SHORT).show();

                // Use a dirty hack for continuation of listening
                if (isStarted) {
                    stopSpeechRecognition();
                    startSpeechRecognition();
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (!isStarted) {
            myButton.setText("Stop");
            startSpeechRecognition();
        } else {
            myButton.setText("Start");
            stopSpeechRecognition();
        }
        isStarted = !isStarted;
    }

    private void startSpeechRecognition() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQ_CODE);
            return;
        }

        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    private void stopSpeechRecognition() {
        speechRecognizer.stopListening();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_REQ_CODE && grantResults.length > 0 ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechRecognition();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }
}