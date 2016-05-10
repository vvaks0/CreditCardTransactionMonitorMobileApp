package com.hortonworks.iot.financial.customerapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MessagingClient extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String accountNumber = data.getString("accountNumber");
        String transactionId = data.getString("transactionId");
        String merchantType = data.getString("merchantType");
        String merchantId = data.getString("merchantId");
        String amount = data.getString("amount");
        String latitude = data.getString("latitude");
        String longitude = data.getString("longitude");
        Log.d(TAG, "From: " + from);
        Log.i(TAG, "AccountNumber: " + accountNumber);
        Log.i(TAG, "TransactionId: " + transactionId);
        Log.i(TAG, "MerchantType: " + merchantType);
        Log.i(TAG, "MerchantId: " + merchantId);
        Log.i(TAG, "Amount: " + amount);
        Log.d(TAG, "Latitude: " + latitude);
        Log.d(TAG, "Longitude: " + longitude);
        if (from.startsWith("/topics/")) {

        } else {

        }

        Intent incomingMessage = new Intent("message");
        incomingMessage.putExtra("accountNumber", accountNumber);
        incomingMessage.putExtra("transactionId", transactionId);
        incomingMessage.putExtra("merchantId", merchantId);
        incomingMessage.putExtra("merchantType", merchantType);
        incomingMessage.putExtra("amount", amount);
        incomingMessage.putExtra("latitude", latitude);
        incomingMessage.putExtra("longitude", longitude);
        LocalBroadcastManager.getInstance(this).sendBroadcast(incomingMessage);
        sendNotification(merchantType);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}