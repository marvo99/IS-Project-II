package com.example.myvoice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;

import androidx.core.content.ContextCompat;


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {



    private Context context;
    private TextToSpeech tts;
    private String LOG_TAG = "VoiceRecognitionActivity";

    public FingerprintHandler(Context context){

        this.context = context;


    }

    //public void onInit(int status) {
    //    tts.setSpeechRate(speechRate);
    //    tts.setPitch(pitch);
    //    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    //}



    //public void speak(String text){

       //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           // tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        //}else{
          //  tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        //}
    //}

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {


        this.update("There was an Auth Error. " + errString, false);
        //tts.speak('There was an Authentication Error');





    }

    @Override
    public void onAuthenticationFailed() {

        this.update("Auth Failed. ", false);
        //speak("Authentication Failed");




    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error: " + helpString, false);

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        this.update("You can now access the app.", true);
        //speak("you can now access the application");


        context.startActivity(new Intent(context,MainActivity.class));




    }



    private void update(String s, boolean b) {

        TextView paraLabel = (TextView) ((Activity)context).findViewById(R.id.paraLabel);
        ImageView imageView = (ImageView) ((Activity)context).findViewById(R.id.fingerprintImage);

        paraLabel.setText(s);

        if(b == false){

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        } else {

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            imageView.setImageResource(R.mipmap.action_done);

        }

    }
}