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

public class MainActivity extends AppCompatActivity implements GetRawDataFromUrl.OnDownloadComplete {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GetRawDataFromUrl getRawDataFromUrl = new GetRawDataFromUrl(this);
        getRawDataFromUrl.execute("https://api.flickr.com/services/feeds/photos_public.gne?tags=android,nougat,sdk&tagmode=any&format=json&nojsoncallback=1");


        Log.d(TAG, "onCreate: Ends");
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
    public void onDownloadComplete(String data, DownloadStatus status){
        if(status == DownloadStatus.OK){
            Log.d(TAG, "onDownloadComplete:  data is " + data);
        }else {
            //Download or processing failed
            Log.e(TAG, "onDownloadComplete: Failed with status " + status );
        }
    }
}
