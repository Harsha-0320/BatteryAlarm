package com.example.myapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    BroadcastReceiver batteryBroadcast;
    IntentFilter intentFilter;

    TextView level,health,voltage,batteryType,chargingSource,temperature,chargingStatus,title;

    ImageView imageView;

    TextView battlevel;

    private SeekBar batteryLevelSeekBar;
    private TextView progressValueTextView;

    private MediaPlayer mediaPlayer;

    private Vibrator vibrator;
    private boolean isVibrating = false;

    private BroadcastReceiver batteryReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        level=findViewById(R.id.txtLevel);
        health=findViewById(R.id.txtHealth);
        voltage=findViewById(R.id.txtVoltage);
        batteryType=findViewById(R.id.txtType);
        chargingSource=findViewById(R.id.txtChargingSource);
        temperature=findViewById(R.id.txtTemperature);
        chargingStatus=findViewById(R.id.txtChargingStatus);
        title=findViewById(R.id.txtTitle);
        //stopButton=findViewById(R.id.stopButton);

        imageView=findViewById(R.id.imageView);

        intentFilterAndBroadcast();

        battlevel=findViewById(R.id.txtLevel);

        batteryLevelSeekBar = findViewById(R.id.batteryLevelSeekBar);

        progressValueTextView = findViewById(R.id.progressValueTextView);
        imageView=findViewById(R.id.imageView);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.clock_alarm_extended_copy);

        batteryReceiver = new BatteryReceiver();
        registerBatteryReceiver();

        batteryLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateProgressText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }
        });

    }

    private void registerBatteryReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int batteryLevel = (int) ((level / (float) scale) * 100);

            battlevel.setText(String.valueOf(intent.getIntExtra("level", 0)) + "%");

            int selectedBatteryLevel = batteryLevelSeekBar.getProgress();           //battery level taken as input from seekbar

            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL;

            //Only if Charging is true the alarm would start
            if (isCharging) {
                // Get the remaining time to reach 100% charge
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

                if (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged==BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                    // The device is charging via AC or USB
                    if (batteryLevel > selectedBatteryLevel) {

                        playAlarm();
                        startVibration();
                        showHighBatteryAlert();
                    }
                }
            }
            if(batteryLevel < 15){
                playAlarm();
                startVibration();
                showLowBatteryAlert();
            }
        }
    }

    /*public void stopButton(View view){
        //stopAlarm();
        mediaPlayer.stop();
        stopVibration();
        Toast.makeText(getApplicationContext(),"Alarm Stopped",Toast.LENGTH_SHORT).show();
    }*/

    //method for showing high battery level alert box
    public void showHighBatteryAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("High Battery Alert")
                .setMessage("Your battery level has reached the limit. Please unplug the charger.")
                .setPositiveButton("STOP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //mediaPlayer.stop();
                        stopAlarm();
                        stopVibration();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Alarm Stopped",Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    //method for showing low level battery alarm
    public void showLowBatteryAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Low Battery Alert")                                                //Title of alert box
                .setMessage("Your battery level is less than 15. Please plug the charger.")  //Message on the alert box
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //mediaPlayer.stop();
                        stopAlarm();
                        dialog.dismiss();
                        stopVibration();
                        Toast.makeText(getApplicationContext(),"Alarm Stopped",Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }


    //method to start alarm media player
    private void playAlarm() {
        // You can replace the sound file with your own alarm sound
        //mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }

    //method after clicking stop button on alert box to stop alarm
    private void stopAlarm() {
        // Stop the MediaPlayer
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
        }
        // You can also finish the activity if you want
        //finish();
    }


    private void updateProgressText(int progress) {
        String progressText = "Selected Battery Level for High Battery Alarm: " + progress + "%";
        progressValueTextView.setText(progressText);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }

    private void startVibration() {
        // Check if the vibrator is not already running
        if (!isVibrating) {
            // Start continuous vibration
            isVibrating = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isVibrating) {
                        // Vibrate for 500 milliseconds, pause for 500 milliseconds (adjust as needed)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                    VibrationEffect.createWaveform(
                                            new long[]{500, 500},
                                            0
                                    )
                            );
                        } else {
                            // Deprecated in API 26
                            vibrator.vibrate(new long[]{500, 500}, 0);
                        }

                        try {
                            // Adjust the sleep duration as needed
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }


    private void stopVibration() {
        // Stop continuous vibration
        isVibrating = false;
        vibrator.cancel();
    }

    private void intentFilterAndBroadcast() {
        intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryBroadcast=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                    level.setText(String.valueOf(intent.getIntExtra("level",0))+"%");
                    //int le=Integer.parseInt(String.valueOf(intent.getIntExtra("level",0)));

                    if((0<intent.getIntExtra("level",0)) && (intent.getIntExtra("level",0)<=10)){
                        imageView.setImageResource(R.drawable.baseline_battery_1_bar_24);
                    }
                    else if((10<intent.getIntExtra("level",0)) && (intent.getIntExtra("level",0)<=20)){
                        imageView.setImageResource(R.drawable.baseline_battery_2_bar_24);
                    }
                    else if ((20<intent.getIntExtra("level",0)) && (intent.getIntExtra("level",0)<=35)) {
                        imageView.setImageResource(R.drawable.baseline_battery_3_bar_24);
                    }
                    else if ((35<intent.getIntExtra("level",0)) && (intent.getIntExtra("level",0)<=50)) {
                        imageView.setImageResource(R.drawable.baseline_battery_4_bar_24);
                    }
                    else if ((50<intent.getIntExtra("level",0)) && (intent.getIntExtra("level",0)<=75)) {
                        imageView.setImageResource(R.drawable.baseline_battery_5_bar_24);
                    }
                    else if ((75<intent.getIntExtra("level",0)) && (intent.getIntExtra("level",0)<90)) {
                        imageView.setImageResource(R.drawable.baseline_battery_6_bar_24);
                    }
                    else if ((90<=intent.getIntExtra("level",0)) && (intent.getIntExtra("level",0)<=100)) {
                        imageView.setImageResource(R.drawable.baseline_battery_full_24);
                    }

                    float voltTemp=(float) (intent.getIntExtra("voltage",0)*0.001);

                    voltage.setText(voltTemp+"v");

                    setHealth(intent);

                    batteryType.setText(intent.getStringExtra("technology"));

                    setChargingSource(intent);

                    float temp=(float) intent.getIntExtra("temperature",-1)/10;

                    temperature.setText(temp+"°");

                    setChargingStatus(intent);
                }
            }
        };
    }

    private void setChargingStatus(Intent intent) {
        int status=intent.getIntExtra("status",-1);
        switch (status){
            case BatteryManager.BATTERY_STATUS_CHARGING:
                chargingStatus.setText("Charging");
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                chargingStatus.setText("Discharging");
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                chargingStatus.setText("Full");
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                chargingStatus.setText("Not charging");
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                chargingStatus.setText("Unknown");
                break;
            default:
                chargingStatus.setText("Null");
        }
    }

    private void setChargingSource(Intent intent) {
        int source=intent.getIntExtra("plugged",-1);
        switch (source){
            case BatteryManager.BATTERY_PLUGGED_AC:
                chargingSource.setText("AC");
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                chargingSource.setText("USB");
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                chargingSource.setText("Wireless");
                break;
            case BatteryManager.BATTERY_PLUGGED_DOCK:
                chargingSource.setText("Charging Dock");
                break;
            default:
                chargingSource.setText("Null");
        }
    }

    private void setHealth(Intent intent) {
        int val=intent.getIntExtra("health",0);
        switch (val){
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                health.setText("Unknown");
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                health.setText("Good");
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                health.setText("Overheat");
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                health.setText("Over voltage");
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                health.setText("Unspecified failure");
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                health.setText("Cold");
                break;
        }
    }

    protected void onStart() {
        super.onStart();
        registerReceiver(batteryBroadcast,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryBroadcast);
    }
    }

