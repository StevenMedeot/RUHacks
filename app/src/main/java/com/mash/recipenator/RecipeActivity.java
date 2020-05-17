package com.mash.recipenator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;
import android.text.util.Linkify;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.Html;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLEncoder;
import java.io.IOException;
import java.util.ArrayList;


public class RecipeActivity extends AppCompatActivity
{
    TextView text;
    String message = "";

    LinearLayout recipeLayout;
    ArrayList<String> imageUrls = new ArrayList<String>();
    ArrayList<ImageView> images = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeLayout = findViewById(R.id.recipe_layout);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);


        new DoSomething().execute();

    }


    View CreateNewRecipe(String link)
    {
        final LinearLayout newLayout = new LinearLayout(this);
        newLayout.setOrientation(LinearLayout.HORIZONTAL);
        final TextView newView = new TextView(this);
        newView.setTextAppearance(getApplicationContext(), R.style.EditText);
        newView.setText(link);

        Linkify.addLinks(newView, Linkify.ALL);
        final ImageView imageView = new ImageView(this);

        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 4));
        newView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
        newLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200));
        newLayout.setBackgroundColor(getResources().getColor(R.color.greyBackground));

        String imageUrl = link;
        images.add(imageView);
        imageUrls.add(imageUrl);

        newLayout.addView(imageView);
        newLayout.addView(newView);

        return newLayout;
    }

    public class DoSomething extends AsyncTask<Void,Void,Void>{
        String words;
        ArrayList<View> views = new ArrayList<View>();
        int ind = 0;
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

                    if(ind == recipeResults.size())
                        continue;
                    if(ind % 2 == 0)
                        views.add(CreateNewRecipe(fullUrl.substring(7,fullUrl.indexOf("&"))));

                    //var += fullUrl.substring(7,fullUrl.indexOf("&"));
                    //System.out.println(fullUrl.substring(7,fullUrl.indexOf("/&")));
                    ind++;


                }
                words=var;

            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Void aVoid)
        {
            super.onProgressUpdate(aVoid);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for(View view : views)
            {
                recipeLayout.addView(view);
            }

            for(int i = 0 ; i < images.size(); i++)
            {
                String[] test = null;

                if(imageUrls.get(i).contains(".com"))
                    test = imageUrls.get(i).split("\\.com");
                else if(imageUrls.get(i).contains(".ca"))
                    test = imageUrls.get(i).split("\\.ca");
                else if(imageUrls.get(i).contains(".co"))
                    test = imageUrls.get(i).split("\\.co");
                else if(imageUrls.get(i).contains(".uk"))
                    test = imageUrls.get(i).split("\\.uk");
                else if(imageUrls.get(i).contains(".org"))
                    test = imageUrls.get(i).split("\\.org");
                else if(imageUrls.get(i).contains(".net"))
                    test = imageUrls.get(i).split("\\.net");
                else if(imageUrls.get(i).contains(".io"))
                    test = imageUrls.get(i).split("\\.io");

                if(test != null)
                    Picasso.get().load(test[0] + ".com/favicon.ico").into(images.get(i));

            }


        }
    }

}
