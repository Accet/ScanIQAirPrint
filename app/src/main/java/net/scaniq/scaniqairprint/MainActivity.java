package net.scaniq.scaniqairprint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import asyncTasks.NewUserEmail;
import helperClasses.AlertBoxBuilder;
import helperClasses.ConfirmationEmailManager;
import helperClasses.DatabaseManager;
import helperClasses.SharedPreferencesManager;
import helperClasses.WifiHelper;

import static net.scaniq.scaniqairprint.ScaniqMainActivity.PERMISSION_REQUEST_CODE;

public class MainActivity extends AppCompatActivity{

    //Controls
    private EditText emailTextField = null;
    private TextView messageTextView = null;
    private TextView enterEmailText = null;
    private Button registrationBtn = null;

    //Shared Preferences Singleton Instance
    public static SharedPreferencesManager sharedInstance = null;
    //Database Manager Helper Singleton Instance
    DatabaseManager mDbManager = null;

    //Extras
    public static int scaniq_active = -1;
    public static String MYSQLRRuid = "";
    public static boolean isSharedPreferencesStored = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if(!(hasPermissions()))
        {
            requestPermissions();
        }
        sharedInstance = SharedPreferencesManager.getInstance(this);
        mDbManager = DatabaseManager.getInstance();

        gatherAllControls(); //Wire all layout control to this activity
//        checkUserRegistration();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkShared();
    }

    private String getValidEmailFromEmailTextField()
    {
        String emailAddress = emailTextField.getText().toString();
        if (emailAddress.trim().equals("") || !emailAddress.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))
        {
            Toast.makeText(getApplicationContext(), getString(R.string.enter_valid_email),Toast.LENGTH_SHORT).show();
        }
        return emailAddress;
    }
    //Connects layout controls to this activity
    private void gatherAllControls() {
        emailTextField = (EditText) findViewById(R.id.emailTextField);
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        enterEmailText = (TextView) findViewById(R.id.enter_email_text);
        registrationBtn = (Button) findViewById(R.id.registrationBtn);
    }

    private void checkShared() {
        if( !sharedInstance.getScaniqMailto().equals("")) {
            if (sharedInstance.getScaniqActive() == 1){
                launchScanning();
            } else {
                checkUserRegistrationClick(sharedInstance.getScaniqMailto());
            }
        }
    }
    //Checks user exist or not and if not the do registration
    private void checkUserRegistrationClick(String userEmail){

        if( sharedInstance.getScaniqMailto().equals(""))
        {
            Connection con = mDbManager.getConnection(this);
            ResultSet userInfo = userExistInDatabase(con, userEmail);
            try {
                if (userInfo.next()) {
                    String user_rrid = userInfo.getString(1);
                    String user_md5 = userInfo.getString(2);
                    String user_mailTo = userInfo.getString(3);
                    int user_isActive = userInfo.getInt(4);
                    sharedInstance.setScaniqMailto(user_mailTo);
                    sharedInstance.setScaniqMd5(user_md5);
                    sharedInstance.setScaniqRrid(user_rrid);
                    if (user_isActive != 1) {
                        resendEmail(user_md5);
                    } else {
                        sharedInstance.setScaniqActive(userInfo.getInt(4));
                        mDbManager.closeConnection(con,this);
                        launchScanning();
                    }
                } else {
                    new NewUserEmail(this).execute(userEmail, "", null);
                    sharedInstance.setScaniqMailto(userEmail);
                    getEmailNotif(getString(R.string.registration_done),getString(R.string.register_msg),getString(R.string.ok_btn),"").show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            } finally {
                mDbManager.closeConnection(con,this);
            }
        } else if (sharedInstance.getScaniqActive() != 1) {
                Connection con = mDbManager.getConnection(this);
                ResultSet userInfo = userExistInDatabase(con, userEmail);
                try {
                    if (userInfo.next()) {
                        if (userInfo.getInt(4) != 1) {
                            String user_md5 = userInfo.getString(2);
                            resendEmail(user_md5);
                        } else {
                            sharedInstance.setScaniqActive(userInfo.getInt(4));
                            mDbManager.closeConnection(con,this);
                            launchScanning();
                        }

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    mDbManager.closeConnection(con,this);
                }
        } else {
            launchScanning();
        }
    }



    private void launchScanning() {
        Intent intent = new Intent(this,ScaniqMainActivity.class);
        finish();
        startActivity(intent);
    }

    private void resendEmail(String md5) {
        getEmailNotif("Activation Required","Please confirm your account activation!\nDo you want to resend the confirmation email?" ,"Yes","Cancel").show();
//        enterEmailText.setText("We are validating your email address. Please check your email " +
//                    mailto + " for further instructions.");
    }

    private ResultSet userExistInDatabase(Connection con, String userEmail) {
        String query = "SELECT RR_ID, RR_MD5, RR_mailTO, RR_ActiveMobile FROM RR_Settings WHERE RR_mailTO = \"" + userEmail + "\" ORDER BY RR_ID DESC LIMIT 1 ; ";
        Log.i("Q","->"+query);
        ResultSet userInfo = mDbManager.executeSelecteQuery(con, query,this);

        return userInfo;
    }
    //Registration Button clicked event
    public void registrationClicked(View view){
        //User is new, create a new account
        WifiHelper wifiHelper = new WifiHelper(this);
        if (wifiHelper.hasActiveInternetConnection(this))
        {
            String emailAddress = getValidEmailFromEmailTextField();
            if(!emailAddress.equals(""))
            {
                String nemail = emailAddress.trim();
                checkUserRegistrationClick(nemail);
        //            new NewUserEmail(this).execute(nemail, "", null);
        //            sharedInstance.setScaniqMailto(nemail);
        //            getEmailNotif().show();
            }
        }
        else
        {
            AlertBoxBuilder.AlertBox(this,"Oops!","Please check your internet connection.");
        }
    }

    //Dialog for alert of registration
    public AlertDialog.Builder getEmailNotif(String title, String msg, final String positiveButton, String negativeButton){

        final AlertDialog.Builder emailNotif = new AlertDialog.Builder(this);

        emailNotif.setTitle(title);
        emailNotif.setMessage(msg);
        emailNotif.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(positiveButton.equals("Yes"))
                {
                    MYSQLRRuid = sharedInstance.getScaniqRrid();
                    String userEmail = sharedInstance.getScaniqMailto();
                    ConfirmationEmailManager.getInstance().sendMail(userEmail, MYSQLRRuid);
                }
            }
        });
        if (!negativeButton.equals(""))
        {
            emailNotif.setNegativeButton(negativeButton,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
        }
        return emailNotif;
    }

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
}
