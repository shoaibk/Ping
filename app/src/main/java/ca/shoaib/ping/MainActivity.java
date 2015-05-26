package ca.shoaib.ping;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button pingButton = (Button) findViewById(R.id.ping_start);
        pingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.ping_result);
                tv.setText("");

                EditText et = (EditText) findViewById(R.id.ping_destination);
                String pingDestination = et.getText().toString();
                if(pingDestination.equals("")) pingDestination = (String)et.getHint();
                String command = "/system/bin/ping -c 10 -q -i 0.2 " + pingDestination;
                String result = pingStart(command);


                tv.setText(result + " ms");
            }
        });
    }

    private String pingStart(String command) {
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
        }
        return parseMeanRTT(lastLine);
        //return result;
    }

    private String parseMeanRTT(String lastLine) {
        String delims = "[/]+";
        String[] tokens = lastLine.split(delims);
        for(int i = 0; i < tokens.length; i++) {
            Log.d("PING", tokens[i]);
        }

        return discardDecimal(tokens[4]);

    }

    private String discardDecimal(String number) {
        String delim = "[.]";
        String[] tokens = number.split(delim);
        return tokens[0];
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
}
