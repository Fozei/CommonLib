package com.ewedo.libserver.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by fozei on 17-12-4.
 */

public class Utils {
    public static String convertIs2String(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
