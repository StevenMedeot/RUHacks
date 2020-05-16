package com.mash.recipenator;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private Button m_photoButton;
    private ImageView m_imageView;
    private Vision vision;

    private Uri m_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyChsmRmjLfHo0ZhyEn6LY465KmDN-mp3uM"));

        vision = visionBuilder.build();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            m_imageView.setImageURI(m_uri);
            Bitmap b = ((BitmapDrawable)(m_imageView.getDrawable())).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] bitmapData = stream.toByteArray();
            try {
                stream.close();
            }
            catch (IOException e)
            {

            }
            Image image = new Image();
            image.encodeContent(bitmapData);

            Feature desiredFeature = new Feature();
            desiredFeature.setType("LABEL_DETECTION");

            AnnotateImageRequest request = new AnnotateImageRequest();
            request.setImage(image);
            request.setFeatures(Arrays.asList(desiredFeature));

            BatchAnnotateImagesRequest batchRequest =
                    new BatchAnnotateImagesRequest();

            batchRequest.setRequests(Arrays.asList(request));
            BatchAnnotateImagesResponse batchResponse = null;
            try {
                batchResponse = vision.images().annotate(batchRequest).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<EntityAnnotation> faces = null;
            if(batchResponse != null)
            {
                faces = batchResponse.getResponses()
                        .get(0).getLabelAnnotations();
            }

// Count faces
            int numberOfFaces = 2;

// Get joy likelihood for each face
            String likelihoods = "";
            for(int i=0; i<numberOfFaces; i++) {
                likelihoods += "\n It is " +
                        faces.get(i).getDescription();
            }

// Concatenate everything
            final String message =
                    "This photo has " + numberOfFaces + " faces" + likelihoods;

// Display toast on UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
