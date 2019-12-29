package com.domsec;

/**
 * An exception class thrown upon an unexpected fatal condition during the processing of a JPEG image.
 */
public class JpegAutorotateException extends Exception {

    public JpegAutorotateException(String message) {
        super(message);
    }

    public JpegAutorotateException(String message, Throwable cause) {
        super(message, cause);
    }

}
