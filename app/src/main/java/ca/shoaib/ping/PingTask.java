/*
 * Copyright (C) 2015  Shoaib Khan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.shoaib.ping;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingTask extends AsyncTask<String, Void, String> {

    private static final String DEBUG_TAG = PingTask.class.getSimpleName();
    private static final String ERROR_PING_FAILED = "ERROR_PING_FAILED";
    private static final String ERROR_NO_INTERNET = "ERROR_NO_INTERNET";
    private boolean PING_IN_PROGRESS = false;
    private Context mContext;
    private TextView tv;
    private ProgressBar pb;
    private Button btn;


    public PingTask(Context context,
                    TextView resultText,
                    ProgressBar progressBar,
                    Button pingButton) {
        mContext = context;
        tv = resultText;
        pb = progressBar;
        btn = pingButton;
    }

    @Override
    protected void onPreExecute() {
        tv.setText("");
        pb.setVisibility(ProgressBar.VISIBLE);
        btn.setText(R.string.cancel);
        PING_IN_PROGRESS = true;
    }

    @Override
    protected String doInBackground(String... urls) {

        try {
            return ping(urls[0]);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "exception", e);
            return ERROR_PING_FAILED;
        }
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        pb.setVisibility(ProgressBar.INVISIBLE);
        btn.setText(R.string.ping);
        PING_IN_PROGRESS = false;

        //btn.setBackgroundColor(Color.rgb(31, 73, 212));

        if(result.equals(ERROR_PING_FAILED)) {
            tv.setTextSize(20);
            tv.setText(R.string.ping_failed);
            tv.setTextColor(Color.RED);
        } else {
            tv.setTextSize(50);
            tv.setTextColor(Color.GRAY);
            tv.setText(result);
        }
    }

    @Override
    protected void onCancelled(){
        btn.setText(R.string.ping);
        //btn.setBackgroundColor(Color.rgb(31, 73, 212));
        pb.setVisibility(ProgressBar.INVISIBLE);
        PING_IN_PROGRESS = false;
    }

    private String ping(String url) {


        if(url.equals("")) url = "www.google.com";
        String command = "/system/bin/ping -c 5 -q -i 0.2 " + url;


        String result = "";
        Process process = null;
        String lastLine = "";
        try {
            process = Runtime.getRuntime().exec(command);
            DataInputStream osRes = new DataInputStream(process.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(osRes));

            String line;

            try {
                while ((line = reader.readLine()) != null || reader.read() !=-1) {

                    result += line + "\n";
                    lastLine = line;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.d(DEBUG_TAG, lastLine);
        }
        return parseMeanRTT(lastLine);
    }

    private String parseMeanRTT(String lastLine) {
        String delims = "[/]+";
        String[] tokens = lastLine.split(delims);
        for(int i = 0; i < tokens.length; i++) {
            Log.d(DEBUG_TAG, tokens[i]);
        }

        return discardDecimal(tokens[4]);

    }

    private String discardDecimal(String number) {
        String delim = "[.]";
        String[] tokens = number.split(delim);
        return tokens[0] + " ms";
    }
}


