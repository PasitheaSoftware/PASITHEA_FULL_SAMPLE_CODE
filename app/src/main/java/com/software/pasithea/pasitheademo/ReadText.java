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

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.software.pasithea.pasithea.Pasithea;
import com.software.pasithea.pasithea.onReadingEndListener;

/**
 * The pertial text reading activity.</br>
 * This activity will set the text to read depending on the system locale.</br>
 * By default we choose to read only 5% of the text but this can be changed in the array percent.
 * The number of sentences to read is rounded up.
 */
public class ReadText extends AppCompatActivity implements onReadingEndListener {
    private static final String TAG = "ReadText";

    private Pasithea mPasithea = Pasithea.getInstance();

    TextView readTextView = null;

    private String filename1;
    private String[] text = new String[1];
    private int[] percent = new int[1];
    private String text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_text);

        Log.i(TAG, "Partial reading: Start");

        readTextView = (TextView) findViewById(R.id.readtextView);
        readTextView.setMovementMethod(new ScrollingMovementMethod());

        // Set the text based upon the Locale configured in the system
        if(getResources().getConfiguration().locale.getLanguage().equals("fr")){
            filename1 = "text1_fr";
        } else if (getResources().getConfiguration().locale.getLanguage().equals("en")){
            filename1 = "text1_en";
        } else {
            mPasithea.saySomething(getString(R.string.locale_error));
            setResult(RESULT_OK);
            finish();
        }
        Log.i(TAG, "Text detection:" + filename1);

        // Prepare the text to read
        text1 = PrepareText.getText(getApplicationContext(),filename1);

        // Says a informative message and starts the reading
        text[0] = text1;
        percent[0] = 5;
        int nsentencesize = PrepareText.getsentencesnumber(text1);
        String msgStart = getString(R.string.read_text_1) + nsentencesize + getString(R.string.read_text_2) + percent[0] + getString(R.string.read_text_3);
        mPasithea.saySomething(msgStart);
        readTextView.setText(text1);
        Log.i(TAG, "Text reading: Start");
        mPasithea.startReadingText(text, percent, this);
    }

    /**
     * Listener executed at the end of the reading.
     */
    @Override
    public void onReadingEnd() {
        mPasithea.pauseReading();
        //Says a message and exit
        String msg = getString(R.string.continue_reading);
        mPasithea.saySomething(msg, new onReadingEndListener() {
            @Override
            public void onReadingEnd() {
                Log.i(TAG, "Text reading: Done");
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Partial reading: Done");
    }
}
