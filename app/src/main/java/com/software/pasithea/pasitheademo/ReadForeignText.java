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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import com.software.pasithea.pasithea.Pasithea;
import com.software.pasithea.pasithea.onReadingEndListener;

/**
 * Read a text in a foreign language.</br>
 * This activity is optional and it has been created for this sample code only.
 */

public class ReadForeignText extends AppCompatActivity implements onReadingEndListener {
    private static final String TAG = "ReadForeignText";

    private Pasithea mPasithea = Pasithea.getInstance();

    private static Locale ReadLocale;

    TextView readTextView = null;

    String file;
    String foreigntext;
    final String[] foreignsentences = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_text);

        Log.i(TAG, "Read foreign language: Start ");

        ReadLocale = getResources().getConfiguration().locale;

        readTextView = (TextView) findViewById(R.id.readtextView);
        readTextView.setMovementMethod(new ScrollingMovementMethod());

        // Set the text file based on the system Locale
        if (getReadLocale().getLanguage().equals("fr")){
            file = "text_en";
        } else if (getReadLocale().getLanguage().equals("en")){
            file = "text_fr";
        } else {
            mPasithea.saySomething(getString(R.string.locale_error));
            setResult(RESULT_OK);
            finish();
        }
        Log.i(TAG, "Text detected: " + file);

        // Prepare the text to read
        foreigntext = PrepareText.getText(getApplicationContext(),file);
        foreignsentences[0] = foreigntext;

        // Set the text to read and starts the reading
        readTextView.setText(foreigntext);
        Log.i(TAG, "Text reading: Start");
        mPasithea.startReadingText(foreignsentences, this);
    }

    public static Locale getReadLocale() {
        return ReadLocale;
    }

    /**
     * Listener to trigger the action after the reading
     */

    @Override
    public void onReadingEnd() {
        mPasithea.pauseReading();
        setResult(RESULT_OK);
        Log.i(TAG, "Text reading: Done");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Read foreign language: Done ");
    }
}
