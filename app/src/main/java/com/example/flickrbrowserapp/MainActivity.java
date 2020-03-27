package com.example.flickrbrowserapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GetFlickrJsonData.OnDataAvailable {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // GetRawDataFromUrl getRawDataFromUrl = new GetRawDataFromUrl(this);
        //getRawDataFromUrl.execute("https://api.flickr.com/services/feeds/photos_public.gne?tags=android,nougat,sdk&tagmode=any&format=json&nojsoncallback=1");


        Log.d(TAG, "onCreate: Ends");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume:  starts");
        super.onResume();
        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData("https://api.flickr.com/services/feeds/photos_public.gne", "en-us", true, this);
        //getFlickrJsonData.executeOnSameThread("android, nougat");
        getFlickrJsonData.execute("android,nougat");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected: returned: returned");
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDataAvaiable(List<Photo> data, DownloadStatus status){
        if(status == DownloadStatus.OK){
            //Log.d(TAG, "onDataAvaiable:  data is " + data);
        }else {
            //Download or processing failed
            Log.e(TAG, "onDataAvaiable: Failed with status " + status );
        }
    }


}
