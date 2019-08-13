package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import java.awt.image.BufferedImage;

public class JpegImageTransform {

    private JpegImageTransform() {
        throw new IllegalStateException("Not intended for instantiation");
    }

    /**
     * Attempts to determine whether the current JPEG image EXIF metadata <code>Orientation</code>
     * tag stipulates the JPEG image needs to have its orientation corrected.
     * <p>
     * JPEG file may potentially either already be set to the correct orientation or not contain
     * the EXIF metadata <code>Orientation</code> tag. In such an event, an exception will be
     * thrown and the JPEG file will not be processed
     * </p>
     *
     * @param jpegImage
     *            JpegImage object providing reference to an object
     *            containing the JPEG image metadata needed for determining whether
     *            the orientation is correct or needs to be rotated.
     * @throws JpegAutorotateException
     *            In the event the JPEG file either has an unknown EXIF metadata <code>Orientation</code>
     *            tag value or the value is already set to 1 (JPEG image is already correctly orientated).
     */
    public static void rotateImage(JpegImage jpegImage) throws JpegAutorotateException {
        int orientation = jpegImage.getMetadata().getOrientation();

        switch (orientation) {
            case 1:
                throw new JpegAutorotateException("Image orientation is already correct", jpegImage.getFile());
            case 2:
                flipHorizontally(jpegImage);
                break;
            case 3:
                flipVertically(jpegImage);
                flipHorizontally(jpegImage);
                break;
            case 4:
                flipVertically(jpegImage);
                break;
            case 5:
                rotate90CW(jpegImage);
                flipHorizontally(jpegImage);
                break;
            case 6:
                rotate90CW(jpegImage);
                break;
            case 7:
                rotate90CCW(jpegImage);
                flipHorizontally(jpegImage);
                break;
            case 8:
                rotate90CCW(jpegImage);
                break;
            default:
                throw new JpegAutorotateException("Image has an unknown Orientation EXIF tag", jpegImage.getFile());
        }
    }

    /**
     * Flips JPEG image buffer vertically.
     *
     * @param jpegImage
     *            JpegImage object providing reference to an object
     *            containing the JPEG image buffer needing to be flipped.
     */
    private static void flipVertically(JpegImage jpegImage) {
        for(int i = 0; i < jpegImage.getHeight()/2; i++) {
            int[] upperRow = jpegImage.getBufferedImage().getRGB(0, i, jpegImage.getWidth(), 1, null, 0, jpegImage.getWidth());
            int[] lowerRow = jpegImage.getBufferedImage().getRGB(0, jpegImage.getHeight() - 1 - i, jpegImage.getWidth(), 1, null, 0, jpegImage.getWidth());

            jpegImage.getBufferedImage().setRGB(0, jpegImage.getHeight() - 1 - i, jpegImage.getWidth(), 1, upperRow, 0, jpegImage.getWidth());
            jpegImage.getBufferedImage().setRGB(0,i, jpegImage.getWidth(), 1, lowerRow, 0, jpegImage.getWidth());
        }
    }

    /**
     * Flips JPEG image buffer horizontally.
     *
     * @param jpegImage
     *            JpegImage object providing reference to an object
     *            containing the JPEG image buffer needing to be flipped.
     */
    private static void flipHorizontally(JpegImage jpegImage) {
        for(int i = 0; i < jpegImage.getHeight(); i++) {
            for(int j = 0; j < jpegImage.getWidth()/2; j++) {
                int temp = jpegImage.getBufferedImage().getRGB(j, i);

                jpegImage.getBufferedImage().setRGB(j, i, jpegImage.getBufferedImage().getRGB(jpegImage.getWidth()-(1+j), i));
                jpegImage.getBufferedImage().setRGB(jpegImage.getWidth()-(1+j), i, temp);
            }
        }
    }

    /**
     * Rotates JPEG image buffer 90 degrees clockwise.
     *
     * @param jpegImage
     *            JpegImage object providing reference to an object
     *            containing the JPEG image buffer needing to be rotated.
     */
    private static void rotate90CW(JpegImage jpegImage) {
        BufferedImage tempImage = new BufferedImage(jpegImage.getHeight(), jpegImage.getWidth(), jpegImage.getBufferedImage().getType());

        for(int i = 0; i < jpegImage.getHeight(); i++) {
            for(int j = 0; j < jpegImage.getWidth(); j++) {
                tempImage.setRGB(tempImage.getWidth()-1-i, j, jpegImage.getBufferedImage().getRGB(j, i));
            }
        }

        jpegImage.setBufferedImage(tempImage);
    }

    /**
     * Rotates JPEG image buffer 90 degrees counter clockwise.
     *
     * @param jpegImage
     *            JpegImage object providing reference to an object
     *            containing the JPEG image buffer needing to be rotated.
     */
    private static void rotate90CCW(JpegImage jpegImage) {
        BufferedImage tempImage = new BufferedImage(jpegImage.getHeight(), jpegImage.getWidth(), jpegImage.getBufferedImage().getType());

        for(int i = 0; i < jpegImage.getHeight(); i++) {
            for(int j = 0; j < jpegImage.getWidth(); j++) {
                tempImage.setRGB(i, tempImage.getHeight()-1-j, jpegImage.getBufferedImage().getRGB(j, i));
            }
        }

        jpegImage.setBufferedImage(tempImage);
    }
}