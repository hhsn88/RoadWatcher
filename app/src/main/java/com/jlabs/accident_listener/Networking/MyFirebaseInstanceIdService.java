package com.jlabs.accident_listener.Networking;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by wafihsn on 31/03/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "gagag " + refreshedToken);
        try {
            final Intent intent = new Intent("tokenReceiver");
            // You can also include some extra data.
            final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
            intent.putExtra("token",refreshedToken);
            broadcastManager.sendBroadcast(intent);
        }
        catch(Exception ex) {
            Log.d(TAG, "gagag " + ex.getMessage());
        }
    }
}
