package com.jlabs.accident_listener.Location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.jlabs.accident_listener.Utils.NetUtils;
import com.jlabs.accident_listener.Utils.SettingsUtils;

/**
 * Created by hhsn8 on 3/5/2017.
 */

public class GooglePlayServicesLocationResolver implements GoogleApiClient.ConnectionCallbacks,
                                                           GoogleApiClient.OnConnectionFailedListener,
                                                           LocationListener
{
    private final String TAG = getClass().getSimpleName();
    private int  mLocationUpdateInterval = 60000;     // [ms]
    private int  mLocationUpdateFastInterval = 60000; // [ms]

    private Context mMyContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public GooglePlayServicesLocationResolver(Context pContext)
    {
        mMyContext = pContext;
    }

    public void Connect()
    {
        buildGoogleApiClient();
        ConnectLocationService();
    }
    public void Disconnect()
    {
        DisonnectLocationService();
    }

    private synchronized void buildGoogleApiClient()
    {
        try {
            Log.i(TAG, "Building GoogleApiClient");
            mGoogleApiClient = new GoogleApiClient.Builder(mMyContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(ActivityRecognition.API)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void ConnectLocationService()
    {
        try {
            if ( !mGoogleApiClient.isConnected() ) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DisonnectLocationService()
    {
        if ( mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean GetApiConnectionStatus()
    {
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        try {
            // Handle Location API
            mLocationRequest = _createLocationRequest(mLocationUpdateInterval, mLocationUpdateFastInterval);
            // Verify Location settings
            SettingsUtils.VerifyLocationSettings(mLocationRequest, mMyContext, mGoogleApiClient);

            StartLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void StartLocationUpdates()
    {//TODO: start this when driving activity is recognised (need to use PendingIntent?)
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest,
                    this);
        }
        catch (SecurityException ex)
        {   // This shouldn't happen since this method is called only after verifying we've got permissions
            Log.wtf(TAG, ex.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.e(TAG, "connection to GoogleApiClient failed. Error: " + connectionResult.getErrorMessage());
    }

    private LocationRequest _createLocationRequest(int pInterval, int pFastInterval)
    {
        try {
            return new LocationRequest()
                    .setInterval(pInterval)
                    .setFastestInterval(pFastInterval)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Location mLastLocation;
    @Override
    public void onLocationChanged(Location pLocation)
    {
        try
        {
            mLastLocation = pLocation;

            if (mLastLocation.getAccuracy() < 100)
            {
                // Update Server
                new NetUtils().execute("&lat=" + Double.toString(mLastLocation.getLatitude()),
                        "&lon=" + Double.toString(mLastLocation.getLongitude()),
                        "&ts="  + Long.toString(mLastLocation.getTime()));
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
    }
}
