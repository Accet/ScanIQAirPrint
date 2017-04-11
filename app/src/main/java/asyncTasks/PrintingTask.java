package asyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.print.PrintAttributes;
import android.util.Log;

import com.hp.mss.hpprint.model.PDFPrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.asset.PDFAsset;
import com.hp.mss.hpprint.util.PrintUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import helperClasses.LocalFileManager;

/**
 * Created by Nihal on 2017-04-11.
 */

public class PrintingTask extends AsyncTask<String, String, String> {

    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private AsyncResponse asyncResponse = null;

    public ProgressDialog pDialog;
    public Context context;
    private ProgressDialog dialog;
    private LocalFileManager mLocalFileManager;
    public static String CONTENT_TYPE_PDF = "PDF";
    public static String CONTENT_TYPE_IMAGE = "Image";
    public static String MIME_TYPE_PDF = "application/pdf";
    public static String MIME_TYPE_IMAGE = "image/*";
    public static String MIME_TYPE_IMAGE_PREFIX = "image/";
    public static String TAG = "TabFragmentPrintLayout";

    String contentType;
    PrintItem.ScaleType scaleType;
    PrintAttributes.Margins margins;
    PrintAttributes.MediaSize mediaSize5x7;
    PrintJobData printJobData;

    public PrintingTask(Context context, AsyncResponse asyncResponse) {
        this.context = context;
        this.asyncResponse = asyncResponse;
        mLocalFileManager = LocalFileManager.getInstance();
        scaleType = PrintItem.ScaleType.CENTER_TOP;
        margins = new PrintAttributes.Margins(0, 0, 0, 0);
        contentType = CONTENT_TYPE_PDF;
        PrintUtil.doNotEncryptDeviceId = true;


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        dialog.setMessage("Please wait a moment\nPrinting...");
        dialog.show();

    }

    @Override
    protected String doInBackground(String... params) {
        File[] filesToPrint = mLocalFileManager.getCompatibleFilesPrint();
//        String additionalEmail = params[0];
        if (filesToPrint.length > 0) {
            for (File file : filesToPrint) {
                createUserSelectedPDFJobData(file);
                PrintUtil.setPrintJobData(printJobData);
            }
            PrintUtil.print((Activity) context);

        }

        return null;
    }

    private void createUserSelectedPDFJobData(File fileToPrint) {
        try {
            FileInputStream input = new FileInputStream(fileToPrint);
//            InputStream input=context.getContentResolver().openInputStream(userPickedUri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "No File", e);
        }

        PDFAsset pdfAsset = new PDFAsset(fileToPrint.getAbsolutePath(), false);

        PrintItem printItem4x6 = new PDFPrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6, margins, scaleType, pdfAsset);
        PrintItem printItem5x7 = new PDFPrintItem(mediaSize5x7, margins, scaleType, pdfAsset);
        PrintItem printItemLetter = new PDFPrintItem(PrintAttributes.MediaSize.NA_LETTER, margins, scaleType, pdfAsset);

        printJobData = new PrintJobData(context, printItem4x6);
        printJobData.addPrintItem(printItemLetter);
        printJobData.addPrintItem(printItem5x7);

        printJobData.setJobName("ScanIQ");
        //Optionally include print attributes.
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .build();
        printJobData.setPrintDialogOptions(printAttributes);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialog.dismiss();

        mLocalFileManager.deleteFile();
        asyncResponse.processFinish(true);

    }
}
