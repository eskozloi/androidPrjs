package com.example.sosmaker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import android.os.Vibrator;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Blinker b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button FlashlightButton=(Button)findViewById(R.id.buttonStart);
        FlashlightButton.setText("START");
        FlashlightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(FlashlightButton.getText().toString()=="START") {
                    Toast.makeText(getApplicationContext(), "SOS режим активирован", Toast.LENGTH_LONG).show();
                    FlashlightButton.setText("STOP");
                    b = new Blinker("...---...");
                    b.execute();
                }
                else{
                    b.cancel(true);
                    b = new Blinker("...---...");
                    Toast.makeText(getApplicationContext(), "SOS режим остановлен", Toast.LENGTH_LONG).show();
                    FlashlightButton.setText("START");
                }
            }
        });
    }
    public final class Blinker extends AsyncTask<Void, Void, Void> {
        private int[] pattern = {};
        private int blinkDelay = 100;
        private CameraManager camera = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        private Blinker(String code){
            for(char c : code.toCharArray()){
                if(c=='.'){
                    pattern = append(pattern, 1);
                }
                else if(c=='-'){
                    pattern = append(pattern, 3);
                }
                pattern = append(pattern, 0);
            }
            pattern = append(pattern, 0);
            pattern = append(pattern, 0);
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                for (int par : pattern) {
                    if (!isCancelled()) {
                        toggleLight(par);
                    }
                }
            }
            setFinalLightState();
            return null;
        }

        private void toggleLight(int par){
            if (par == 0) {
                vibrator.cancel();
                try {
                    String camId = camera.getCameraIdList()[0];
                    camera.setTorchMode(camId, false);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                vibrator.vibrate(getBlinkDelay()*par);
                try {
                    String camId = camera.getCameraIdList()[0];
                    camera.setTorchMode(camId, true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            try {
                if(par == 0){
                    Thread.sleep(blinkDelay);
                }
                else{
                    Thread.sleep(blinkDelay * par);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        private int getBlinkDelay(){
            return blinkDelay;
        }
        private void setFinalLightState(){
            vibrator.cancel();
            try {
                String camId = camera.getCameraIdList()[0];
                camera.setTorchMode(camId, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        private int[] append(int[] arr, int element) {
            final int N = arr.length;
            arr = Arrays.copyOf(arr, N + 1);
            arr[N] = element;
            return arr;
        }
    }
}