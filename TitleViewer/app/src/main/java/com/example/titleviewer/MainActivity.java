package com.example.titleviewer;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver handleCommandReceiver;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction("service.to.activity.transfer");
        handleCommandReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent local = new Intent();
                local.setAction("activity.to.service.transfer");
                local.putExtra("title", title);
                context.sendBroadcast(local);
            }
        };
        registerReceiver(handleCommandReceiver, filter);
    }

    public void onFindClicked(View view) {
        EditText text = (EditText)findViewById(R.id.urlText);
        String urlString = text.getText().toString();

        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    title = response.substring(response.indexOf("<title>") + 7, response.indexOf("</title>"));

                } catch (IOException | RuntimeException e) {
                    title = "";
                }

                TextView titleView = findViewById(R.id.titleView);
                if (title.isEmpty()) {
                    titleView.setTextColor(RED);
                    titleView.setText("Can't get title");
                }
                else {
                    titleView.setTextColor(GREEN);
                    titleView.setText(title);
                }
            }
        });
    }

    public void onShareClicked(View view) {
        Intent mIntent = new Intent();
        mIntent.setComponent(new ComponentName("com.example.titlereceiver", "com.example.titlereceiver.MainActivity"));
        mIntent.putExtra("title", title);
        startActivity(mIntent);
    }
}