package com.orientation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "NaiveOrientationActivity";
    private Intent intent;
    private int detectedOrientation;
    ConstraintLayout mScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScreen = (ConstraintLayout) findViewById(R.id.myScreen);

        //  Instantiate the intent declared globally - which will be passed to startService and stopService.
        intent = new Intent(this, NaiveOrientationService.class);

        // start Service.
        startForegroundService(new Intent(getBaseContext(), NaiveOrientationService.class));
        // register our BroadcastReceiver by passing in an IntentFilter. * identifying the message that is broadcasted by using static string "BROADCAST_ACTION".
        registerReceiver(broadcastReceiver, new IntentFilter(NaiveOrientationService.BROADCAST_ACTION));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // call updateUI passing in our intent which is holding the data to display.
            updateViews(intent);
        }
    };

    private void updateViews(Intent intent) {
        detectedOrientation = intent.getIntExtra("Orientation_Int", 0);

        int color = 0Xff0000ff;
        switch (detectedOrientation) {
            case 0:
                color = 0Xffff0000;
                break;
            case 1:
                color = 0Xff40e0d0;
                break;
            case 2:
                color = 0Xffa83290;
                break;
            case 3:
                color = 0xff00ff00;
                break;
            case 4:
                color = 0Xff0000ff;
                break;
            case 5:
                color = 0xffffff00;
                break;
            default:
                color = 0Xff0000ff;
        }

        mScreen.setBackgroundColor(color);
    }
}
