package com.orientation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class NaiveOrientationService extends Service implements SensorEventListener {
    private static final String TAG = "NaiveOrientationService";
    SensorManager sensorManager;
    NotificationManager notificationManager;
    Sensor accelerometerSensor;
    int orientationState;
    int newOrientationState;
    float threshold = 9;
    Intent intent;
    public static final String BROADCAST_ACTION = "com.orientation.broadcast";
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Start");

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationChannel channel = new NotificationChannel(
                "orientation",
                "orientation channel",
                NotificationManager.IMPORTANCE_LOW);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Notification notification =
                new Notification.Builder(this, "orientation")
                        .setContentTitle("title")
                        .setContentText("text")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setTicker("ticker text")
                        .build();

        startForeground(1337, notification);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, 0);
        orientationState = 0;
        newOrientationState = 0;

        // remove any existing callbacks to the handler
        handler.removeCallbacks(updateBroadcastData);
        // call our handler with or without delay.
        handler.post(updateBroadcastData); // 0 seconds

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if ((float) event.values[0] > threshold) {
                newOrientationState = 0;
            } else if ((float) event.values[0] < -threshold) {
                newOrientationState = 1;
            } else if ((float) event.values[1] > threshold) {
                newOrientationState = 2;
            } else if ((float) event.values[1] < -threshold) {
                newOrientationState = 3;
            } else if ((float) event.values[2] > threshold) {
                newOrientationState = 4;
            } else if ((float) event.values[2] < -threshold) {
                newOrientationState = 5;
            }

            if (newOrientationState != orientationState) {
                Log.v(TAG, "newOrientationState: " + String.valueOf(newOrientationState));
                orientationState = newOrientationState;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            // Call the method that broadcasts the data to the Activity..
            broadcastSensorValue();
            // Call "handler.postDelayed" again, after a specified delay.
            handler.postDelayed(this, 1000);
        }
    };

    private void broadcastSensorValue() {
        Log.d(TAG, String.valueOf(orientationState));
        // add orientation state to intent.
        intent.putExtra("Orientation_Int", orientationState);
        // call sendBroadcast with that intent  - which sends a message to whoever is registered to receive it.
        sendBroadcast(intent);
    }
}
