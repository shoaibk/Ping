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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * TODO: Use searchview for typing in web address
 */
public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "PING";
    private static final String ERROR_PING_FAILED = "ERROR_PING_FAILED";
    private static final String ERROR_NO_INTERNET = "ERROR_NO_INTERNET";

    private boolean PING_IN_PROGRESS = false;

    private TextView tv;
    private EditText et;
    private ProgressBar pb;
    private Button btn;
    private AsyncTask pingTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progress_bar);
        et = (EditText) findViewById(R.id.ping_destination);
        tv = (TextView) findViewById(R.id.ping_result);
        btn = (Button) findViewById(R.id.ping_start);

        //btn.setBackgroundColor(Color.rgb(31, 73, 212));

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(PING_IN_PROGRESS) {

                    pb.setVisibility(ProgressBar.INVISIBLE);
                    pingTask.cancel(true);

                } else {

                    String stringUrl = et.getText().toString();

                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        pingTask = new PingTask().execute(stringUrl);

                    } else {
                        tv.setTextSize(20);
                        tv.setTextColor(Color.RED);
                        tv.setText(R.string.no_internet);
                    }

                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }

            }
        });

        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(event.getRawX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        et.setText("");
                        et.selectAll();

                        return true;
                    }
                }
                return false;
            }
        });

    }


    private String ping(String url) {


        if(url.equals("")) url = (String)et.getHint();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to initiate a ping.
    private class PingTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            tv.setText("");
            pb.setVisibility(ProgressBar.VISIBLE);
            btn.setText(R.string.cancel);
            PING_IN_PROGRESS = true;

            //btn.setBackgroundColor(Color.rgb(212, 72, 28));

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
    }
}