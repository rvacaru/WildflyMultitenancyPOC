package com.raz.poc.multitenancy.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {
    
    public static void write(String path, String msg) {
        try {
            if(Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS))
                Files.write(Paths.get(path), msg.getBytes(), StandardOpenOption.APPEND);
            else
                Files.write(Paths.get(path), msg.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, "Error while trying to write to file", ex);
        }
    }
}
