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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * TODO: Use searchview for typing in web address
 */
public class PingListActivity extends AppCompatActivity
    implements PingListFragment.PingListCallback{

    private static final String TAG = PingListActivity.class.getSimpleName();
    public static final String PING_DETAIL = "ping_detail";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_list);
        if(findViewById(R.id.ping_detail_container) != null) {
            // we are in Tablet layout
            mTwoPane = true;
            ((PingListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_pings))
                    .setActivateOnItemClick(true);
            Log.d(TAG, "Tablet Layout");
        }
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

    @Override
    public void onPingSelected(PingResult pingResult) {
        Log.d(TAG, "onPingSelected: " + pingResult.toString());
        if (mTwoPane) {
            // In two-pane mode, show the tracks view in this activity by
            // adding or replacing the tracks fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(PING_DETAIL, pingResult);
            //Log.d(TAG, "ArtistId: " + id);
            PingDetailActivityFragment fragment = new PingDetailActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ping_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            Intent intent = new Intent(this, PingDetailActivity.class);
            intent.putExtra(PING_DETAIL, pingResult);
            startActivity(intent);
        }
    }
}