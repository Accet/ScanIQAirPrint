package helperClasses;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;


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
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/ScanIQ Air/Scans";
    }

    public String getAbsoulteFilePathPrint()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/ScanIQ Air";
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

    public File[] getCompatibleFilesPrint()
    {
        File dir = new File(getAbsoulteFilePathPrint());

        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir1, String filename)
            { return filename.endsWith(".pdf"); }
        } );
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

    public boolean deletePrintedFile()
    {
        File[] filestoPrint = getCompatibleFilesPrint();
        if(filestoPrint.length != 0)
        {
            boolean temp = false;
            for (File tempFile : filestoPrint) {
                temp = new File(tempFile.getAbsolutePath()).getAbsoluteFile().delete();
            }
            return temp;
        }
        return false;
    }

}
