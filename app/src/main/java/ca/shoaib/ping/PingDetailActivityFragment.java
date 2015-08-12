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

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PingDetailActivityFragment extends Fragment {
    public static final String DEBUG_TAG = PingDetailActivityFragment.class.getSimpleName();

    public PingDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_container = inflater.inflate(R.layout.fragment_ping_detail, container, false);
        if( savedInstanceState == null ) {
            if (getArguments().containsKey(PingTask.PING_RESULT)) {
                PingResult pingResult = getArguments().getParcelable(PingTask.PING_RESULT);

                assert pingResult != null;
                setStatusImage(pingResult.getErrorCode(), fragment_container);
                Log.d(DEBUG_TAG, pingResult.toString());
                TextView rtt_avg_value = (TextView) fragment_container.findViewById(R.id.rtt_avg_value);
                rtt_avg_value.setText("" + pingResult.getAvgRtt() + " ms");
            }
        }

        return fragment_container;
    }

    private void setStatusImage(int errorCode, View container) {
        ImageView statusImage = (ImageView) container.findViewById(R.id.ping_status_icon);
        switch (errorCode) {
            case PingTask.PING_ERROR_NOERROR:
                statusImage.setImageResource(R.drawable.ok);
                break;
            case PingTask.PING_ERROR_NOTREACHABLE:
            default:
                statusImage.setImageResource(R.drawable.unreachable);
                break;
        }
    }
}
