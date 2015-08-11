## Ping
Android Ping utility wrapper that uses the underlying linux ping utility. The app can be downloaded from the [Google Play Store](https://play.google.com/store/apps/details?id=ca.shoaib.ping)

### Features
- Shows Average Round Trip Time (RTT)
- Detects if the device is offline/online
- Uses AppCompat theme

### Todo
- Create model class - PingResult implementing Parcelable
- Show connection type of the user - Wifi/GSM/3G/4G

- Show pulic IP address of the user
- Separate AsyncTask into a class
- Show previous ping results into a Listview
- Show current ping result into a Detail view
- Use fragment to support tablet layout
- Show number of ping (icmp) packets
- Show min/avg/max in a bar
- Show location of the IP
- Show estimated distance to the remote IP
- ContentProvider for common website names
