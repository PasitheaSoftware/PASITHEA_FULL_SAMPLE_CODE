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

public class ReadForeignText extends AppCompatActivity implements onReadingEndListener {
    private static final String TAG = "ReadForeignText";

    private Pasithea mPasithea = Pasithea.getInstance();

    private static Context ReadContext;
    private static Locale ReadLocale;
    private static AppCompatActivity ReadActivity;

    TextView readTextView = null;

    String file;
    String foreigntext;
    final String[] foreignsentences = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_text);

        ReadContext = getApplicationContext();
        ReadLocale = getResources().getConfiguration().locale;
        ReadActivity = this;

        readTextView = (TextView) findViewById(R.id.readtextView);
        readTextView.setMovementMethod(new ScrollingMovementMethod());

        if (getReadLocale().getLanguage().equals("fr")){
            file = "text_en";
        } else if (getReadLocale().getLanguage().equals("en")){
            file = "text_fr";
        } else {
            mPasithea.saySomething(getString(R.string.locale_error));
            setResult(RESULT_OK);
            finish();
        }

        foreigntext = PrepareText.getText(getApplicationContext(),file);
        foreignsentences[0] = foreigntext;

        Log.i(TAG, "startReading: " + file);
        readTextView.setText(foreigntext);
        mPasithea.startReadingText(foreignsentences, this);
    }

    public static Context getReadContext() {
        return ReadContext;
    }

    public static Locale getReadLocale() {
        return ReadLocale;
    }

    public static AppCompatActivity getReadActivity() {
        return ReadActivity;
    }

    @Override
    public void onReadingEnd() {
        mPasithea.pauseReading();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ReadForeignText finished");
    }
}
