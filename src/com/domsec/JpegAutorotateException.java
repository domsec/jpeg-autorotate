package com.domsec;

import java.io.File;

/**
 * An exception class thrown upon an unexpected fatal condition during the processing of a JPEG image.
 */
public class JpegAutorotateException extends Exception {

    private final File file;

    public JpegAutorotateException(String message, File file) {
        super(message);
        this.file = file;
    }

    public JpegAutorotateException(String message, File file, Throwable cause) {
        super(message, cause);
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

}
