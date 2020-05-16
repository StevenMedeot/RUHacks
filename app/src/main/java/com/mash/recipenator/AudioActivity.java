package com.mash.recipenator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.UUID;

public class AudioActivity extends AppCompatActivity
{

    private static final int PERMISSION_CODE = 1000;
    private static final int AUDIO_CAPTURE_CODE = 1002;


    private Button m_record, m_stopRecord, m_startPlay, m_stopPlay;
    private String m_pathSave = "";
    private MediaRecorder m_mediaRecorder;
    private MediaPlayer m_mediaPlayer;


    private Uri m_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_activity);

        m_record = findViewById(R.id.record_but);
        m_stopRecord = findViewById(R.id.stop_but);
        m_startPlay = findViewById(R.id.play_but);
        m_startPlay = findViewById(R.id.pause_but);

        // What happens on button click
        m_record.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // System os check
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
                    {
                        // No permissions, will fool with this later
                        String[] permission  = {Manifest.permission.RECORD_AUDIO};
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

    private void RecordAudio()
    {

        m_pathSave = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + UUID.randomUUID().toString() +"_test.3gp";

        //setupMediaRecorder here;

        try{
            m_mediaRecorder.prepare();
            m_mediaRecorder.start();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

    }

    /*
    CAMERA PICTURE TAKING TEST

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private Button m_photoButton;
    private ImageView m_imageView;

    private Uri m_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        // Find the buttons
        m_photoButton = findViewById(R.id.photo_button);
        m_imageView = findViewById(R.id.test_image);

        // What happens on button click
        m_photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // System os check
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                    {
                        // No permissions, will fool with this later
                        String[] permission  = {Manifest.permission.CAMERA};
                        //Asks for permission
                        requestPermissions(permission, PERMISSION_CODE);

                    }
                    else
                    {
                        OpenCamera();
                    }
                }
                else
                {
                    OpenCamera();
                }
            }
        });

    }

    private void OpenCamera()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "New Picture");
        m_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, m_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
            m_imageView.setImageURI(m_uri);
    }
    */

}
