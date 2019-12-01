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
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;

/**
 * Prepare the texts to be read.</br>
 * This class is optional and specific to this sample code. In this sample code the text are
 * located in the res/raw directory. In a production system this class must be changed to reflect
 * the correct path to the texts files.
 */

public class PrepareText {
    private static final String TAG = "PrepareText";

    public PrepareText(){ }

    // Return the res ID of the texts in the res/raw folder
    private static int getTextId(Context context, String filename){

        int id = context.getResources().getIdentifier(filename,
                "raw",
               context.getPackageName());
        return id;
    }

    /**
     * Open the text file and build a string with each line of the file.
     *
     * @param context The app context
     * @param filename The text filename to open
     * @return The text to reade
     */
    public static String getText(Context context, String filename){
        Log.i(TAG, "Text preparation: Start");
        String line;
        StringBuilder text = new StringBuilder();

        // Get the res ID of the file
        int resid = getTextId(context, filename);

        // Open the file, read each lines and appends the string
        InputStream mInputStream = context.getResources().openRawResource(resid);
        InputStreamReader mInputStreamReader = new InputStreamReader(mInputStream);
        BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
        try {
            while ((line = mBufferedReader.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e){
            Log.e(TAG, "IOException detected in text preparation: ", e);
        }
        Log.i(TAG, "Text preparation: Done");
        return text.toString();
    }

    /**
     * Compute the number of sentences in the file to read
     *
     * @param textToExamine The text to read
     * @return The sentences number
     */
    public static int getsentencesnumber(String textToExamine){
        Log.i(TAG, "Sentence number: Start");
        ArrayList<String> mArrayList = new ArrayList<String>();
        BreakIterator mBreakIterator = BreakIterator.getSentenceInstance();
        mBreakIterator.setText(textToExamine);
        int start = mBreakIterator.first();
        for (int end = mBreakIterator.next(); end != BreakIterator.DONE; start = end, end = mBreakIterator.next()) {
            mArrayList.add(textToExamine.substring(start, end));
        }
        Log.i(TAG, "Sentence number: Done");
        return mArrayList.size();
    }
}
