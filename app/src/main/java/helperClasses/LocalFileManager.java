package helperClasses;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by savanpatel on 2017-02-22.
 */

public class LocalFileManager {

    private static LocalFileManager localFileManager = null;
    public File[] globalFileArray = null;

    public static LocalFileManager getInstance(){
        if(localFileManager == null)
        {
            localFileManager = new LocalFileManager();
        }
        return localFileManager;
    }

    public String getAbsoulteFilePath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/ScanSnap";
    }

    public int getFilesCount() {
        File[] localFiles = getCompatibleFiles();
        return localFiles.length;
    }

    public File[] getCompatibleFiles()
    {
        File dir = new File(getAbsoulteFilePath());

        File[] fileabsolute = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename)
            { return filename.endsWith(".pdf"); }
        } );

        globalFileArray = fileabsolute;

        return fileabsolute;
    }

    public boolean deleteFile()
    {
        if(globalFileArray.length != 0)
        {
            boolean temp = false;
            File[] filestoSend = globalFileArray;
            for (File tempFile : filestoSend) {
                temp = new File(tempFile.getAbsolutePath()).getAbsoluteFile().delete();
            }
            return temp;
        }
        return false;
    }

}
