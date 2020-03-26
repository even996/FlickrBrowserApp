package com.example.flickrbrowserapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus{IDLE, PROCESSING, NOT_INiTIALISED, FAILED_OR_EMPTY, OK}


class GetRawDataFromUrl extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawDataFromUrl";

    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete mCallback;

    interface OnDownloadComplete{
        void onDownloadComplete(String data, DownloadStatus status);

    }


    public GetRawDataFromUrl(OnDownloadComplete mCallback) {
        this.mDownloadStatus= DownloadStatus.IDLE;
        this.mCallback = mCallback;
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute:  parameter = " + s);
        if(mCallback != null){
            mCallback.onDownloadComplete(s, mDownloadStatus);
        }
        System.out.println("texst");
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if(strings == null){
            mDownloadStatus= DownloadStatus.NOT_INiTIALISED;
            return null;
        }

        try{
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: The response code was" + response);
            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine())!= null){
                result.append(line).append("\n");
            }
            mDownloadStatus = DownloadStatus.OK;
            return result.toString();

        }catch (MalformedURLException e){
            Log.e(TAG, "doInBackground: Invalid URL" + e.getMessage() );
        }catch (IOException e){
            Log.e(TAG, "doInBackground: IO Exception --> cant read the data" + e.getMessage() );
        }catch (SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception, --> Needs permission" + e.getMessage());
        } finally {
            if(connection != null){
                connection.disconnect();
            }
            if(reader != null){
                try{
                    reader.close();
                }catch (IOException e){
                    Log.e(TAG, "doInBackground: Error closing streams" + e.getMessage());
                }
            }
        }
        mDownloadStatus=DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }
}
