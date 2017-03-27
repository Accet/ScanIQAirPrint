package helperClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by savanpatel on 2017-03-23.
 */

public class AlertBoxBuilder {

    public static void AlertBox(Context context, final String title, final String message)
    {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
        alertbox.setTitle(title);
        alertbox.setCancelable(false);
        alertbox.setMessage(message);
        alertbox.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertbox.show();
    }


}
