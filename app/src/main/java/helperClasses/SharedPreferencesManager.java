package helperClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by savanpatel on 2017-02-08.
 */

public class SharedPreferencesManager {

    private static SharedPreferencesManager mInstance = null;
    private static SharedPreferences mPref = null;
    private SharedPreferences.Editor mEditor;
    private Context context = null;
    //All finals
    private static final String PREF_NAME = "MyPrefsFile";
    private static final String SCANIQ_EMAIL = "scaniq_email";
    private static final String SCANIQ_ACTIVE = "scaniq_active";
    private static final String SCANIQ_MAILTO = "scaniq_mailto";
    private static final String SCANIQ_RRID= "scaniq_rrid";
    private static final String SCANIQ_MD5 = "scaniq_md5";
    private static final String SCAN_LAT = "scan_latitude";
    private static final String SCAN_LON = "scan_longitude";
    private static final String SCAN_PROBILL = "probill";
    private static final String SCAN_FCMTOKEN = "fcmToken";
    private static final String SCAN_USER_SERIAl = "scan_user_serial";

    public SharedPreferencesManager(Context context) {
        this.context = context;
        mPref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }

    public static SharedPreferencesManager getInstance(Context context)
    {
        if( mInstance == null)
        {
            mInstance = new SharedPreferencesManager(context);
        }
        return  mInstance;
    }

    public static SharedPreferencesManager getInstance()
    {
        if( mInstance != null)
        {
            return mInstance;
        }
        throw new IllegalArgumentException("You should use getInstance(Context) at least once before using this method.");
    }

    ////////////////////////////////////////////////////////////////////////////
    //             Default methods used in this Singleton class               //
    ////////////////////////////////////////////////////////////////////////////


    private void doEdit() {
        mEditor = mPref.edit();
    }

    private void doCommit() {
        mEditor.commit();
        mEditor = null;
    }

    public void put(String key, String val) {
        doEdit();
        mEditor.putString(key, val);
        doCommit();
    }

    public void put(String key, int val) {
        doEdit();
        mEditor.putInt(key, val);
        doCommit();
    }

    public void put(String key, boolean val) {
        doEdit();
        mEditor.putBoolean(key, val);
        doCommit();
    }

    public void put(String key, float val) {
        doEdit();
        mEditor.putFloat(key, val);
        doCommit();
    }

    public String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    public String getString(String key) {
        return mPref.getString(key, null);
    }

    public int getInt(String key) {
        return mPref.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return mPref.getBoolean(key, true);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPref.getBoolean(key, true);
    }

    ////////////////////////////////////////////////////////////////////////////
    //      Custom getter and setter methods to use in other activities       //
    ////////////////////////////////////////////////////////////////////////////

    //ScanIQ Email
    public boolean getScaniqEmail() {
        return mInstance.getBoolean(SCANIQ_EMAIL,true);
    }

    public void setScaniqEmail(Boolean value) {
        mInstance.put(SCANIQ_EMAIL, value);
    }

    //ScanIQ Email Activation
    public int getScaniqActive() {
        return mInstance.getInt(SCANIQ_ACTIVE,0);
    }

    public void setScaniqActive(int value){
        mInstance.put(SCANIQ_ACTIVE, value);
    }

    //ScanIQ Email To
    public String getScaniqMailto() {
        return mInstance.getString(SCANIQ_MAILTO,"");
    }

    public void setScaniqMailto(String value){
        mInstance.put(SCANIQ_MAILTO, value);
    }

    //ScanIQ RRID
    public String getScaniqRrid() {
        return  mInstance.getString(SCANIQ_RRID,"");
    }

    public void setScaniqRrid(String value) {
        mInstance.put(SCANIQ_RRID, value);
    }

    //ScanIQ MD5
    public String getScaniqMd5() {
        return mInstance.getString(SCANIQ_MD5,"");
    }

    public void setScaniqMd5(String value){
        mInstance.put(SCANIQ_MD5, value);
    }

    public double getScanLat()
    {
        return Double.parseDouble(mInstance.getString(SCAN_LAT,"0"));
    }

    public void setScanLat(String value)
    {
        mInstance.put(SCAN_LAT, value);
    }

    public double getScanLon()
    {
        return Double.parseDouble(mInstance.getString(SCAN_LON,"0"));
    }

    public void setScanLon(String value)
    {
        mInstance.put(SCAN_LON, value);
    }

    public String getScanProbill() {
        return mInstance.getString(SCAN_PROBILL,"");
    }

    public void setScanProbill(String value){
        mInstance.put(SCAN_PROBILL, value);
    }

    public  String getScanFcmtoken() {
        return mInstance.getString(SCAN_FCMTOKEN,"") ;
    }

    public void setScanFcmtoken(String value){
        mInstance.put(SCAN_FCMTOKEN, value);
    }

    public String getSCAN_USER_SERIAl()
    {
        return mInstance.getString(SCAN_USER_SERIAl,"");
    }

    public void setSCAN_USER_SERIAl(String value)
    {
        mInstance.put(SCAN_USER_SERIAl,value);
    }
}
