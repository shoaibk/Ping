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
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class PingTask extends AsyncTask<String, Void, PingResult> {

    private static final String DEBUG_TAG = PingTask.class.getSimpleName();
    private static final String ERROR_PING_FAILED = "ERROR_PING_FAILED";
    private static final String ERROR_NO_INTERNET = "ERROR_NO_INTERNET";
    private static final String DEFAULT_URL = "www.google.com";
    private static final String PING_PATH = "/system/bin/ping";

    public static final int PING_ERROR_NOERROR = 0;
    public static final int PING_ERROR_NOTREACHABLE = 1;
    //public static final String PING_RESULT = "ping_result";

    private boolean PING_IN_PROGRESS = false;
    private Context mContext;
    private PingResult mPingResult;
    private int mNumberOfPings = 3;
    private float mPingInterval = 0.2f;

    private List<PingResult> mPingList;
    private PingListAdapter mPingAdapter;


    public PingTask(Context context, List<PingResult> pingList, PingListAdapter pingListAdapter) {
        mContext = context;
        mPingList = pingList;
        mPingAdapter = pingListAdapter;
    }

    @Override
    protected void onPreExecute() {
        //mPingResult = new PingResult();
    }

    @Override
    protected PingResult doInBackground(String... urls) {
        mPingResult = new PingResult();
        try {
            mPingResult.setRemoteName(urls[0]);
            mPingResult.setNumberOfPings(mNumberOfPings);
            ping(urls[0]);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "exception", e);
        }
        return mPingResult;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(PingResult pingResult) {
        /*Intent intent = new Intent(mContext, PingDetailActivity.class);
        Log.d(DEBUG_TAG, pingResult.toString());
        intent.putExtra(PING_RESULT, mPingResult);
        mContext.startActivity(intent);*/
        mPingList.add(pingResult);
        mPingAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCancelled(){

    }

    private void ping(String url) {

        String command = PING_PATH
                + " -c " + mNumberOfPings
                + " -q "
                + " -i " + mPingInterval + " "
                + (url.equals("") ? DEFAULT_URL : url);

        String result = "";
        Process process = null;
        String lastLine = "";
        String firstLine = "";
        try {
            process = Runtime.getRuntime().exec(command);
            DataInputStream osRes = new DataInputStream(process.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(osRes));
            String line;
            int lineIndex = 0;

            try {
                while ((line = reader.readLine()) != null || reader.read() != -1) {
                    if (lineIndex++ == 0) firstLine = line;
                    result += line + "\n";
                    lastLine = line;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Log.d(DEBUG_TAG, "result = ");
            Log.d(DEBUG_TAG, "result = " + result);
            //Log.d(DEBUG_TAG, "lastLine = ");
            Log.d(DEBUG_TAG, "lastLine = " + lastLine + " end_last_line");
        }
        parseIP(firstLine);
        parseRtts(lastLine);
    }

    private void parseRtts(String lastLine) {
        String delims = "[/=]+";
        String[] tokens = lastLine.split(delims);
        for(int i = 0; i < tokens.length; i++) {
            Log.d(DEBUG_TAG, tokens[i]);
        }
        if (tokens.length > 6) {
            float minRtt = -1.0f;
            float avgRtt = -1.0f;
            float maxRtt = -1.0f;
            try {
                minRtt = Float.parseFloat(tokens[4]);
                avgRtt = Float.parseFloat(tokens[5]);
                maxRtt = Float.parseFloat(tokens[6]);
                mPingResult.setErrorCode(PING_ERROR_NOERROR);

            } catch (NumberFormatException e) {
                mPingResult.setErrorCode(PING_ERROR_NOTREACHABLE);
                e.printStackTrace();
            } finally {
                mPingResult.setMinRtt(minRtt);
                mPingResult.setAvgRtt(avgRtt);
                mPingResult.setMaxRtt(maxRtt);
            }
        } else {
            mPingResult.setErrorCode(PING_ERROR_NOTREACHABLE);
        }
    }

    private String parseIP(String line) {
        String ip = "";
        String delims = "[()]+";
        String[] tokens = line.split(delims);
        for (String token : tokens) {
            Log.d(DEBUG_TAG, token);
        }
        if (tokens.length > 0) {
            ip = tokens[1];
        }
        mPingResult.setRemoteIP(ip);

        return ip;
    }
}


