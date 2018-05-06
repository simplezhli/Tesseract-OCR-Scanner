package com.zl.tesseract.scanner.tess;

import android.content.Context;
import android.util.Log;

import com.zl.tesseract.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Fadi on 6/11/2014.
 */
public class TessDataManager {

    static final String TAG = "DBG_" + TessDataManager.class.getName();

    private static final String tessdir = "tesseract";
    private static final String subdir = "tessdata";
    private static final String filename = "eng.traineddata";

    private static String trainedDataPath;

    private static String tesseractFolder;

    public static String getTesseractFolder() {
        return tesseractFolder;
    }

    public static String getTrainedDataPath(){
        return initiated ? trainedDataPath : null;
    }

    private static boolean initiated;

    public static void initTessTrainedData(Context context){

        if(initiated){
            return;
        }

        File appFolder = context.getFilesDir();
        File folder = new File(appFolder, tessdir);
        if(!folder.exists()){
            folder.mkdir();
        }
            
        tesseractFolder = folder.getAbsolutePath();

        File subfolder = new File(folder, subdir);
        if(!subfolder.exists()){
            subfolder.mkdir();
        }

        File file = new File(subfolder, filename);
        trainedDataPath = file.getAbsolutePath();
        Log.d(TAG, "Trained data filepath: " + trainedDataPath);

        if(!file.exists()) {

            try {
                FileOutputStream fileOutputStream;
                byte[] bytes = readRawTrainingData(context);
                if (bytes == null){
                    return;
                }
                    
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
                initiated = true;
                Log.d(TAG, "Prepared training data file");
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Error opening training data file\n" + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "Error opening training data file\n" + e.getMessage());
            }
        }
        else{
            initiated = true;
        }
    }

    private static byte[] readRawTrainingData(Context context){

        try {
            InputStream fileInputStream = context.getResources()
                    .openRawResource(R.raw.eng_traineddata);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int bytesRead;

            while (( bytesRead = fileInputStream.read(b))!=-1){
                bos.write(b, 0, bytesRead);
            }

            fileInputStream.close();

            return bos.toByteArray();

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error reading raw training data file\n"+e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Error reading raw training data file\n" + e.getMessage());
        }

        return null;
    }

}
