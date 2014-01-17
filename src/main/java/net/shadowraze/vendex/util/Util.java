package net.shadowraze.vendex.util;

import java.io.File;
import java.io.IOException;

public class Util {

    public static void validateFile(File checkFile) {
        if(checkFile.exists()) return;
        try {
            checkFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
