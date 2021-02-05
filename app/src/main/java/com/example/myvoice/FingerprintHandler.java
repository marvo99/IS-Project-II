package com.example.myvoice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;

import androidx.core.content.ContextCompat;

import java.util.Locale;


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {



    private Context context;
    private TextToSpeech myTTS;
    private String LOG_TAG = "VoiceRecognitionActivity";

    public FingerprintHandler(Context context ){

        this.context = context;

    }

  





    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){



        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {


        this.update("There was an Auth Error. " + errString, false);

        myTTS = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale localeToUse = new Locale("en","UK");
                    myTTS.setLanguage(localeToUse);
                    myTTS.speak("Authentication Error Please try again!", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {



                context.startActivity(new Intent(context,Fingerprint.class));




            }

        }, 2000L);






    }

    @Override
    public void onAuthenticationFailed() {

        this.update("Access Denied", false);
        myTTS = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale localeToUse = new Locale("en","UK");
                    myTTS.setLanguage(localeToUse);
                    myTTS.speak("Access Denied!", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });





    }

    @Override
    public void onAuthenticationHelp(int helpCode, final CharSequence helpString) {

        this.update("Error: " + helpString, false);
        myTTS = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale localeToUse = new Locale("en","UK");
                    myTTS.setLanguage(localeToUse);
                    myTTS.speak("Error:" + helpString, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });


    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        this.update("Fingerprint Access Granted!", true);

        myTTS = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale localeToUse = new Locale("en","UK");
                    myTTS.setLanguage(localeToUse);
                    myTTS.speak("Fingerprint Access Granted!", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {



                context.startActivity(new Intent(context,MainActivity.class));




            }

        }, 2000L);




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