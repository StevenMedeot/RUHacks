package com.mash.recipenator;

import androidx.appcompat.app.AppCompatActivity;

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

    /*
    TextView textView;
        String content="<h1>Recipe from allrecipes.com </h1>\n" +
                " <h2> Ingredients </h2> " +
                " <ingredients>\n" +
                " <li>One</li>\n" +
                " <li>Two</li>\n" +
                " </ingredients>\n " +
                " <h2> Directions </h2> " +
                " <directions>\n" +
                " <li> 1. Put oil in pan</li> " +
                " <li> 2. Put beef in pan and cook until no longer pink </li> " +
                "</directions>\n ";
*/

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        text=(TextView)findViewById(R.id.ingredients);

        Button button=(Button) findViewById(R.id.getBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoSomething().execute();
            }
        });


        /*
        textView = (TextView) findViewById(R.id.simpleTextView);
        //set Text in TextView using fromHtml() method with version check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        } else
            textView.setText(Html.fromHtml(content));
         */

    }

    public class DoSomething extends AsyncTask<Void,Void,Void>{
        String words;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String encoding = "UTF-8";
                String var = "";
                String searchText = "Potatoes Carrots Steak";
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
                    var += fullUrl.substring(7,fullUrl.indexOf("/&"));
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
