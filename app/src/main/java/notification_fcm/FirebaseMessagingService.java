package notification_fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import net.scaniq.scaniqairprint.MainActivity;
import net.scaniq.scaniqairprint.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Savan on 2016-10-18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public static String imageURL = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showNotification(remoteMessage.getData().get("message"));
        imageURL = remoteMessage.getData().get("link");

        Bundle b = new Bundle();
        b.putString("imageURl",imageURL);
        Log.i("Link ",""+imageURL);

        Intent intents=new Intent();
        intents.setAction("MyReceiver");
        getBaseContext().sendBroadcast(intents);
    }

    private void showNotification(String message) {

        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("ScanIQ Air")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }


}