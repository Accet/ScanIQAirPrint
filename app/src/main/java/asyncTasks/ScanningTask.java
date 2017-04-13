package asyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.print.PrintAttributes;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.mobile.scan.sdk.AdfException;
import com.hp.mobile.scan.sdk.ScanCapture;
import com.hp.mobile.scan.sdk.ScanTicketValidator;
import com.hp.mobile.scan.sdk.Scanner;
import com.hp.mobile.scan.sdk.ScannerException;
import com.hp.mobile.scan.sdk.browsing.ScannersBrowser;
import com.hp.mobile.scan.sdk.model.Resolution;
import com.hp.mobile.scan.sdk.model.ScanPage;
import com.hp.mobile.scan.sdk.model.ScanTicket;
import com.hp.mobile.scan.sdk.model.ScanValues;
import com.hp.mss.hpprint.model.PDFPrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.asset.PDFAsset;
import com.hp.mss.hpprint.util.PrintUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import helperClasses.AlertBoxBuilder;
import helperClasses.LocalFileManager;
import helperClasses.Setting;

import static net.scaniq.scaniqairprint.ScaniqMainActivity.serialNumber;

/**
 * Created by savanpatel on 2017-04-12.
 */

public class ScanningTask extends AsyncTask<String, String, String> {

    private ScanTicket mScanTicket;


    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private ScanningTask.AsyncResponse asyncResponse = null;

    public Context context;
    private ProgressDialog dialog;
//    private LocalFileManager mLocalFileManager;
    public static String TAG = "Scan";
    private Scanner mScanner;
    private OnScannerSelectedListener mListener;

    private ScannersBrowser mScannerBrowser;
    private String additionalEmail;
    private String validFaxNumber;

    public ScanningTask(Context context, ScanningTask.AsyncResponse asyncResponse) {
        this.context = context;
        this.asyncResponse = asyncResponse;
//        mScanTicket = new ScanTicket()
        mScannerBrowser = new ScannersBrowser(context.getApplicationContext());

////////////////////////////



////////////////////////////

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        dialog.setMessage("Please wait a moment\nScaning...");
        dialog.show();

    }

    @Override
    protected String doInBackground(String... params) {

        if(params.length>0) {
            additionalEmail = params[0];
            validFaxNumber = params[1];
        }

        Log.i(TAG,"->doIn - Start");
        File theExternalStorageDirectory = Environment.getExternalStorageDirectory();
        final File theScan_demo = new File(theExternalStorageDirectory, "ScanIQ Air/Scans");
        theScan_demo.mkdirs();

        ScannersBrowser.ScannerAvailabilityListener mAvailabilityListener
                = new ScannersBrowser.ScannerAvailabilityListener() {
            @Override
            public void onScannerFound(Scanner aScanner) {
                mScanner = aScanner;
                Log.i(TAG, "Scanner found ->" + aScanner.getIdentifier());
                serialNumber = aScanner.getIdentifier();
                mScanTicket = ScanTicket.createWithPreset(ScanTicket.SCAN_PRESET_TEXT_AND_IMAGES);
                mScanTicket.setSetting(ScanTicket.SCAN_SETTING_CONTENT_TYPE,"Auto");
                mScanTicket.setSetting(ScanTicket.SCAN_SETTING_FORMAT,1);
                mScanTicket.setSetting(ScanTicket.SCAN_SETTING_RESOLUTION,new Resolution(600,600));

                mScanner.validateTicket(mScanTicket, new ScanTicketValidator.ScanTicketValidationListener() {
                    @Override
                    public void onScanTicketValidationComplete(ScanTicket aValidScanTicket) {
                        Log.i(TAG,"Validation Complete");
                    }

                    @Override
                    public void onScanTicketValidationError(ScannerException aException) {
                        Log.i(TAG,"Ticket Error");
                    }
                });

                mScanner.scan(theScan_demo.getAbsolutePath(), mScanTicket, mScanProgressListener);
                mScannerBrowser.stop();
            }

            @Override
            public void onScannerLost(Scanner aScanner) {
                Log.i(TAG, "Connection Lost");

                //        updateCounter();
            }

        };
        mScannerBrowser.start(mAvailabilityListener);

        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        dialog.dismiss();
        asyncResponse.processFinish(true);
        Log.i(TAG,"->Post-Exec");
    }

    public interface OnScannerSelectedListener {
        void onScannerSelected(Scanner aScanner);
    }

    private ScanCapture.ScanningProgressListener mScanProgressListener =
            new ScanCapture.ScanningProgressListener() {

                @Override
                public void onScanningPageDone(final ScanPage aScanPage) {
                    Log.i(TAG, "onScanningPageDone: " + aScanPage);

//                    mScanResultAdapter.add(aScanPage);
                }

                @Override
                public void onScanningComplete() {
                    Log.i(TAG, "onScanningComplete: ");
                    dialog.dismiss();
                    new AfterScanningAsyncTask(context).execute(additionalEmail,validFaxNumber);
                }

                @Override
                public void onScanningError(final ScannerException aException) {
                    Log.i(TAG, "onScanningError: ", aException);
                    dialog.dismiss();
                    AlertBoxBuilder.AlertBox(context,"Failure","Failed to scan document.\nPlease try again...");
                    closeSession();
                }
            };

    private void closeSession() {
        if (mScanner != null) {
            mScanner.cancelScanning();
        }
    }


//****************************************************************//
//****************************************************************//

}
