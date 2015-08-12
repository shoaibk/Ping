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

import android.os.Parcel;
import android.os.Parcelable;

public class PingResult implements Parcelable {

    private String mRemoteIP;
    private String mRemoteName;
    private float mMinRtt;
    private float mAvgRtt;
    private float mMaxRtt;
    private int mNumberOfPings;
    private int mErrorCode;

    //private String mUserIP;
    //private float mUserLat;
    //private float mUserLong;
    //private String mUserCity;
    //private String mUserCountry;
    //private float mRemoteLat;
    //private float mRemoteLong;
    //private String mRemoteCity;
    //private String mRemoteCountry;
    //private float mDistanceInKm;


    public PingResult(/*String remoteIP,
                      String remoteName,
                      float minRtt,
                      float avgRtt,
                      float maxRtt,
                      int numberOfPing*/) {
        /*mRemoteIP = remoteIP;
        mRemoteName = remoteName;
        mMinRtt = minRtt;
        mAvgRtt = avgRtt;
        mMaxRtt = maxRtt;
        mNumberOfPings = numberOfPing;*/
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRemoteIP);
        dest.writeString(mRemoteName);
        dest.writeFloat(mMinRtt);
        dest.writeFloat(mAvgRtt);
        dest.writeFloat(mMaxRtt);
        dest.writeInt(mNumberOfPings);
        dest.writeInt(mErrorCode);
    }

    public static final Parcelable.Creator<PingResult> CREATOR = new Parcelable.Creator<PingResult>() {
        public PingResult createFromParcel(Parcel parcel) {
            return new PingResult(parcel);
        }

        public PingResult[] newArray(int size) {
            return new PingResult[size];
        }
    };

    private PingResult(Parcel in) {
        mRemoteIP = in.readString();
        mRemoteName = in.readString();
        mMinRtt = in.readFloat();
        mAvgRtt = in.readFloat();
        mMaxRtt = in.readFloat();
        mNumberOfPings = in.readInt();
        mErrorCode = in.readInt();
    }



    @Override
    public String toString() {
        return "[Ping]x" + mNumberOfPings +
                " to: " + mRemoteName +
                //" ( " + mRemoteIP + " ) " +
                " min : " + mMinRtt +
                " avg : " + mAvgRtt +
                " max : " + mMaxRtt +
                " error_code: " + mErrorCode;
    }


    public String getRemoteIP() {
        return mRemoteIP;
    }

    public String getRemoteName() {
        return mRemoteName;
    }


    public float getMinRtt() {
        return mMinRtt;
    }

    public float getAvgRtt() {
        return mAvgRtt;
    }

    public float getMaxRtt() {
        return mMaxRtt;
    }

    public int getNumberOfPings() {
        return mNumberOfPings;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setRemoteIP(String mRemoteIP) {
        this.mRemoteIP = mRemoteIP;
    }

    public void setRemoteName(String mRemoteName) {
        this.mRemoteName = mRemoteName;
    }

    public void setMinRtt(float mMinRtt) {
        this.mMinRtt = mMinRtt;
    }

    public void setAvgRtt(float mAvgRtt) {
        this.mAvgRtt = mAvgRtt;
    }

    public void setMaxRtt(float mMaxRtt) {
        this.mMaxRtt = mMaxRtt;
    }

    public void setNumberOfPings(int mNumberOfPings) {
        this.mNumberOfPings = mNumberOfPings;
    }

    public void setErrorCode(int errorCode) {
        this.mErrorCode = errorCode;
    }
}
