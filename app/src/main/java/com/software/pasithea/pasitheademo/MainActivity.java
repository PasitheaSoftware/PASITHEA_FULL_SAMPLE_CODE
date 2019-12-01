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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.software.pasithea.pasithea.Pasithea;
import com.software.pasithea.pasithea.PasitheaBuilder;
import com.software.pasithea.pasithea.onAnswerListener;
import com.software.pasithea.pasithea.onChangeLanguageDoneListener;
import com.software.pasithea.pasithea.onInitListener;
import com.software.pasithea.pasithea.onReadingEndListener;

import java.util.Locale;

/**
 * Main activity class to manage the function and to display the menu status.</br>
 * When this activity is created and started it begins with a permissions check.
 * The PASITHEA speech recognizer needs to have the AUDIO_RECORDING and the READ_EXTERNAL_STORAGE permissions.</br>
 * Once the permission are set (granted or denied), the activity build a PASITHEA instance. If any of these permission is denied,
 * PASITHEA will still continue but the speech recognizer will not work. </br>
 * After PASITHEA has been instantiated, the first sample function (Question/Answer) is executed.</br>
 * The ohers samples, that need an activity are executed with a startActivityForResult() and the main activity is waiting the return from
 * the running activity.
 */

public class MainActivity extends AppCompatActivity implements onAnswerListener, onReadingEndListener {
    private static final String TAG = "MainActivity";

    private static final int AUDIO_REQUEST = 1;
    private static final int STORAGE_REQUEST = 2;

    public static final int PARTIAL_READING = 100;
    public static final int VOICE_TO_TEXT_CODE = 200;
    public static final int NAVIGATION_CODE = 300;
    public static final int DEFAULT_LANGUAGE_CODE = 400;
    public static final int FOREIGN_LANGUAGE_CODE = 500;

    private static Locale mLocale;

    String[] answerwords = new String[2];

    private Pasithea mPasithea = null;

    private static TextView answerTV = null;
    private static TextView readTV = null;
    private static TextView writeTV = null;
    private static TextView navTV = null;
    private static TextView langTV = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocale = getResources().getConfiguration().locale;

        navTV = findViewById(R.id.navigate_text);
        answerTV = findViewById(R.id.question);
        readTV = findViewById(R.id.read_text);
        writeTV = findViewById(R.id.write_text);
        langTV = findViewById(R.id.change_language);

