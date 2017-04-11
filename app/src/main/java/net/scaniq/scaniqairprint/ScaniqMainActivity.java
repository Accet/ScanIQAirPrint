package net.scaniq.scaniqairprint;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;

import asyncTasks.AfterScanningAsyncTask;
import asyncTasks.PrintingTask;
import asyncTasks.ScanningSettings;
import helperClasses.DocumentDownloader;
import helperClasses.LocalFileManager;
import helperClasses.SharedPreferencesManager;
import helperClasses.WifiHelper;
import static net.scaniq.scaniqairprint.MainActivity.MYSQLRRuid;
import static net.scaniq.scaniqairprint.MainActivity.sharedInstance;
import static notification_fcm.FirebaseMessagingService.imageURL;

public class ScaniqMainActivity extends AppCompatActivity {

    private TextView ccEmailAddress = null;
    private TextView faxNumber = null;
    private TextView scaniqID = null;
    private Button cancelCCEmail = null;
    private Button cancelFax = null;
    private Button printBtn = null;
    private WifiHelper wifi;
    private FloatingActionsMenu fabMenu;
    public static final int PERMISSION_REQUEST_CODE = 123;
    private static final int SCANSNAP_REQ = 100;
    private String additionalEmail = "";
    private String validFaxNumber = "";
    public static String serialNumber = null;
    public static boolean allowed = true;
    private MyReceiver notificationReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scaniq_main);

        notificationReceiver = new MyReceiver();
        registerReceiver(notificationReceiver,new IntentFilter("MyReceiver"));

        Toolbar tool_bar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tool_bar);

        Log.i("Shared Token","->"+SharedPreferencesManager.getInstance(this).getScanFcmtoken());

        gatherAllControls(); //Wire all layout control to this activity
        setTextToLabels();
        //Check for permissions
        if(!(hasPermissions()))
        {
            requestPermissions();
        }
        wifi = new WifiHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("as", "gggg" +SharedPreferencesManager.getInstance(this).getSCAN_USER_SERIAl());
        if(!imageURL.equals(""))
        {
            if( printBtn == null )
            {
                printBtn = (Button) findViewById(R.id.printBtn);
            }
            printBtn.setEnabled(true);
        }
    }

    private void gatherAllControls() {
        scaniqID = (TextView) findViewById(R.id.scaniqID);
        ccEmailAddress = (TextView) findViewById(R.id.ccEmailAddress);
        faxNumber = (TextView) findViewById(R.id.faxNumber);
        cancelCCEmail = (Button) findViewById(R.id.cancelCCEmail);
        cancelFax = (Button) findViewById(R.id.cancelFax);
        printBtn = (Button) findViewById(R.id.printBtn);
        gatherFAB();
    }

    private void gatherFAB() {

        fabMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        final FloatingActionButton actionAddFax = (FloatingActionButton) findViewById(R.id.action_b);
        actionAddFax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapseImmediately();
                showFaxDialog();
            }
        });

        final FloatingActionButton actionAddEmail = (FloatingActionButton) findViewById(R.id.action_a);
        actionAddEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.collapseImmediately();
                showCCEmailDialog();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //***                         Functions for button clicked event: Start                        ***//
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void scanBtnClicked(View view)
    {
        new ScanningSettings(this).execute();
    }

    public void ccEmailCancelClicked(View view)
    {
        additionalEmail = "";
        ccEmailAddress.setText("");
        cancelCCEmail.setVisibility(View.INVISIBLE);
    }

    public void cancelFaxClicked(View view)
    {
        validFaxNumber = "";
        faxNumber.setText("");
        cancelFax.setVisibility(View.INVISIBLE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //***                          Functions for button clicked event: End                         ***//
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void showCCEmailDialog() {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.email_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Send a copy");
        alertDialogBuilder.setMessage("Enter the email address");

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.dialogCCEmailAddress);
        userInput.setHint("Enter email address");
        final TextView msg = (TextView) promptsView.findViewById(R.id.msg);


        // set dialog message
        alertDialogBuilder.setCancelable(true).setPositiveButton("Add",
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

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                String tempCCEMail = userInput.getText().toString().trim();
                if (tempCCEMail.equals(""))
                {
                    msg.setText("Email address must not be blank!");
                    msg.setVisibility(View.VISIBLE);
                    wantToCloseDialog = true;

                } else if (!tempCCEMail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    msg.setText("Please enter a valid email address");
                    msg.setVisibility(View.VISIBLE);
                    wantToCloseDialog = true;
                }
                else{
                    additionalEmail = tempCCEMail;
                    cancelCCEmail.setVisibility(View.VISIBLE);
                    ccEmailAddress.setVisibility(View.VISIBLE);
                    ccEmailAddress.setText("CC : "+additionalEmail);
                    wantToCloseDialog = false;
                }
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (!wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }

    public void showFaxDialog() {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.email_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Send a copy to a Fax");
        alertDialogBuilder.setMessage("Enter the fax number");

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.dialogCCEmailAddress);
        userInput.setInputType(InputType.TYPE_CLASS_PHONE);
        userInput.setHint("Enter fax number");

        final TextView msg = (TextView) promptsView.findViewById(R.id.msg);

        alertDialogBuilder.setCancelable(true).setPositiveButton("Add",
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
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                String tempFaxNumber = userInput.getText().toString().trim();
                if (tempFaxNumber.equals(""))
                {
                    msg.setText("Fax number must not be blank!");
                    msg.setVisibility(View.VISIBLE);
                    wantToCloseDialog = true;

                } else if (tempFaxNumber.length() < 9) {
                    msg.setText("Please enter a valid fax number");
                    msg.setVisibility(View.VISIBLE);
                    wantToCloseDialog = true;
                }
                else{
                    if(tempFaxNumber.length() < 11){
                        validFaxNumber = "1" + tempFaxNumber;
                    }
                    cancelFax.setVisibility(View.VISIBLE);
                    faxNumber.setVisibility(View.VISIBLE);
                    faxNumber.setText("Fax : "+validFaxNumber.replaceFirst("(\\d{1})(\\d{3})(\\d{3})(\\d+)", "(+$1) ($2) $3-$4"));
                    wantToCloseDialog = false;
                }
                if (!wantToCloseDialog)
                    alertDialog.dismiss();
            }
        });
    }

    private void setTextToLabels() {
        MYSQLRRuid = sharedInstance.getScaniqRrid();
        scaniqID.setText(sharedInstance.getSCAN_USER_SERIAl());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch ( requestCode )
        {
            case SCANSNAP_REQ:
                Log.i("Result After Scan","-> We're back in ScanIQ :) ");
                //Start sending files
                disconnectScanner();
                //Get the CC if entered.....
                File[] files = LocalFileManager.getInstance().getCompatibleFiles();
                if (files.length > 0) {
                    new AfterScanningAsyncTask(this).execute(additionalEmail,validFaxNumber);
                } else {
                    Toast.makeText(this,getString(R.string.no_files), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void disconnectScanner() {

        if (wifi.checkWifiEnabled()) {
            wifi.disconnectFromWifi();
            wifi.disableWifi();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //*** Functions for runtime permissions for android version "M" (Marshmallow) or above : Start ***//
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean hasPermissions()
    {
        int res = 0;

        String[] permissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.ACCESS_FINE_LOCATION};

        for ( String perms : permissions)
        {
            res = checkCallingOrSelfPermission(perms);

            if(!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions()
    {
        String[] permissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.ACCESS_FINE_LOCATION};

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
        {
            requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults) {
                    //If user grant all permissions
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                allowed = false;
                break;
        }
    }

    public void printDocument(View view) {
        new DocumentDownloader(this, new DocumentDownloader.AsyncResponse() {
            @Override
            public void processFinish(Boolean output) {
                new PrintingTask(ScaniqMainActivity.this, new PrintingTask.AsyncResponse() {
                    @Override
                    public void processFinish(Boolean output) {
                        printBtn.setEnabled(!output);
                    }
                });
            }
        }).execute(imageURL);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //**** Functions for runtime permissions for android version "M" (Marshmallow) or above : End ****//
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    public class MyReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent){
            printBtn.setEnabled(true);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
    }
}