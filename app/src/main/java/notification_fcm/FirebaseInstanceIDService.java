package notification_fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.sql.Connection;

import helperClasses.DatabaseManager;
import helperClasses.SharedPreferencesManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import static net.scaniq.scaniqairprint.MainActivity.MYSQLRRuid;

/**
 * Created by Savan on 2016-10-18.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static String sharedToken;

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        sharedToken = token;
        Log.i("Token ",""+token);
        Log.i("RRuid",""+MYSQLRRuid);
        registerToken(token,this);
    }

    private void registerToken(String token,Context context) {
        Log.i("RRuid","registerToken"+token);

        if(!SharedPreferencesManager.getInstance(context).getScanFcmtoken().equals(token) && !MYSQLRRuid.equals("")) {
            SharedPreferencesManager.getInstance(context).setScanFcmtoken(token);
            Connection con = DatabaseManager.getInstance().getConnection(context);
            DatabaseManager.getInstance().executeStoreFCMTokenPreparedStatement(con, token, MYSQLRRuid, context);
            DatabaseManager.getInstance().closeConnection(con,context);
        }
    }
}
