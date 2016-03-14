package com.andrutyk.nasa.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by admin on 07.03.2016.
 */
public class ConnectionDetector {

    private Context context;

    public ConnectionDetector(Context context){
        this.context = context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (connectivity != null) {
                Network[] networks = connectivity.getAllNetworks();
                NetworkInfo networkInfo;
                if (networks != null) {
                    for (Network network : networks) {
                        networkInfo = connectivity.getNetworkInfo(network);
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        } else {
            if (connectivity != null) {
                NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
                if (networkInfos != null)
                    for (NetworkInfo networkInfo : networkInfos)
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
            }
        }
        return false;
    }
}
