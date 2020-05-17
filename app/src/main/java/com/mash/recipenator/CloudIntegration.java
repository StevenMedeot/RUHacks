package com.mash.recipenator;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;



import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import androidx.annotation.NonNull;

public class CloudIntegration
{

    public static String message = "";
    public static boolean complete = false;
    private final String CLOUD_API_KEY = "AIzaSyAWLw0Gm4k4FqpliKINRGAsSrjyyGLlq48";

    public static String GetFromImage(final Context context, Bitmap bitmap, final float confidenceThreshold)
    {
        complete = false;
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);


        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();


        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels)
                    {
                        // Task completed successfully
                        // ...
                        message = "";
                        for (FirebaseVisionImageLabel label: labels) {
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            float confidence = label.getConfidence();
                            if(confidence < confidenceThreshold/100)
                                continue;
                            message += text + " ";
                        }
                        complete = true;
                        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Toast.makeText(context, "Error please try again.", Toast.LENGTH_SHORT).show();
                        message = "Ingredient";
                        complete = true;
                    }
                });

        return message;

    }

}
