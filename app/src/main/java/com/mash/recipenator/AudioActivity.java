package com.mash.recipenator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AudioActivity extends AppCompatActivity
{

    private static final int PERMISSION_CODE = 1000;
    private static final int AUDIO_CAPTURE_CODE = 1002;


    private Button m_record;
    private String m_pathSave = "";
    private MediaRecorder m_mediaRecorder;


    private Uri m_uri;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_activity);
        txtResult = (TextView) findViewById(R.id.txtResult);
        m_record = findViewById(R.id.record_but);

        // What happens on button click
        m_record.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // System os check
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED
                            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        // No permissions, will fool with this later
                        String[] permission  = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //Asks for permission
                        requestPermissions(permission, PERMISSION_CODE);

                    }
                    else
                    {
                        RecordAudio();
                    }
                }
                else
                {
                    RecordAudio();
                }
            }
        });

    }

    public void getSpeech(View view)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent,10);
        }
        else
        {
            Toast.makeText(this, "Device does not support speech input", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case 10:
                if (resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> result =  data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtResult.setText(result.get(0));
                }
                break;
        }
    }

    private void SetupMediaRecorder()
    {
        m_mediaRecorder = new MediaRecorder();

        m_mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        m_mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        m_mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        m_mediaRecorder.setOutputFile(m_pathSave);
    }

    private void RecordAudio()
    {

        m_pathSave = this.getCacheDir().getAbsolutePath()+"/" + UUID.randomUUID().toString() +"_test.3gp";

        //setupMediaRecorder here;
        SetupMediaRecorder();
        try{
            m_mediaRecorder.prepare();
            m_mediaRecorder.start();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        m_record.setEnabled(false);

        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

    }


}
