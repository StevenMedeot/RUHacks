package com.mash.recipenator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.Html;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.io.IOException;


public class RecipeActivity extends AppCompatActivity {


    TextView text;
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        text=(TextView)findViewById(R.id.ingredients);

        Button button=(Button) findViewById(R.id.getBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoSomething().execute();
            }
        });

    }

    public class DoSomething extends AsyncTask<Void,Void,Void>{
        String words;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String encoding = "UTF-8";
                String var = "";
                String searchText = message;
                Document google = Jsoup.connect("https://www.google.com/search?q=" + URLEncoder.encode(searchText, encoding)).userAgent("Mozilla/5.0").get();

                Elements recipeResults = google.getElementsByClass("kCrYT");
                if (recipeResults.isEmpty()) {
                    System.out.println("Search yielded no results :(");
                }

                for (Element result : recipeResults) {
                    Elements link = result.getElementsByTag("a");
                    //System.out.println(link);
                    if(link.isEmpty()) {
                        continue;
                    }
                    String fullUrl = link.attr("href").toString();
                    var += fullUrl.substring(7,fullUrl.indexOf("&"));
                    //System.out.println(fullUrl.substring(7,fullUrl.indexOf("/&")));

                }
                words=var;

            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            text.setText(words); //pass words in param for other code

        }
    }

}
