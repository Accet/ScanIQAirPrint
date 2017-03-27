package helperClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.util.ArrayList;


public class BarcodeProcessor {

    private static final String LOG_TAG = "BarcodeProcessor";
    private Context context;

    public BarcodeProcessor(Context context) {
        this.context = context.getApplicationContext();
    }

    public String scanForBarcodes(File file) {
        ArrayList<Bitmap> bitmaps = pdfToBitmap(file);
        String barcodeText = "";

        for (Bitmap bitmap : bitmaps) {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                    .build();
            if(barcodeDetector.isOperational()){
                SparseArray<Barcode> sparseArray = barcodeDetector.detect(frame);
                if(sparseArray != null && sparseArray.size() > 0){
                    for (int i = 0; i < sparseArray.size(); i++){
                        Log.d(LOG_TAG, "Value: " + sparseArray.valueAt(i).rawValue + "----" + sparseArray.valueAt(i).displayValue);
                        barcodeText = sparseArray.valueAt(i).rawValue;
                    }
                    break;
                }else {
                    Log.e(LOG_TAG,"SparseArray null or empty");
                }

            }else{
                Log.e(LOG_TAG, "Detector dependencies are not yet downloaded");
            }
        }

        return barcodeText;
    }

    private ArrayList<Bitmap> pdfToBitmap(File pdfFile) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            try {
                PdfRenderer renderer;
                renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));

                Bitmap bitmap;
                final int pageCount = renderer.getPageCount();
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);

                    int width = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                    int height = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    bitmaps.add(bitmap);

                    // close the page
                    page.close();

                }

                // close the renderer
                renderer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

        }

        return bitmaps;

    }
}
