package com.example.roader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.VideoView;

import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISION_REQUEST = 0;
    private static final int RESULT_LOAD_VIDEO = 1;
    private static final int RESULT_LOAD_IMAGE = 2;

    Button upldv;
    Button upldi;
    VideoView vv;
    ImageView iv;

    MediaController mediaController;

    PowerSpinnerView spinner;
    Button sm;
    ScrollView form;
    CheckBox chck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vv = (VideoView) findViewById(R.id.videoView);
        upldv = (Button) findViewById(R.id.uploadBtn1);

        mediaController = new MediaController(this);
        mediaController.setAnchorView(vv);

        iv = (ImageView) findViewById(R.id.imageView);
        upldi = (Button) findViewById(R.id.uploadBtn2);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISION_REQUEST);
        }

        upldv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_VIDEO);
            }
        });

        upldi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_VIDEO:
                if (resultCode == RESULT_OK) {
                    Uri selectedVideo = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedVideo, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String videoPath = cursor.getString(columnIndex);
                    Uri uri = Uri.parse(videoPath);
                    cursor.close();
                    vv.setMediaController(mediaController);
                    vv.setVideoURI(uri);
                    vv.requestFocus();
                    vv.start();
                    vv.setVisibility(View.VISIBLE);
                    showContent();
                }
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();
                    iv.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    iv.setVisibility(View.VISIBLE);
                    showContent();
                }
        }
    }

    private void showContent() {
        upldv.setVisibility(View.GONE);
        upldi.setVisibility(View.GONE);

        spinner = (PowerSpinnerView) findViewById(R.id.spinnerView);
        spinner.setBackgroundResource(R.drawable.spnrstyle);
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                spinner.setBackgroundResource(R.drawable.spnrstyle2);
            }
        });

        sm = (Button) findViewById(R.id.sendBtn);
        sm.setVisibility(View.VISIBLE);
        sm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //something
            }
        });

        form = (ScrollView) findViewById(R.id.form);
        form.setVisibility(View.VISIBLE);

        chck = (CheckBox) findViewById(R.id.chck);
        chck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    sm.setBackgroundResource(R.drawable.btnsndstyle2);
                    sm.setTextColor(Color.parseColor("#0FFF00"));
                }
                else{
                    sm.setBackgroundResource(R.drawable.btnsndstyle);
                    sm.setTextColor(Color.parseColor("#A9A9A9"));
                }

            }
        });
    }
}