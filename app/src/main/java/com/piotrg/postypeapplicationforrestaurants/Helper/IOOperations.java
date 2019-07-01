package com.piotrg.postypeapplicationforrestaurants.Helper;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class IOOperations {
    private static final String TAG = "IOOperations";

    public static void writeToFile(Context ctx, String filename, String fileContent) {
        try {
            FileOutputStream outputStream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
            Log.d(TAG, "Finished writing to file: " + filename + " " + fileContent.substring(0, 10) + "...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(Context ctx, String filename){
        String result = "fail";
        try{
            FileInputStream inputStream = ctx.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while((lines = bufferedReader.readLine()) != null){
                stringBuffer.append(lines+"\n");
            }

            result = stringBuffer.toString();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }
}
