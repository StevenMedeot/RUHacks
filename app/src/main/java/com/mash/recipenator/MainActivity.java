
package com.mash.recipenator;
import android.Manifest;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity
{

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    ArrayList<TextView> ingredientList = new ArrayList<TextView>();

    LinearLayout ingredientLayout;

    private Uri m_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ingredientLayout = findViewById(R.id.ingredient_layout);
        AddTextView();

        final Button buttonAudio = findViewById(R.id.audio_b);
        Button buttonImage = findViewById(R.id.image_b);
        Button newIngButton = findViewById(R.id.newIngredient_b);

        buttonAudio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AudioButton(buttonAudio);
            }
        });

        buttonImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ImageButton();
            }
        });

        newIngButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AddTextView();
            }
        });
    }


    void MoveToActivity(Class<?> type)
    {
        Intent intent = new Intent(MainActivity.this, type);
        startActivity(intent);
    }


    private void AudioButton(Button button)
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
                getSpeech(button);
            }
        }
        else
        {
            getSpeech(button);
        }
    }

    private void ImageButton()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED )
            {
                // No permissions, will fool with this later
                String[] permission  = {Manifest.permission.CAMERA ,Manifest.permission.INTERNET};
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

                    String[] splitStrings = result.get(0).split(" ");

                    for(int i = 0; i < splitStrings.length; i++)
                    {
                        AddTextView();
                        int count = ingredientList.size();
                        ingredientList.get(count - 1).setText(splitStrings[i]);
                    }


                }
                break;
            case IMAGE_CAPTURE_CODE:
                if (resultCode == RESULT_OK)
                {
                    ImageView temp = new ImageView(this);
                    temp.setImageURI(m_uri);

                    Bitmap b = ((BitmapDrawable)(temp.getDrawable())).getBitmap();

                    GetFromImage(this, b, 60.0f);
                }
                break;
        }
    }

    protected void AddTextView()
    {
        int count = ingredientList.size();
        String text = " ";
        if(count > 0)
        {
            text = ingredientList.get(count - 1).getText().toString();
        }

        if((text.compareTo("") != 0 && text.compareTo("Ingredient") != 0) || count <= 0)
        {
            final LinearLayout newLayout = new LinearLayout(this);
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            final EditText newView = new EditText(this);
            newView.setTextAppearance(getApplicationContext(), R.style.EditText);
            newView.setText("Ingredient");

            Button button = new Button(this);
            button.setTextAppearance(getApplicationContext(), R.style.AppTheme);
            newView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 2));
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 20));
            newLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            newLayout.addView(newView);
            newLayout.addView(button);

            button.setText("X");
            button.setBackgroundColor(getResources().getColor(R.color.buttonHover));
            button.setScaleX(0.75f);
            button.setScaleY(0.75f);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    ingredientLayout.removeView(newLayout);
                    ingredientList.remove(newView);
                }
            });


            ingredientLayout.addView(newLayout);
            ingredientList.add(newView);
        }

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


    public void GetFromImage(final Context context, Bitmap bitmap, final float confidenceThreshold)
    {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();


        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels)
                    {
                        // Task completed successfully
                        // ...
                        String message = "";
                        for (FirebaseVisionImageLabel label: labels) {
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            float confidence = label.getConfidence();
                            if(confidence < confidenceThreshold/100)
                                continue;
                            message += text + " ";
                        }

                        String[] splitStrings = message.split(" ");

                        for(int i = 0; i < splitStrings.length; i++)
                        {
                            AddTextView();
                            int count = ingredientList.size();
                            ingredientList.get(count - 1).setText(splitStrings[i]);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Toast.makeText(context, "Error please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}


