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
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PingListAdapter extends ArrayAdapter<PingResult> {

    Context context;

    public PingListAdapter(Context context, int resourceId,
                         List<PingResult> items) {
        super(context, resourceId, items);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        PingResult pingResult = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ping_row, null);
        }

        /*ImageView icon = (ImageView) convertView.findViewById(R.id.ping_row_icon);
        String url = artist.getImageThumbnailUrl();
        Picasso.with(context).load(url).into(icon);*/

        TextView pingName = (TextView) convertView.findViewById(R.id.ping_row_name);
        pingName.setText(pingResult.getRemoteName());

        TextView pingRtt = (TextView) convertView.findViewById(R.id.ping_row_rtt);
        pingName.setText("" + pingResult.getAvgRtt() + " ms");

        return convertView;
    }

}
