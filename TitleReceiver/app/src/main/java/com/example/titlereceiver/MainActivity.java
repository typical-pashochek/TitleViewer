package com.example.titlereceiver;

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

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver updateUIReciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        TextView titleView = findViewById(R.id.titleView);
        if (title != null && !title.isEmpty()) {
            titleView.setTextColor(GREEN);
            titleView.setText(title);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("activity.to.service.transfer");
        updateUIReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //UI update here
                if (intent != null) {
                    TextView titleView = findViewById(R.id.titleView);
                    String title = intent.getStringExtra("title");

                    if (title.isEmpty()) {
                        titleView.setTextColor(RED);
                        titleView.setText("Can't get title");
                    }
                    else {
                        titleView.setTextColor(GREEN);
                        titleView.setText(title);
                    }
                }
            }
        };
        registerReceiver(updateUIReciver, filter);
    }

    public void onFindClicked(View view) {
        Intent mIntent = new Intent();
        mIntent.setAction("service.to.activity.transfer");
        mIntent.putExtra("command", "getLastTitle");
        sendBroadcast(mIntent);
    }
}