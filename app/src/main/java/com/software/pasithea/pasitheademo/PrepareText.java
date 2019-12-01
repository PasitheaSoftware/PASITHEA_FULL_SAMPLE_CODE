package com.software.pasithea.pasitheademo;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;

public class PrepareText {
    private static final String TAG = "PrepareText";
    private static int nbSentences = 0;

    public PrepareText(){}

    private static int getTextId(Context context, String filename){

        int id = context.getResources().getIdentifier(filename,
                "raw",
               context.getPackageName());
        return id;
    }

    public static String getText(Context context, String filename){
        String line;
        StringBuilder text = new StringBuilder();
        ArrayList<String> sentencelist = new ArrayList<String>();
        int resid = getTextId(context, filename);
        InputStream mInputStream = context.getResources().openRawResource(resid);
        InputStreamReader mInputStreamReader = new InputStreamReader(mInputStream);
        BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
        try {
            while ((line = mBufferedReader.readLine()) != null) {
                text.append(line);
                //text.append('\n');
            }
        } catch (IOException e){
            Log.e(TAG, "getTextToDisplay: ", e);
        }
        return text.toString();
    }

    public static int getsentencesnumber(String textToExamine){
        ArrayList<String> mArrayList = new ArrayList<String>();
        BreakIterator mBreakIterator = BreakIterator.getSentenceInstance();
        mBreakIterator.setText(textToExamine);
        int start = mBreakIterator.first();
        for (int end = mBreakIterator.next(); end != BreakIterator.DONE; start = end, end = mBreakIterator.next()) {
            mArrayList.add(textToExamine.substring(start, end));
        }
        return mArrayList.size();
    }
}
