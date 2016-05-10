package com.hortonworks.iot.financial.customerapp;

import android.app.Fragment;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private AWSCredentials credentials = new BasicAWSCredentials( Constants.awsAccessKeyId, Constants.awsSecretKey );
    private AmazonSQSClient sqsClient = new AmazonSQSClient( credentials );
    private GetQueueUrlResult queueURLResult;
    private String customerValidationQueueURL = Constants.customerValidationQueueURL;
    private GoogleMap mMap;
    private boolean isReceiverRegistered;

    private BroadcastReceiver mRegistrationBroadcastReceiver  = new BroadcastReceiver() {
        public void onReceive(Context context, Intent incomingMessage) {
            Log.i(TAG, "AccountNumber: " + incomingMessage.getStringExtra("accountNumber"));
            Log.i(TAG, "TransactionId: " + incomingMessage.getStringExtra("transactionId"));
            Log.i(TAG, "MerchantType: " + incomingMessage.getStringExtra("merchantType"));
            Log.i(TAG, "MerchantId: " + incomingMessage.getStringExtra("merchantId"));
            Log.i(TAG, "Amount: " + incomingMessage.getStringExtra("amount"));
            Log.i(TAG, "Latitude: " + incomingMessage.getStringExtra("latitude"));
            Log.i(TAG, "Longitude: " + incomingMessage.getStringExtra("longitude"));

            updateMap(incomingMessage);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //queueURLResult = sqsClient.getQueueUrl("customerValidation");
        //customerValidationQueueURL = queueURLResult.getQueueUrl();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerReceiver();
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        final Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String response = ((TextView)findViewById(R.id.accountNumber)).getText().toString().replaceAll("Account Number: ","") +"," +
                        ((TextView)findViewById(R.id.transactionId)).getText().toString().replaceAll("Transaction Id: ","") + ",false,customer_validation";
                sqsClient.sendMessage(customerValidationQueueURL, response);
            }
        });

        final Button denyButton = (Button) findViewById(R.id.deny);
        denyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String response = ((TextView)findViewById(R.id.accountNumber)).getText().toString().replaceAll("Account Number: ", "") +"," +
                        ((TextView)findViewById(R.id.transactionId)).getText().toString().replaceAll("Transaction Id: ", "") + ",true,customer_validation";
                sqsClient.sendMessage(customerValidationQueueURL, response);
            }
        });
    }

    public void updateMap(Intent incomingMessage){
        String accountNumber = incomingMessage.getStringExtra("accountNumber");
        String transactionId = incomingMessage.getStringExtra("transactionId");
        String merchantType = incomingMessage.getStringExtra("merchantType");
        String merchantId = incomingMessage.getStringExtra("merchantId");
        String amount = incomingMessage.getStringExtra("amount");
        Double latitude = Double.valueOf(incomingMessage.getStringExtra("latitude"));
        Double longitude = Double.valueOf(incomingMessage.getStringExtra("longitude"));

        ((TextView)findViewById(R.id.accountNumber)).setText("Account Number: " + accountNumber);
        ((TextView)findViewById(R.id.transactionId)).setText("Transaction Id: " + transactionId);
        ((TextView)findViewById(R.id.merchantType)).setText("Merchant Type: " + merchantType);
        //((TextView)findViewById(R.id.merchantId)).setText(merchantId);
        ((TextView)findViewById(R.id.amount)).setText("Transaction Amount: $" + amount);

        String markerDetails = "Merchant Type: " + merchantType + System.getProperty ("line.separator") + "Amount: $" + amount;
        LatLng transactionLocation = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions().position(transactionLocation).title(markerDetails);
        CameraUpdate cameraPostion = CameraUpdateFactory.newLatLngZoom(transactionLocation, 15);

        mMap.addMarker(markerOptions);
        mMap.moveCamera(cameraPostion);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter("message"));
            isReceiverRegistered = true;
            Log.i(TAG, "Broadcast Receiver Registered ");
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
