package com.mash.recipenator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    /*
     CAMERA PICTURE TAKING TEST

     */
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private Button m_photoButton;
    private ImageView m_imageView;

    private Uri m_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonAudio = (Button) findViewById(R.id.audio_b);
        buttonAudio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, AudioActivity.class);
                startActivity(intent);
            }
        });

        Button buttonText = (Button) findViewById(R.id.text_b);
        Button buttonImage = (Button) findViewById(R.id.image_b);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            m_imageView.setImageURI(m_uri);

            Bitmap b = ((BitmapDrawable)(m_imageView.getDrawable())).getBitmap();

            String output = CloudIntegration.GetFromImage(this, b, 60.0f);

        }
    }

}
