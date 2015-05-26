package ca.shoaib.ping;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Todo:
 * Show progressbar when doing ping
 * Show packet sending animation when doing ping
 * Create icon
 *
 */

public class MainActivity extends ActionBarActivity {

    private static final String DEBUG_TAG = "PING";
    private TextView tv;
    private EditText et;
    private ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progress_bar);

        final Button pingButton = (Button) findViewById(R.id.ping_start);
        pingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tv = (TextView) findViewById(R.id.ping_result);
                tv.setText("");

                et = (EditText) findViewById(R.id.ping_destination);
                String stringUrl = et.getText().toString();

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    pb.setVisibility(ProgressBar.VISIBLE);
                    new PingTask().execute(stringUrl);

                } else {
                    tv.setText("No Internet");
                }
            }
        });
    }


    private String ping(String url) {


        if(url.equals("")) url = (String)et.getHint();
        String command = "/system/bin/ping -c 10 -q -i 0.2 " + url;


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
        return tokens[0] + "ms";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class PingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            try {
                return ping(urls[0]);
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "exception", e);
                return "Ping failed.\nURL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            pb.setVisibility(ProgressBar.INVISIBLE);
            tv.setText(result);
        }
    }
}