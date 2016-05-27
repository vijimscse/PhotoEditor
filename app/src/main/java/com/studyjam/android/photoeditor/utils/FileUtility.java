package com.studyjam.android.photoeditor.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Vijayalakshmi on 27-05-2016.
 */
public class FileUtility {

    public static File[] getSavedCollections() {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.FOLDER_NAME);
        File[] listOfFiles = folder.listFiles();

        return listOfFiles;
    }
}
