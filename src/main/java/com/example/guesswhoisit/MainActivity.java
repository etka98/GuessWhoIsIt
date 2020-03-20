package com.example.guesswhoisit;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    String[] answers = new String[4];
    int location = 0;
    int chosenCeleb = 0;
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(location))){
            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Wrong It was" + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char c = (char) data;
                    result += c;
                    data = reader.read();
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }


        }
    }

    public void newQuestion(){
       try {
           Random random = new Random();
           chosenCeleb = random.nextInt(celebUrls.size());

           ImageDownloader imageTask = new ImageDownloader();
           Bitmap celebImage = imageTask.execute(celebUrls.get(chosenCeleb)).get();

           imageView.setImageBitmap(celebImage);
           location = random.nextInt(4);
           int incorect;

           for (int i = 0; i < 4; i++) {
               if (i == location) {
                   answers[i] = celebNames.get(chosenCeleb);
               } else {
                   incorect = random.nextInt(celebUrls.size());
                   while (incorect == chosenCeleb) {
                       incorect = random.nextInt(celebUrls.size());
                   }
                   answers[i] = celebNames.get(incorect);
               }
           }
           button0.setText(answers[0]);
           button1.setText(answers[1]);
           button2.setText(answers[2]);
           button3.setText(answers[3]);
       } catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String ans = "";
        imageView = (ImageView)findViewById(R.id.imageView2);
        button0 = (Button)findViewById(R.id.button0);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);

        try{
            ans = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = ans.split("<div class=\"listedArticles\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebUrls.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while(m.find()){
                celebNames.add(m.group(1));
            }
            newQuestion();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
