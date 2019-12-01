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
        checkRecordAudioPermissions();
    }

    private void checkRecordAudioPermissions() {
        Log.i(TAG, "checkRecordAudioPermissions: start");
        int recordCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (recordCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST);
            Log.i(TAG, "checkRecordAudioPermissions: end");
        } else {
            checkStoragePermissions();
            Log.i(TAG, "checkRecordAudioPermissions: end");
        }
    }

    private void checkStoragePermissions() {
        Log.i(TAG, "checkStoragePermissions: start");
        int recordCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (recordCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST);
            Log.i(TAG, "checkRecordAudioPermissions: end");
        } else {
            mPasithea = new PasitheaBuilder().setValues(getApplication()).
                    setInitListener(new onInitListener() {
                        @Override
                        public void InitDone() {
                            startAnswer();
                        }
                    }).build();
            Log.i(TAG, "checkStoragePermissions: end");
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {
        if(i == AUDIO_REQUEST) {
            checkStoragePermissions();
        }
        if(i == STORAGE_REQUEST){
            mPasithea = new PasitheaBuilder().setValues(getApplication()).
                    setInitListener(new onInitListener() {
                        @Override
                        public void InitDone() {
                            startAnswer();
                        }
                    }).build();
        }
    }

    public void startAnswer(){
        mPasithea.startQuestionAnswer(
                getString(R.string.start_question),
                answerwords,
                this);
    }

    @Override
    public void onAnswerYes() {
        Log.i(TAG, "onAnswer: Yes detected");
        mPasithea.saySomething(getString(R.string.answer_yes), this);
    }

    @Override
    public void onAnswerNo() {
        Log.i(TAG, "onAnswer: No detected");
        mPasithea.saySomething(getString(R.string.answer_no), this);
    }

    @Override
    public void onAnswerUnk() {
        mPasithea.unkAnswer();
        mPasithea.restartQuestionAnswer();
    }

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


@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_TO_TEXT_CODE){
            writeTV.setTextColor(Color.GREEN);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPasithea.saySomething(getString(R.string.partial_reading), new onReadingEndListener() {
                        @Override
                        public void onReadingEnd() {
                            Intent readIntent = new Intent(getApplicationContext(), ReadText.class);
                            startActivityForResult(readIntent, PARTIAL_READING);
                        }
                    });
                }
            }, 500);
        }

        if (requestCode == PARTIAL_READING){
            readTV.setTextColor(Color.GREEN);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
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

        if(requestCode == NAVIGATION_CODE){
            if (resultCode == RESULT_OK) {
                navTV.setTextColor(Color.GREEN);
                Log.i(TAG, "onActivityResult: Navigation finished");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPasithea.saySomething(getString(R.string.foreign_language_reading));
                    Intent LangIntent = new Intent(getApplicationContext(), ReadForeignText.class);
                        startActivityForResult(LangIntent, DEFAULT_LANGUAGE_CODE);
                    }
                },500);
            }
        }

        if (requestCode == DEFAULT_LANGUAGE_CODE) {
            if (resultCode == RESULT_OK){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPasithea.saySomething(getString(R.string.foreign_language_reading_failure));
                        Locale ForeignLocale;
                        if (mLocale.getLanguage().equals("fr")) {
                            ForeignLocale = new Locale("en_US");
                        } else if (mLocale.getLanguage().equals("en")){
                            ForeignLocale = new Locale("fr_FR");
                        } else {
                            ForeignLocale = new Locale("fr_FR");
                        }
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

        if (requestCode == FOREIGN_LANGUAGE_CODE){
            if (resultCode == RESULT_OK){
                Locale oldLocale = null;
                langTV.setTextColor(Color.GREEN);
                if (mLocale.getLanguage().equals("fr")){
                    oldLocale = new Locale("fr_FR");
                } else if (mLocale.getLanguage().equals("en")){
                    oldLocale = new Locale("en_US");
                }
                mPasithea.changeLanguage(oldLocale, new onChangeLanguageDoneListener() {
                    @Override
                    public void onReconfigurationDone() {
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
