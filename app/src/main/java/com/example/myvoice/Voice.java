package com.example.myvoice;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class Voice implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean isReady;

    public Voice(Context context) {
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.UK);

            isReady = true;
        }
    }

    public void speak(String text) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null); //use of QUEUE_ADD to ensure all items are spoken
        } else {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    //checks whether the speaker is speaking
    public boolean isSpeaking() {
        return tts.isSpeaking();
    }

    //begin speaking only when speaker is ready; this will avoid silent messages
    public boolean isSpeakerReady() {
        return isReady;
    }
}
