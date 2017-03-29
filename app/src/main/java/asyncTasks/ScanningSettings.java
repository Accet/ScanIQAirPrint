package asyncTasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.scaniq.scaniqairprint.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import helperClasses.AlertBoxBuilder;
import helperClasses.DatabaseManager;
import helperClasses.SharedPreferencesManager;
import helperClasses.WifiHelper;


public class ScanningSettings extends AsyncTask<String, String, String> {

    private Context context;
    private ProgressDialog dialog;
    private WifiHelper wifiHelper;
    private int isProbillNumberActive;

    public ScanningSettings(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        dialog.setMessage("Please wait a moment\nChecking settings...");

        wifiHelper = new WifiHelper(context.getApplicationContext());
        dialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        if(wifiHelper.hasActiveInternetConnection(context.getApplicationContext())) {
            DatabaseManager dbManager = DatabaseManager.getInstance();

            Connection dbCon = dbManager.getConnection(context);
            ResultSet result = dbManager.executeSelecteQuery(dbCon,
                    "SELECT RR_probillMode FROM RR_Settings WHERE RR_ID = "
                            + SharedPreferencesManager.getInstance(context).getScaniqRrid(),
                    context);
            try {
                while (result.next()) {
                    isProbillNumberActive = result.getInt("RR_probillMode");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                publishProgress("Error");
            }
            if (isProbillNumberActive == 1) {
                publishProgress("probill");
            }
        } else {
            publishProgress("No connection");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        String updates = values[0];

        switch (updates)
        {
            case "No connection":
                dialog.dismiss();
                AlertBoxBuilder.AlertBox(context,"Error","No Internet connection.");
                break;
            case "Error":
                dialog.dismiss();
                AlertBoxBuilder.AlertBox(context,"Error","Try again please.");
                break;
            case "probill":
                dialog.dismiss();
                String probillNumber = showIDDialog();
                if (probillNumber != null && !probillNumber.equals("")) {
                    new WirelessScannerAsyncTask(context).execute();
                }
                break;
            default:
                break;
        }
    }

    private String showIDDialog() {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.email_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Info");
        alertDialogBuilder.setMessage("Enter the ID");
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.dialogCCEmailAddress);
        userInput.setHint("Document ID:");
        final TextView msg = (TextView) promptsView.findViewById(R.id.msg);

        // set dialog message
        alertDialogBuilder.setCancelable(true).setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        final String[] probillNumber = new String[1];
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog;
                probillNumber[0] = userInput.getText().toString().trim();
                if (probillNumber[0].equals(""))
                {
                    msg.setText("ID must not be blank!");
                    msg.setVisibility(View.VISIBLE);
                    wantToCloseDialog = true;

                }
                else{
                    SharedPreferencesManager.getInstance(context).setScanProbill(probillNumber[0]);
                    wantToCloseDialog = false;
                }
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (!wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
        return probillNumber[0];
    }
}
