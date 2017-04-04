package notification_fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

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
        registerToken(token);
    }

    private void registerToken(String token) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token",token)
                .add("Unit",MYSQLRRuid)
                .build();

        Request request = new Request.Builder()
                .url("http://scaniq.secureserverdot.com/FCM/register_FCMToken.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}