package com.software.pasithea.pasitheademo;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.software.pasithea.pasithea.Pasithea;
import com.software.pasithea.pasithea.onReadingEndListener;

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

        readTextView = (TextView) findViewById(R.id.readtextView);
        readTextView.setMovementMethod(new ScrollingMovementMethod());

        if(getResources().getConfiguration().locale.getLanguage().equals("fr")){
            filename1 = "text1_fr";
        } else if (getResources().getConfiguration().locale.getLanguage().equals("en")){
            filename1 = "text1_en";
        } else {
            mPasithea.saySomething(getString(R.string.locale_error));
            setResult(RESULT_OK);
            finish();
        }

        text1 = PrepareText.getText(getApplicationContext(),filename1);
        text[0] = text1;
        percent[0] = 5;
        int nsentencesize = PrepareText.getsentencesnumber(text1);
        String msgStart = getString(R.string.read_text_1) + nsentencesize + getString(R.string.read_text_2) + percent[0] + getString(R.string.read_text_3);
        mPasithea.saySomething(msgStart);
        Log.i(TAG, "startReading: " + filename1);
        readTextView.setText(text1);
        mPasithea.startReadingText(text, percent, this);
    }

    @Override
    public void onReadingEnd() {
        mPasithea.pauseReading();
        String msg = getString(R.string.continue_reading);
        mPasithea.saySomething(msg, new onReadingEndListener() {
            @Override
            public void onReadingEnd() {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ReadText finished");
    }
}
