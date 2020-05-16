package com.mash.recipenator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    //Let user choose between text, audio, picture

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
            }
        });

        Button buttonText = (Button) findViewById(R.id.text_b);
        Button buttonImage = (Button) findViewById(R.id.image_b);
    }


}
