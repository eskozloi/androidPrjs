package com.example.morze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText UserInputText;
    private  TextView MorseText;
    Map<Character, String> convertor = new HashMap<Character, String>();
    private String MorseCode;
    private Blinker b;
    private Integer mode;
    private Integer speed;

    //TextView MorzeTxt = (TextView)findViewById(R.id.MorzeText);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Nums
        convertor.put('1',".----");
        convertor.put('2',"..---");
        convertor.put('3',"...--");
        convertor.put('4',"....-");
        convertor.put('5',".....");
        convertor.put('6',"-....");
        convertor.put('7',"--...");
        convertor.put('8',"---..");
        convertor.put('9',"----.");
        convertor.put('0',"-----");

        //Words
        convertor.put('A',".-");
        convertor.put('B',"-...");
        convertor.put('C',"-.-.");
        convertor.put('D',"-..");
        convertor.put('E',".");
        convertor.put('F',"..-.");
        convertor.put('G',"--.");
        convertor.put('H',"....");
        convertor.put('I',"..");
        convertor.put('J',".---");
        convertor.put('K',"-.-");
        convertor.put('L',".-..");
        convertor.put('M',"--");
        convertor.put('N',"-.");
        convertor.put('O',"---");
        convertor.put('P',".--.");
        convertor.put('Q',"--.-");
        convertor.put('R',".-.");
        convertor.put('S',"...");
        convertor.put('T',"-");
        convertor.put('U',"..-");
        convertor.put('V',"...-");
        convertor.put('W',".--");
        convertor.put('X',"-..-");
        convertor.put('Y',"-.--");
        convertor.put('Z',"--..");

        //Space
        convertor.put(' ',"   ");

        RadioGroup radioGroup1=(RadioGroup)findViewById(R.id.rgS);
        RadioGroup radioGroup2=(RadioGroup)findViewById(R.id.rgD);

        Button StartBtn = (Button)findViewById(R.id.start);
        Button ClearBtn = (Button)findViewById(R.id.clear);

        MorseText = (TextView)findViewById(R.id.MorzeText);
        UserInputText = (EditText) findViewById(R.id.UserInput);
        UserInputText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(UserInputText.getText().toString().length()==0){
                    MorseText.setText("Код Морзе");
                }
                else{
                    String text = UserInputText.getText().toString();
                    String morseText = "";
                    Boolean correct = true;
                    Character tmpSymb = ' ';
                    for(int i=0; i<text.length(); i++){
                        if(convertor.get(text.charAt(i))!=null){
                            if(tmpSymb==' ' && tmpSymb==text.charAt(i)){}
                            else{
                                morseText += convertor.get(text.charAt(i));
                                tmpSymb = text.charAt(i);
                            }
                        }
                        else{
                            correct=false;
                            break;
                        }
                    }
                    if(correct){
                        MorseText.setText(morseText);
                        MorseCode = morseText;
                        StartBtn.setEnabled(true);
                    }
                    else{
                        MorseText.setText("Неизвестные символы");
                        StartBtn.setEnabled(false);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        ClearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                UserInputText.setText("");
            }
        });

        StartBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(StartBtn.getText().toString().equals("СТАРТ")) {
                    if(UserInputText.getText().toString().length()!=0) {
                        int selectedId1 = radioGroup1.getCheckedRadioButtonId();
                        if(selectedId1==2131231018){ mode=1; }
                        if(selectedId1==2131231019){ mode=2; }
                        if(selectedId1==2131231020){ mode=3; }
                        int selectedId2 = radioGroup2.getCheckedRadioButtonId();
                        if(selectedId2==2131231015){ speed=3; }
                        if(selectedId2==2131231016){ speed=2; }
                        if(selectedId2==2131231017){ speed=1; }
                        //Log.i("kot", String.valueOf(selectedId2));

                        Toast.makeText(getApplicationContext(), "SOS режим активирован", Toast.LENGTH_LONG).show();
                        StartBtn.setText("СТОП");
                        b = new Blinker(MorseCode);
                        b.execute();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Введите текст!", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    b.cancel(true);
                    //b = new Blinker(MorseCode);
                    Toast.makeText(getApplicationContext(), "SOS режим остановлен", Toast.LENGTH_LONG).show();
                    StartBtn.setText("СТАРТ");
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
                    pattern = append(pattern, 1*speed);
                }
                else if(c=='-'){
                    pattern = append(pattern, 3*speed);
                }
                else if(c==' '){
                    pattern = append(pattern, 0);
                }
                pattern = append(pattern, 0);
            }
            for(int i = 0; i<15; i++){pattern = append(pattern, 0);}
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
                if(mode==3 || mode==2){vibrator.cancel();}
                try {
                    if(mode==3 || mode==1){String camId = camera.getCameraIdList()[0];
                    camera.setTorchMode(camId, false);}
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                if(mode==3 || mode==2){vibrator.vibrate(getBlinkDelay()*par);}
                try {
                    if(mode==3 || mode==1){String camId = camera.getCameraIdList()[0];
                    camera.setTorchMode(camId, true);}
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            try {
                if(par == 0){
                    Thread.sleep(blinkDelay*speed);
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