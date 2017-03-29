package asyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.util.Log;
import net.scaniq.scaniqairprint.ScaniqMainActivity;
import java.util.HashMap;
import java.util.List;
import helperClasses.ScanLocation;
import helperClasses.SharedPreferencesManager;
import helperClasses.WifiHelper;
import static net.scaniq.scaniqairprint.ScaniqMainActivity.allowed;
import static net.scaniq.scaniqairprint.ScaniqMainActivity.serialNumber;

public class WirelessScannerAsyncTask extends AsyncTask<String, String, String>{

    private Context context;
    private ProgressDialog dialog;
    private WifiHelper wifiHelper;
    private String SSID = "";
    private String networkPassword = "";
    private final String LAT = "latitude";
    private final String LON = "longitude";

    public WirelessScannerAsyncTask(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        SSID = "";
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        dialog.setMessage("Please wait a moment\nConnecting to Scanner...");

        context.getApplicationContext();
        wifiHelper = new WifiHelper(context);

        if (!wifiHelper.checkWifiEnabled()) {
            wifiHelper.enableWifi();
        }

        dialog.show();

    }
//TEST
    @Override
    protected String doInBackground(String... strings) {
        Log.i("Wifi"," doIn");

        List<ScanResult> wifiList;

        while (SSID.equals("") && networkPassword.equals("")) {
            wifiList = wifiHelper.getWifiInRange();
            if( wifiHelper.checkWifiEnabled() && allowed) {
                wifiHelper.scanWifiInRange();
            } else {
                Log.i("Error ","Either Wifi MGR null or Permissions not granted");
            }
            for (int i = 0; i < wifiList.size(); i++) {
                String tempSSID = wifiList.get(i).SSID;
                Log.i("Wifi", "wifiList.get(i).SSID ->" + wifiList.get(i).SSID);

                if ((tempSSID).contains("iX100")) {
                    SSID = tempSSID;
                    Log.i("Wifi", "getScanResults in if SSID ->" + SSID);

                    networkPassword = tempSSID.substring(6);
                    serialNumber = networkPassword;
                    break;
                }
            }
        }

        Log.i("Wifi ","wifiHelper.checkWifiEnabled() -> " + wifiHelper.checkWifiEnabled());



        if( SSID != null && networkPassword != null )
        {
//            WifiInfo wifiinfo = wifiHelper.getWifiManager(context).getConnectionInfo();
//            Log.i("Wifi", "wifiinfo.getSupplicantState() ->" + wifiinfo.getSupplicantState());
////            if (wifiinfo.getSupplicantState() == SupplicantState.COMPLETED)
////            {
////                Log.i("Wifi", "!wifiinfo.getSSID().contains(SSID) ->" + wifiinfo.getSSID().contains(SSID));
//
//                if ( !wifiinfo.getSSID().contains(SSID) ) {
//                    Log.i("Wifi", "wifiinfo.getSupplicantState() in 2nd if wifiinfo.getSSID() ->" + wifiinfo.getSSID());
//                    wifiHelper.disconnectFromWifi();
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            wifiHelper.connectToSelectedNetwork(this.SSID, this.networkPassword);

//            while (wifiinfo.getSupplicantState() != SupplicantState.COMPLETED) {
                try {
                    Log.i("Wifi", "sleep");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String lenghtOfFile) {
        // do stuff after posting data
        HashMap<String,Double> location = getLocationFromHelperClass();

        SharedPreferencesManager.getInstance(context).setScanLat(""+location.get(LAT));
        SharedPreferencesManager.getInstance(context).setScanLon(""+location.get(LON));

        Uri uri = Uri.parse("scansnap://" + this.SSID + "/Scan&Format=1&SaveTogether=0");
        Intent in = new Intent();
        in.setData(uri);
        in.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        serialNumber = this.SSID;
        this.SSID = "";
        this.networkPassword = "";
        ((ScaniqMainActivity) context).startActivityForResult(in,100);

        dialog.dismiss();
    }

    protected void onProgressUpdate(String... progress) {
    }

    private HashMap getLocationFromHelperClass()
    {
        HashMap<String,Double> location = new HashMap<>();
        ScanLocation locationHelper = new ScanLocation(context);
        if(locationHelper.canGetLocation()){

            double latitude = locationHelper.getLatitude();
            double longitude = locationHelper.getLongitude();
            location.put(LAT, latitude);
            location.put(LON, longitude);
        }else{
            locationHelper.showSettingsAlert();
        }
        return location;
    }

}