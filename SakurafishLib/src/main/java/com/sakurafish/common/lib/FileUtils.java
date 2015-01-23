package com.sakurafish.common.lib;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sakura on 2014/12/28.
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void saveImageFileInApp(Context con, InputStream is) {
        FileOutputStream fos = null;
        String outputFilePath = null;
        try {
            File outputDir = con.getExternalFilesDir("images");

            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, String.valueOf(System
                    .currentTimeMillis()));
            fos = new FileOutputStream(outputFile);
            copyLarge(is, fos);
            outputFilePath = outputFile.toString();
            Log.d(TAG, "outputFilePath:" + outputFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private FileUtils(){}
}
