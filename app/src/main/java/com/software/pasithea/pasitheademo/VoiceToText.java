package com.software.pasithea.pasitheademo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import com.software.pasithea.pasithea.Pasithea;
import com.software.pasithea.pasithea.onReadingEndListener;
import com.software.pasithea.pasithea.onWriteListener;

public class VoiceToText extends AppCompatActivity implements onWriteListener{
    private static final String TAG = "VoiceToText";

    private Pasithea mPasithea = Pasithea.getInstance();

    private static Context VoiceToTextContext;
    private static Locale VoiceToTextLocale;
    private static AppCompatActivity VoiceToTextActivity;

    private TextView voiceToText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_to_text);

        VoiceToTextContext = getApplicationContext();
        VoiceToTextLocale = getResources().getConfiguration().locale;
        VoiceToTextActivity = this;

        voiceToText = (TextView) findViewById(R.id.voicetotextView);
        voiceToText.setMovementMethod(new ScrollingMovementMethod());

        mPasithea.saySomething(getString(R.string.dictation_start), new onReadingEndListener() {
            @Override
            public void onReadingEnd() {
                mPasithea.startWriteText(getOnWriteListener());
            }
        });
    }

    private onWriteListener getOnWriteListener(){
        return new onWriteListener() {
            @Override
            public void textDetected(String s) {
                mPasithea.saySomething(getString(R.string.dictation_stop));
                voiceToText.setText(s);
                mPasithea.saySomething(s);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPasithea.stopWriteText();
                        setResult(RESULT_OK);
                        Log.d(TAG, "onReadingEnd: execute finish");
                        finish();
                    }
                },2500);
            }
        };
    }

    @Override
    public void textDetected(String s) {
        voiceToText.setText(s);
        mPasithea.stopWriteText();
        finish();
        setResult(RESULT_OK);
        Log.d(TAG, "onReadingEnd: execute finish");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: VoiceToText finished");
    }
}
