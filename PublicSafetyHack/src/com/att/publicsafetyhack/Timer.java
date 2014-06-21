package com.att.publicsafetyhack

import android.widget.ToggleButton;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Timer
    extends Activity
{
    public class TimerDemo
        extends Activity
    {
        /** Called when the activity is first created. */
        private TextView textTimer;
        private Button   startButton;
        private Button   pauseButton;
        private long     startTime     = 0L;
        private Handler  myHandler     = new Handler();
        long             timeInMillies = 0L;
        long             timeSwap      = 0L;
        long             finalTime     = 0L;


        public void onToggleClicked(View view) {
            boolean on = ((ToggleButton)view).isChecked();
            if (on)
            {
                //
            }
            else
            {

            }
        }


        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.missioncontrolscreen);

            textTimer = (TextView)findViewById(R.id.textTimer);



        }

        private Runnable updateTimerMethod = new Runnable() {

                                               public void run()
                                               {
                                                   timeInMillies =
                                                       SystemClock
                                                           .uptimeMillis()
                                                           - startTime;
                                                   finalTime =
                                                       timeSwap + timeInMillies;
                                                   int seconds =
                                                       (int)(finalTime / 1000);
                                                   int minutes = seconds / 60;
                                                   seconds = seconds % 60;
                                                   int milliseconds =
                                                       (int)(finalTime % 1000);
                                                   textTimer.setText(""
                                                       + minutes
                                                       + ":"
                                                       + String.format(
                                                           "%02d",
                                                           seconds)
                                                       + ":"
                                                       + String.format(
                                                           "%03d",
                                                           milliseconds));
                                                   myHandler.postDelayed(
                                                       this,
                                                       0);
                                               }

                                           };
    }

}
