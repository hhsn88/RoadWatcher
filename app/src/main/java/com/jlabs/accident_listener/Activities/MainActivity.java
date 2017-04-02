package com.jlabs.accident_listener.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jlabs.accident_listener.Location.GooglePlayServicesLocationResolver;
import com.jlabs.accident_listener.Networking.MyFirebaseInstanceIdService;
import com.jlabs.accident_listener.R;
import com.jlabs.accident_listener.Utils.NetUtils;
import com.jlabs.accident_listener.Utils.PermissionsUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static String token = null;
    private String androidId = null;

    private GooglePlayServicesLocationResolver mLocation;

    // Layout
    private TextView latTV;
    private TextView lonTV;
    private TextView timeTV;
    private FloatingActionButton goFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver,
                    new IntentFilter("tokenReceiver"));

            GetServerAccessToken();

            _initLayout();
            PermissionsUtils.VerifyLocationPermissions(this);
            mLocation = new GooglePlayServicesLocationResolver(this.getApplicationContext());

            androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

            mLocation.Connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        try {
            super.onResume();
            Intent intent_o = getIntent();
            if (intent_o != null)
            {
                String lat = intent_o.getStringExtra("lat");
                String lon = intent_o.getStringExtra("lon");
                if (lat != null && lon != null)
                {
                    latTV.setText(lat);
                    lonTV.setText(lon);
                    _setTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _setTime()
    {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            timeTV.setText(dateFormat.format(date));

            // Create a DateFormatter object for displaying date in specified format.
//            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
//
//            // Create a calendar object that will convert the date and time value in milliseconds to date.
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            timeTV.setText(formatter.format(calendar.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _initLayout()
    {
        try {
            latTV = (TextView) findViewById(R.id.Latitude);
            lonTV = (TextView) findViewById(R.id.Longitude);
            timeTV = (TextView) findViewById(R.id.Time);
            goFAB  = (FloatingActionButton) findViewById(R.id.gogogoBtn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mLocation.Disconnect();
    }

    public void GetServerAccessToken()
    {
        try {
            MyFirebaseInstanceIdService op = new MyFirebaseInstanceIdService();
            op.onTokenRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            token = intent.getStringExtra("token");
            new NetUtils().execute("&devid=" + androidId);
        }
    };

    public void onGoGoGoClick(View view)
    {
        try {
            if ( _isLocationRelevant() )
            {
                String latStr, lonStr;

                latStr = latTV.getText().toString();
                lonStr = lonTV.getText().toString();

                String wazeAddress = "waze://?ll=" + latStr + ", " + lonStr + "&navigate=yes";
                Intent wazeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wazeAddress));
                if (wazeIntent.resolveActivity(getPackageManager()) != null)
                {
                    // Map app available
                    startActivity(wazeIntent);
                }
                else
                {
                    String mapsAddress = "geo:0,0?q=" + latStr + "," + lonStr + "(Accident Location)";
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsAddress));
                    if (mapIntent.resolveActivity(getPackageManager()) != null)
                    {
                        // Map app available
                        startActivity(mapIntent);
                    }
                    else
                    {
                        // Open in browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=" + Uri.encode(mapsAddress)));
                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(browserIntent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean _isLocationRelevant()
    {
        return true;//TEMP
//        try {
//            long eventTime = Long.getLong(timeTV.getText().toString());
//
//            Date cdate = new Date(System.currentTimeMillis());
//
//            return (cdate.getTime() - eventTime) * MILLI_TO_SEC < 120;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
    }
}
