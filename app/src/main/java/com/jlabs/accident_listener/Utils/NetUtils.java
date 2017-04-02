package com.jlabs.accident_listener.Utils;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import com.jlabs.accident_listener.Activities.MainActivity;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class NetUtils extends AsyncTask<String, String, String> {

    private String mUrl = "http://rsossl.net23.net";
    private String mCmd = "/?side=server";

    public NetUtils() {
        //set context variables if required
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String urlString = mUrl + mCmd + "&token=" + MainActivity.token; // URL to call
        for (int i = 0; i < params.length; i++)
        {
            urlString += params[i];
        }
        Log.d(TAG, "gagag : "+urlString);

        String resultToDisplay = "";

        InputStream in = null;
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            SystemClock.sleep(300);
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Log.d(TAG, "gagag catch: "+e.getMessage());
            return null;
        }

        Log.d(TAG, "gagag time to return: "+resultToDisplay);
        return null;
    }
}
