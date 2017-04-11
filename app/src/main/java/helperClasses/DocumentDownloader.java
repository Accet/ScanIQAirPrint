package helperClasses;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static notification_fcm.FirebaseMessagingService.imageURL;

public class DocumentDownloader extends AsyncTask<String, String, String>
{
    public interface AsyncResponse {
        void processFinish(Boolean output, String absoluteFilePath);
    }

    AsyncResponse asyncResponse = null;

    public ProgressDialog pDialog;
    public Context context;
    private WifiHelper wifi;
    private ProgressDialog dialog;
    public static String fileName = "";
    public DocumentDownloader(Context context, AsyncResponse asyncResponse) {
        this.context = context;
        this.asyncResponse = asyncResponse;

    }

    protected Dialog showDialog(int id) {
        switch (id) {
            case 0:
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        wifi = new WifiHelper(context.getApplicationContext());

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//        dialog.setMessage("Please wait a moment\nConecting to Internet...");
//        dialog.show();
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;

        if(!wifi.hasActiveInternetConnection(context.getApplicationContext())) {
            try {
                //check if connected!
                while (!wifi.hasActiveInternetConnection(context.getApplicationContext())) {
                    //Wait to connect
                    Thread.sleep(500);
                    Log.i("Connect","...ing");
                    publishProgress("Waiting for active internet connection...");
                }
            } catch (Exception e) {
                publishProgress("...");
            }

        }

        publishProgress("Connected");

        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            File filepath = Environment.getExternalStorageDirectory();

            File dir = new File(filepath.getAbsolutePath()+"/ScanIQ Air");

            dir.mkdirs();

            fileName = URLUtil.guessFileName(f_url[0],null,null);
            Log.i("F name ",""+fileName);
            File file = new File(dir,fileName);

            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress(""+(int)((total*100)/lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return "Done";
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        switch(progress[0])
        {
            case "Waiting for active internet connection...":
                dialog.setMessage("Waiting for active internet connection...");
                dialog.show();
                break;
            case "Connected": dialog.dismiss();
                showDialog(0);
                break;

            default:
                pDialog.setProgress(Integer.parseInt(progress[0]));
        }

    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
//        completed = true;
        imageURL = "";
        pDialog.dismiss();
        asyncResponse.processFinish(true, Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScanIQ Air/"+fileName);

//        AlertBoxBuilder.AlertBox(context,"Download Complete","File is stored in\n \"internal storage/ScanIQ Air\"");

        // Displaying downloaded image into image view
        // Reading image path from sdcard
//        String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
        // setting downloaded into image view
    }


}
