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

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**

 */
public class PingListFragment extends Fragment {
    public static final String TAG = PingListFragment.class.getSimpleName();
    private static final String KEY_PINGS = "ping_results";

    private TextView tv;
    private EditText et;
    private ProgressBar pb;
    private Button btn;

    private ArrayList<PingResult> pingList;
    private PingListAdapter pingListAdapter;
    private ListView mListView;

    //private PingListCallback mListListener;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks
     */
    private PingListCallback mPingListCallback = sPingListCallback;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    public PingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState != null ) {
            pingList = savedInstanceState.getParcelableArrayList(KEY_PINGS);
        } else {
            pingList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ping_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_ping);

        pingListAdapter = new PingListAdapter(getActivity(), R.layout.ping_row, pingList);
        mListView.setAdapter(pingListAdapter);
        //mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPingListCallback.onPingSelected(pingList.get(position));
                mActivatedPosition = position;
                setActivatedPosition(position);
                //Log.d(TAG, "ArtistId: " + artistList.get(position).getArtistId());
            }
        });


        pb = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        et = (EditText) rootView.findViewById(R.id.ping_destination);
        tv = (TextView) rootView.findViewById(R.id.ping_result);
        btn = (Button) rootView.findViewById(R.id.ping_start);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String stringUrl = et.getText().toString();

                ConnectivityManager connMgr = (ConnectivityManager)
                        getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    AsyncTask pingTask = new PingTask(getActivity(), pingList, pingListAdapter).execute(stringUrl);

                } else {
                    tv.setTextSize(20);
                    tv.setTextColor(Color.RED);
                    tv.setText(R.string.no_internet);
                }

                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        });

        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        et.setText("");
                        et.selectAll();

                        return true;
                    }
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(PingResult pingResult) {
        if (mPingListCallback != null) {
            mPingListCallback.onPingSelected(pingResult);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mPingListCallback = (PingListCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PingListCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPingListCallback = sPingListCallback;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
        outState.putParcelableArrayList(KEY_PINGS, pingList);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface PingListCallback {
        public void onPingSelected(PingResult pingResult);
    }

    /**
     * A dummy implementation of the callback interface that does nothing.
     * Used only when this fragment is not attached to an activity.
     */
    private static PingListCallback sPingListCallback = new PingListCallback() {
        @Override
        public void onPingSelected(PingResult result) {}
    };

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        mListView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mListView.setItemChecked(mActivatedPosition, false);
        } else {
            mListView.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

}
