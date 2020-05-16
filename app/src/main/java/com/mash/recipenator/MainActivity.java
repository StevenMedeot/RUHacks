package com.mash.recipenator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonAudio = findViewById(R.id.audio_b);
        Button buttonImage = findViewById(R.id.image_b);

        buttonAudio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MoveToActivity(AudioActivity.class);
            }
        });

        buttonImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MoveToActivity(ImageActivity.class);
            }
        });
    }


    void MoveToActivity(Class<?> type)
    {
        Intent intent = new Intent(MainActivity.this, type);
        startActivity(intent);
    }


}