        answerwords[0] = getString(R.string.answer_word_yes);
        answerwords[1] = getString(R.string.answer_word_no);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start the permission request
        checkRecordAudioPermissions();
    }

    private void checkRecordAudioPermissions() {
        Log.i(TAG, "Check audio permissions: Start");
        int recordCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (recordCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST);
        } else {
            checkStoragePermissions();
            Log.i(TAG, "Audio permissions granted");
            Log.i(TAG, "Check audio permission: Done");
        }
    }

    private void checkStoragePermissions() {
        Log.i(TAG, "Check storage permissions: Start");
        int recordCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (recordCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST);
        } else {
            Log.i(TAG, "Storage permission granted");
            Log.i(TAG, "Check storage permissions: Done");
            createPasitheaInstance();
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {
        if(i == AUDIO_REQUEST) {
            int audioCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            if (audioCheck == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "Audio permission granted");
            } else {
                Log.i(TAG, "Audio permission denied");
            }
            Log.i(TAG, "Check audio permission: Done");
            checkStoragePermissions();
        }
        if(i == STORAGE_REQUEST){
            int storageCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (storageCheck == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Storage permission granted");
            } else {
                Log.i(TAG, "Storage permission denied");
            }
            Log.i(TAG, "Check storage permission: Done");
            createPasitheaInstance();
        }
    }

    /**
     * Build a PASITHEA and return an instance.
     * This method is called right after the permission request is done.
     */
    private void createPasitheaInstance(){
        mPasithea = new PasitheaBuilder().setValues(getApplication()).
                setInitListener(new onInitListener() {
                    @Override
                    public void InitDone() {
                        startAnswer(); // start the Question/Answer function
                    }
                }).build();
    }

    /**
     * Start the Question/Answer function of PASITHEA.</br>
     * This method is passed to PASITHEA through a listener and is executed once PASITHEA is instantiated.</br>
     * The listener used in this method is implemented in the main activity.</br>
     */
    public void startAnswer(){
        mPasithea.startQuestionAnswer(
                getString(R.string.start_question),
                answerwords,
                this);
    }

    /**
     * onAnswerYes():</br>
     * Executed when the answer yes is detected</br>
     */
    @Override
    public void onAnswerYes() {
        Log.i(TAG, "onAnswer: Yes detected");
        mPasithea.saySomething(getString(R.string.answer_yes), this);
    }

    /**
     * Executed when the answer no is detected</br>
     */
    @Override
    public void onAnswerNo() {
        Log.i(TAG, "onAnswer: No detected");
        mPasithea.saySomething(getString(R.string.answer_no), this);
    }

    /**
     * Executed when the answer is neither yes or no. For this sample code we used method available in PASITHEA.</br>
     * unkAnswer() will say a generic sentence to inform the user that the detection failed.</br>
     * restartQuestionAnswer() will restart the speech recognizer with the same parameters.</br>
     * These methods are optional and the developper can write his or her own methods.
     */
    @Override
    public void onAnswerUnk() {
        mPasithea.unkAnswer();
        mPasithea.restartQuestionAnswer();
    }

    /**
     * Listener used to trigger the next activity after the Question/Answer is done.
     */
    @Override
    public void onReadingEnd() {
        mPasithea.stopQuestionAnswer();
        answerTV.setTextColor(Color.GREEN);
        mPasithea.saySomething(getString(R.string.dictation), new onReadingEndListener() {
            @Override
            public void onReadingEnd() {
                Intent voiceIntent = new Intent(getApplicationContext(), VoiceToText.class);
                startActivityForResult(voiceIntent, VOICE_TO_TEXT_CODE);
            }
        });
    }

    /**
     * The callback method to trigger the sample intents.
     * Because of race condition between the setTextColor() and the saysomething() methods we postpone
     * the start of the next intent w/ a Handler.postDelayed of 500 ms.
     *
     * @param requestCode the int defined for each activity
     * @param resultCode the int result for the activity (Not used in this sample code)
     * @param data the data returned by the Intent (Not used in this sample code)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Return from the VoiceToText Intent
        if (requestCode == VOICE_TO_TEXT_CODE){
            writeTV.setTextColor(Color.GREEN);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Say a vocal message and execute the next intent upon reading completion
                    mPasithea.saySomething(getString(R.string.partial_reading),
                            new onReadingEndListener() {
                        @Override
                        public void onReadingEnd() {
                            Intent readIntent = new Intent(getApplicationContext(), ReadText.class);
                            startActivityForResult(readIntent, PARTIAL_READING);
                        }
                    });
                }
            }, 500);
        }

        // Return from the Partial text reading
        if (requestCode == PARTIAL_READING){
            readTV.setTextColor(Color.GREEN);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Say a vocal message to provide the help to the user
                    // The message depends on the system language
                    // Start the navigation intent upon message reading completion
                    mPasithea.saySomething(getString(R.string.voice_control));
                    String helpfile;
                    if (mLocale.getLanguage().equals("fr")){
                        helpfile = "help_fr";
                    } else if (mLocale.getLanguage().equals("en")){
                        helpfile = "help_en";
                    } else {
                        helpfile = "help_fr";
                    }
                    String help;
                    String[] helpsentences = new String[1];
                    help = PrepareText.getText(getApplicationContext(), helpfile);
                    helpsentences[0] = help;
                    mPasithea.startReadingText(helpsentences, new onReadingEndListener() {
                        @Override
                        public void onReadingEnd() {
                            Intent navIntent = new Intent(getApplicationContext(), Navigation.class);
                            startActivityForResult(navIntent, NAVIGATION_CODE);
                        }
                    });
                }
            }, 500);
        }

        // Return from the Navigation intent
        if(requestCode == NAVIGATION_CODE){
            if (resultCode == RESULT_OK) {
                navTV.setTextColor(Color.GREEN);
                Log.i(TAG, "onActivityResult: Navigation finished");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Say a vocal message and execute the next intent upon reading completion
                        // This intent will read a text in a foreign language w/ the default language
                        // This results in a not understandable reading
                        mPasithea.saySomething(getString(R.string.foreign_language_reading));
                    Intent LangIntent = new Intent(getApplicationContext(), ReadForeignText.class);
                        startActivityForResult(LangIntent, DEFAULT_LANGUAGE_CODE);
                    }
                },500);
            }
        }

        // Return from the failed foreign text reading intent
        if (requestCode == DEFAULT_LANGUAGE_CODE) {
            if (resultCode == RESULT_OK){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Say a vocal message
                        mPasithea.saySomething(getString(R.string.foreign_language_reading_failure));
                        // Change the locale in the text-to-speech engine to make it match the foreign language
                        // So far we only supports French and English
                        Locale ForeignLocale;
                        if (mLocale.getLanguage().equals("fr")) {
                            ForeignLocale = new Locale("en_US");
                        } else if (mLocale.getLanguage().equals("en")){
                            ForeignLocale = new Locale("fr_FR");
                        } else {
                            ForeignLocale = new Locale("fr_FR");
                        }
                        // Dynamicaly reconfigure the text-to-speech engine w/ the new locale
                        mPasithea.changeLanguage(ForeignLocale, new onChangeLanguageDoneListener() {
                            @Override
                            public void onReconfigurationDone() {
                                Intent UsLangIntent = new Intent(getApplicationContext(), ReadForeignText.class);
                                startActivityForResult(UsLangIntent, FOREIGN_LANGUAGE_CODE);
                            }
                        });
                    }
                },500);
            }
        }

        // return from the correct foreign language reading
        if (requestCode == FOREIGN_LANGUAGE_CODE){
            if (resultCode == RESULT_OK){
                Locale oldLocale = null;
                langTV.setTextColor(Color.GREEN);
                if (mLocale.getLanguage().equals("fr")){
                    oldLocale = new Locale("fr_FR");
                } else if (mLocale.getLanguage().equals("en")){
                    oldLocale = new Locale("en_US");
                }
                // Change back the locale of the text-to-speech engine
                mPasithea.changeLanguage(oldLocale, new onChangeLanguageDoneListener() {
                    @Override
                    public void onReconfigurationDone() {
                        // Says a message to inform that the demo is done
                        mPasithea.saySomething(getString(R.string.end_demo));
                    }
                });
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
