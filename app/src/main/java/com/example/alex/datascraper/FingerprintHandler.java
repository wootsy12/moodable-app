package com.example.alex.datascraper;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by Bella on 2/18/18.
 */

public class FingerprintHandler extends FingerprintManager .AuthenticationCallback {

    //use cancellationsSignal method whenever can nolonger process user input
    private CancellationSignal cancellationSignal;
    private Context context;

    public FingerprintHandler(Context mContext){
        context = mContext;
    }
    //implement startauth method, responsible for starting authentication process
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject){
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }
    @Override
    public void onAuthenticationError( int errMsgId, CharSequence errString){
        //Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
        Intent i = new Intent(context, RecordActivity.class);
        context.startActivity(i);

    }
}
