package com.ashenone.dfp.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadUtil {

    public static List readLines(String path) {
        BufferedReader br = null;
        ArrayList lines = new ArrayList();
        File file = new File(path);
        try {
            br = new BufferedReader(new FileReader(file));
            while(true) {
                String line = br.readLine();
                if(line == null) {
                    break;
                }
                if(!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }
        catch(Exception e) {
        }finally {
            ReadUtil.close(br);
        }
        return lines;
    }

    public static void close(Closeable arg1) {
        if(arg1 != null) {
            try {
                arg1.close();
            }
            catch(Exception v0) {
            }
        }
    }
}
