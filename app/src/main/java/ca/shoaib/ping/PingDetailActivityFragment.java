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

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private static final String KEY_DETAIL = "ping_detail";
    private PingResult mPingDetail;

    public PingDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPingDetail = savedInstanceState.getParcelable(KEY_DETAIL);
        } else {
            if (getArguments().containsKey(PingListActivity.PING_DETAIL))
                mPingDetail = getArguments().getParcelable(PingListActivity.PING_DETAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_container = inflater.inflate(R.layout.fragment_ping_detail, container, false);
        assert mPingDetail != null;
        ImageView statusImage = (ImageView) fragment_container.findViewById(R.id.ping_status_icon);
        TextView statusText = (TextView) fragment_container.findViewById(R.id.ping_status_text);
        setStatus(mPingDetail.getErrorCode(), statusImage, statusText);
        Log.d(DEBUG_TAG, mPingDetail.toString());

        TextView tv_rtt_avg = (TextView) fragment_container.findViewById(R.id.rtt_avg_value);
        setRTT(tv_rtt_avg, mPingDetail.getAvgRtt());

        TextView tv_ip_remote = (TextView) fragment_container.findViewById(R.id.ping_destination_address);
        tv_ip_remote.setText(mPingDetail.getRemoteIP());

        TextView tv_remote_name = (TextView) fragment_container.findViewById(R.id.ping_destination_name);
        tv_remote_name.setText(mPingDetail.getRemoteName());

        TextView tv_ping_count = (TextView) fragment_container.findViewById(R.id.count_value);
        tv_ping_count.setText("" + mPingDetail.getNumberOfPings());

        return fragment_container;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_DETAIL, mPingDetail);
    }

    private void setStatus(int errorCode, ImageView statusImage, TextView statusText) {

        switch (errorCode) {
            case PingTask.PING_ERROR_NOERROR:
                statusImage.setImageResource(R.drawable.ic_check_circle_black_48dp);
                statusText.setText("");
                break;
            case PingTask.PING_ERROR_NOTREACHABLE:
            default:
                statusImage.setImageResource(R.drawable.ic_error_outline_black_48dp);
                statusText.setText("Ping failed");
                break;
        }
    }

    private void setRTT(TextView tv, float value) {
        int rounded = Math.round(value);
        tv.setText("" + rounded + " ms");
    }
}
