package com.sheepyang.sourceviewer.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by SheepYang on 2016/6/2 10:36.
 */
public class StreamUtil {
    public static String streamToString(InputStream is) {
        String result = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ( (len = is.read(buffer)) != -1) {
                baos.write(buffer,0,len);
                baos.flush();
                result = baos.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
