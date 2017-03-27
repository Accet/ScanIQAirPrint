package helperClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class WifiHelper {
    //constants
    public static final int WEP = 1;
    public static final int WAP = 2;
    public static final int OPEN_NETWORK = 3;

    public static final String TAG = "WiFi";

    private WifiConfiguration wifiConf;				/* WifiConfiguration object */

    private WifiManager wifiMgr;							/* WifiManager object */

    private WifiInfo wifiInfo;								/* WifiInfo object */
    private Context context;
    private List<ScanResult> wifiScan;				/* List of ScanResult objects */

    public WifiHelper(Context context) {
        wifiMgr  = getWifiManager(context);		// gets wifiMgr in the current context
        wifiInfo = getWifiInfo(context);			// gets wifiInfo in the current context
        wifiConf = getWifiConf(context);			// gets wifiConf in the current context
        wifiScan = getWifiInRange();					// gets wifiScan in the current context
        this.context = context.getApplicationContext();
    }


    public boolean checkWifiEnabled() {
        // checks if WiFi is enabled
        return (wifiMgr != null && wifiMgr.isWifiEnabled());
    }


    public boolean enableWifi() {
        // enables WiFi connection
        return wifiMgr.setWifiEnabled(true);
    }

    public boolean disableWifi() {
        // disables WiFi connection
        return wifiMgr.setWifiEnabled(false);
    }


    public WifiManager getWifiManager(Context context) {
        WifiManager wifiMgr = null;

        // gets WifiManager obj from the system
        wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr == null) {
            Log.d("TAG", "WIFI_SERVICE is the wrong service name.");
        }

        return wifiMgr;
    }

    public WifiInfo getWifiInfo(Context context) {
        WifiInfo wifiInfo = null;

        // gets WiFi network info of the current connection
        if (checkWifiEnabled()) {
            wifiInfo = (WifiInfo) wifiMgr.getConnectionInfo();
        }

        if (wifiInfo == null) {
            Log.d("TAG", "WifiInfo object is empty.");
        }

        return wifiInfo;
    }

    public WifiConfiguration getWifiConf(Context context) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        if (wifiInfo == null) {
            Log.d("TAG", "WifiInfo object is empty");
            return null;
        }

        wifiConfiguration.SSID = wifiInfo.getSSID();
        wifiConfiguration.networkId = wifiInfo.getNetworkId();

        return wifiConfiguration;
    }


    public void clearWifiConfig() {
        wifiConf = new WifiConfiguration();
    }

    public List<ScanResult> getWifiInRange() {
        // gets ~last~ list of WiFi networks accessible through the access point.
        return (wifiScan = (List<ScanResult>) wifiMgr.getScanResults());
    }

    public boolean scanWifiInRange() {
        if (!checkWifiEnabled()) {
            return false;
        }

        if (!wifiMgr.startScan()) {
            Log.d("TAG", "Failed to scan wifi's in range.");
            return false;
        }

        return true;
    }

    public boolean disconnectFromWifi() {
        return (wifiMgr.disconnect());
    }

    public boolean connectToSelectedNetwork(String networkSSID, String networkPassword) {
        int networkId;
        int SecurityProtocol = WAP;

        // Clear wifi configuration variable
        clearWifiConfig();

        // Sets network SSID name on wifiConf
        wifiConf.SSID = "\"" + networkSSID + "\"";
        Log.d(TAG, "SSID Received: " + wifiConf.SSID);
        switch(SecurityProtocol) {
            // WEP "security".
            case WEP:
                wifiConf.wepKeys[0] = "\"" + networkPassword + "\"";
                wifiConf.wepTxKeyIndex = 0;
                wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;

            // WAP security. We have to set preSharedKey.
            case WAP:
                wifiConf.preSharedKey = "\""+ networkPassword +"\"";
                break;

            // Network without security.
            case OPEN_NETWORK:
                wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
        }

        // Add WiFi configuration to list of recognizable networks
        if ((networkId = wifiMgr.addNetwork(wifiConf)) == -1) {
            Log.d("TAG", "Failed to add network configuration!");
            return false;
        }

        // Disconnect from current WiFi connection
        if (!disconnectFromWifi()) {
            Log.d("TAG", "Failed to disconnect from network!");
            return false;
        }

        // Enable network to be connected
        if (!wifiMgr.enableNetwork(networkId, true)) {
            Log.d("TAG", "Failed to enable network!");
            return false;
        }

        // Connect to network
        if (!wifiMgr.reconnect()) {
            Log.d("TAG", "Failed to connect!");
            return false;
        }

        return true;
    }

    public boolean removeFromConfiguration(int id){
        boolean done = wifiMgr.removeNetwork(id);
        wifiMgr.saveConfiguration();
        return done;
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("WifiHelper", "Error checking internet connection", e);
            }
        } else {
            Log.d("WifiHelper", "No network available!");
        }
        return false;
    }
}

