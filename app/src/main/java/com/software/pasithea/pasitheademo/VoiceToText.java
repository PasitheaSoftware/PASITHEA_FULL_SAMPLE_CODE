/* ====================================================================
 *
 *  Copyright (C) 2019 François Laforgia - All Rights Reserved
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL FRANCOIS LAFORGIA BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package com.software.pasithea.pasitheademo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.LoginFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import com.software.pasithea.pasithea.Pasithea;
import com.software.pasithea.pasithea.onReadingEndListener;
import com.software.pasithea.pasithea.onWriteListener;

/**
 * The speech-to-text sample code
 */
public class VoiceToText extends AppCompatActivity {
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

        Log.i(TAG, "VoiceToText: Start");

        VoiceToTextContext = getApplicationContext();
        VoiceToTextLocale = getResources().getConfiguration().locale;
        VoiceToTextActivity = this;

        voiceToText = (TextView) findViewById(R.id.voicetotextView);
        voiceToText.setMovementMethod(new ScrollingMovementMethod());

        // Says a message and trigger the speech-to-text detection.
        // Execute the listener upon detection completion
        mPasithea.saySomething(getString(R.string.dictation_start), new onReadingEndListener() {
            @Override
            public void onReadingEnd() {
                Log.i(TAG, "Voice detection: Start");
                mPasithea.startWriteText(getOnWriteListener());
            }
        });
    }

    /**
     * Create the onWriteListener to trigger an action once the speech-to-text engine returns the
     * detection
     *
     * @return an onWriteListener
     */
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
                        Log.i(TAG, "Voice detection: Done");
                        setResult(RESULT_OK);
                        finish();
                    }
                },2500);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "VoiceToText: Done");
    }
}
