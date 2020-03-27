package com.example.flickrbrowserapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData extends AsyncTask<String, Void, List<Photo>> implements GetRawDataFromUrl.OnDownloadComplete {

    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList;
    private String mBaseUrl;
    private String mLanguage;
    private boolean mMatchAll;

    private final OnDataAvailable mCallback;
    private boolean runningOnSameThread = false;

    interface  OnDataAvailable{
        void onDataAvaiable(List<Photo> data, DownloadStatus status);
    }

    public GetFlickrJsonData(String mBaseUrl, String mLanguage, boolean mMatchAll, OnDataAvailable mCallback) {
        Log.d(TAG, "GetFlickrJsonData: called");
        this.mBaseUrl = mBaseUrl;
        this.mLanguage = mLanguage;
        this.mMatchAll = mMatchAll;
        this.mCallback = mCallback;
    }

    void executeOnSameThread(String searchCriteria){
        Log.d(TAG, "executeOnSameThread:  starts");
        runningOnSameThread = true;
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);
        GetRawDataFromUrl getRawDataFromUri = new GetRawDataFromUrl(this);
        getRawDataFromUri.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread:  ends");

    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute:  starts");
        if(mCallback != null){
            mCallback.onDataAvaiable(mPhotoList, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground:  starts");
        String destinationUri = createUri(params[0], mLanguage, mMatchAll);
        GetRawDataFromUrl getRawDataFromUrl = new GetRawDataFromUrl(this);
        // den vil ikke lage flerer tråder i samme tråd så må bruke en metode
        getRawDataFromUrl.runningSameThread(destinationUri);
        Log.d(TAG, "doInBackground:  ends");

        return mPhotoList;
    }

    private String createUri(String searchCriteria, String lang, boolean matchAll){
        Log.d(TAG, "createUri: starts");


        return Uri.parse(mBaseUrl).buildUpon().appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagemode", matchAll ? "ALL" : "ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: starts; Status = " + status);

        if(status == DownloadStatus.OK){
            mPhotoList = new ArrayList<>();

            try{
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i <jsonArray.length(); i++) {
                    JSONObject jsonPhoto = jsonArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete " + photoObject.toString());

                }
            } catch (JSONException jsone){
                jsone.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Error processing Json Data " + jsone.getMessage() );
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }
        if(runningOnSameThread && mCallback != null){
            // inform the caller that processing is done - possibly returning null if there was an error
            mCallback.onDataAvaiable(mPhotoList, status);
        }

        Log.d(TAG, "onDownloadComplete:  ends ");
    }
}
