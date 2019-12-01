package com.software.pasithea.pasitheademo;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

import com.software.pasithea.pasithea.Pasithea;
import com.software.pasithea.pasithea.onNavigateListener;

public class Navigation extends AppCompatActivity {
    private static final String TAG = "Navigation";

    private Pasithea mPasithea = Pasithea.getInstance();

    private TextView mTextView;
    private int vocalOn = 0;

    private static Activity mActivity;
    private Locale mLocale;


    private String text1;
    private String text2;
    private String[] textlist = new String[2];
    int[] percentage = new int[2];
    private int textindex = 0;
    HashMap<String, String> keywordslist = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Global variables creation
        mLocale = getResources().getConfiguration().locale;
        String lang = mLocale.getLanguage();
        mActivity = this;


        if (lang.equals("fr")){
            text1 = PrepareText.getText(getApplicationContext(),"text1_fr");
            text2 = PrepareText.getText(getApplicationContext(),"text2_fr");
        } else if (lang.equals("en")) {
            text1 = PrepareText.getText(getApplicationContext(),"text1_en");
            text2 = PrepareText.getText(getApplicationContext(),"text2_en");
        } else {
            mPasithea.saySomething(getString(R.string.locale_error));
            setResult(RESULT_OK);
            finish();
        }


        // Textview and scrolling creation
        mTextView = (TextView) findViewById(R.id.navigationtextView);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        // Keywords list creation
        keywordslist.put("NEXT", getString(R.string.next_text_word));
        keywordslist.put("PREVIOUS", getString(R.string.previous_text_word));
        keywordslist.put("QUIT", getString(R.string.quit_word));
        keywordslist.put("RESUME", getString(R.string.resume_word));
        keywordslist.put("STOP", getString(R.string.stop_word));
        keywordslist.put("NEXT_PART", getString(R.string.next_sentence_word));
        keywordslist.put("PREVIOUS_PART", getString(R.string.previous_sentence_word));

        final GestureDetector NavigationGestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if(vocalOn == 0){
                    vocalOn = 1;
                    mPasithea.startNavigation(keywordslist, getNavigationListener());
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                if(vocalOn == 2){
                    mPasithea.saySomething(getString(R.string.back_from_silence));
                    mPasithea.continueReading();
                    vocalOn = 0;
                }
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });

        // Text preparation
        textlist[0] = text1;
        textlist[1] = text2;
        percentage[0] = 100;
        percentage[1] = 100;

        // Set the onTouchListener with the GestureDetector
        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                NavigationGestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start reading
        //mTextView.setBackgroundColor(Color.YELLOW);
        mTextView.setText(textlist[getTextindex()]);
        mPasithea.startReadingText(textlist, percentage);
    }

    public int getTextindex() {
        return textindex;
    }

    public void setTextindex(int textindex) {
        this.textindex = textindex;
    }

    private onNavigateListener getNavigationListener(){
        return new onNavigateListener() {
            @Override
            public void onNavPrevious() {
                if (getTextindex()-1 >= 0) {
                    mPasithea.saySomething(getString(R.string.previous_text));
                    setTextindex(getTextindex()-1);
                    mTextView.setText(textlist[getTextindex()]);
                    //mTextView.setTextColor(Color.RED);
                    //mTextView.setBackgroundColor(Color.YELLOW);
                    mPasithea.readPreviousText();
                    vocalOn = 0;
                }
            }

            @Override
            public void onNavPreviousPart() {
                mPasithea.saySomething(getString(R.string.previous_sentence));
                mPasithea.readPreviousSentence();
                vocalOn = 0;
            }

            @Override
            public void onNavNext() {
                if (getTextindex()+1 < textlist.length){
                    mPasithea.saySomething(getString(R.string.next_text));
                    setTextindex(getTextindex()+1);
                    mTextView.setText(textlist[getTextindex()]);
                    //mTextView.setTextColor(Color.GREEN);
                    //mTextView.setBackgroundColor(Color.GREEN);
                    mPasithea.readNextText();
                    vocalOn = 0;
                }
            }

            @Override
            public void onNavNextPart() {
                mPasithea.saySomething(getString(R.string.next_sentence));
                mPasithea.readNextSentence();
                vocalOn = 0;
            }

            @Override
            public void onNavQuit() {
                mPasithea.saySomething(getString(R.string.quit));
                mPasithea.stopNavigation();
                Log.d(TAG, "onNavQuit: Au revoir");
                setResult(RESULT_OK);
                finish();
                //return;
            }

            @Override
            public void onNavResume() {
                mPasithea.stopNavigation();
                mPasithea.saySomething(getString(R.string.resume));
                mPasithea.continueReading();
                vocalOn = 0;
            }

            @Override
            public void onNavStop() {
                mPasithea.saySomething(getString(R.string.silence));
                mPasithea.pauseReading();
                vocalOn = 2;
            }

            @Override
            public void onNavUnk() {
                mPasithea.unkAnswer();
                mPasithea.restartNavigation();
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPasithea.pauseReading();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPasithea.pauseReading();
        finish();
    }
}
