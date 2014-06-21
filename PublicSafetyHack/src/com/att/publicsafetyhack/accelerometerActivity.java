package com.att.publicsafetyhack;

import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.view.View;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.app.Activity;

public class accelerometerActivity
    extends Activity
    implements SensorEventListener
{

    private SensorManager manager;

    private Long          lastUpdate;
    private ImageView     image1;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.missioncontrolscreen);

        //image1 = (ImageView)findViewById(R.id.image1);


        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
    }


    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        // nah
    }


    public void onSensorChanged(SensorEvent change)
    {
        if (change.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            getAccelerometerValue(change);
            //somehow pass data to database
        }

    }


    public void getAccelerometerValue(SensorEvent sensorEvent)
    {
        float[] accelerometerValue = sensorEvent.values;

        float x = accelerometerValue[0];
        float y = accelerometerValue[1];
        float z = accelerometerValue[2];

        float accelerationSquareRoot =
            (((x * x) + (y * y) + (z * z)) / (manager.GRAVITY_EARTH * manager.GRAVITY_EARTH));

        float acceleration = (float)Math.sqrt(accelerationSquareRoot);

        long actualTime = sensorEvent.timestamp;

        if (acceleration > 1)
        {
            if ((actualTime - lastUpdate) < 200)
            {
                return;
            }
            lastUpdate = actualTime;

            image1.setImageResource(R.drawable.yellow);
            if (acceleration > 2)
            {
                image1.setImageResource(R.drawable.green);
            }
        }
        else
        {
            image1.setImageResource(R.drawable.red);
        }
    }

    protected void onResume()
    {
        super.onResume();
        manager.registerListener(
            this,
            manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            manager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause()
    {
        super.onPause();
        manager.unregisterListener(this);
    }

}
