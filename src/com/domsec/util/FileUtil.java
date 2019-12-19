package com.domsec.util;

import java.io.File;
import java.io.FilenameFilter;

public final class FileUtil {

    private FileUtil() {
        throw new IllegalStateException("Not intended for instantiation.");
    }

    /**
     * Attempts to determine if the file is acceptable based on its extension.
     */
    private static final FilenameFilter FILENAME_FILTER = (File dir, String name) -> {
        for(FileExtensions ext : FileExtensions.values()){
            if(name.toUpperCase().endsWith("." + ext)) {
                return true;
            }
        }

        return false;
    };

    /**
     * Attempts to determine if the file is acceptable.
     *
     * @param file File object providing a reference to a file
     *             that may be an acceptable image file.
     * @return true If the image file extension is acceptable;
     *              otherwise, false.
     */
    public static boolean isAcceptable(final File file) {
        return FILENAME_FILTER.accept(file, file.getName());
    }

}
