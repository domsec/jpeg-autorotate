package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import java.awt.image.BufferedImage;

final class JpegImageTransform {

    /**
     * Not intended for instantiation.
     */
    private JpegImageTransform() {
        throw new IllegalStateException("Not intended for instantiation.");
    }

    /**
     * Attempts to automatically rotate JPEG image.
     *
     * @param jpegImage
     *              A valid JpegImage object reference.
     * @throws JpegAutorotateException
     *              In the event the buffered image of the JpegImage object is
     *              unable to be rotated.
     */
    protected static void rotateImage(JpegImage jpegImage) throws JpegAutorotateException {
        BufferedImage image = rotateAndFlip(jpegImage.getImage(), jpegImage.getMetadata().getOrientation());

        jpegImage.setImage(image);
    }

    /**
     * Attempts to automatically rotate JPEG thumbnail image.
     *
     * @param metadata
     *              A valid JpegImageMetadata object reference
     * @throws JpegAutorotateException
     *              In the event the buffered thumbnail image of JpegImageMetadata object
     *              is unable to be rotated.
     */
    protected static void rotateThumbnail(JpegImageMetadata metadata) throws JpegAutorotateException {
        BufferedImage bufferedImage = rotateAndFlip(metadata.getThumbnail(), metadata.getOrientation());

        metadata.setThumbnail(bufferedImage);
    }

    /**
     * Attempts to determine and process the transformation (rotate and flip) required for the JPEG image.
     * Transformation is based on the provided EXIF <code>Orientation</code> metadata tag value.
     * <p>
     * Image may potentially either already be set to the correct orientation or not contain
     * the EXIF <code>Orientation</code> metadata tag. In such an event, an exception will be
     * thrown and the JPEG file will not be processed.
     * </p>
     *
     * @param image
     *              A valid buffered image object.
     * @param orientation
     *              A valid EXIF <code>Orientation</code> metadata tag value
     * @return If successful, a valid buffered image.
     * @throws JpegAutorotateException
     *            In the event the JPEG file either has an unknown EXIF <code>Orientation</code> metadata
     *            tag value or the value is already set to 1 (JPEG image is already correctly orientated).
     */
    private static BufferedImage rotateAndFlip(BufferedImage image, int orientation) throws JpegAutorotateException {
        switch (orientation) {
            case 1:
                throw new JpegAutorotateException("JPEG image orientation is already correct.");
            case 2:
                flipHorizontally(image);
                break;
            case 3:
                flipVertically(image);
                flipHorizontally(image);
                break;
            case 4:
                flipVertically(image);
                break;
            case 5:
                image = rotate90CW(image);
                flipHorizontally(image);
                break;
            case 6:
                image = rotate90CW(image);
                break;
            case 7:
                image = rotate90CCW(image);
                flipHorizontally(image);
                break;
            case 8:
                image = rotate90CCW(image);
                break;
            default:
                throw new JpegAutorotateException("JPEG image has an unknown EXIF Orientation metadata tag.");
        }

        return image;
    }

    /**
     * Flips buffered image vertically.
     *
     * @param image
     *              A valid buffered image object.
     */
    private static void flipVertically(BufferedImage image) {
        for(int i = 0; i < image.getHeight()/2; i++) {
            int[] upperRow = image.getRGB(0, i, image.getWidth(), 1, null, 0, image.getWidth());
            int[] lowerRow = image.getRGB(0, image.getHeight() - 1 - i, image.getWidth(), 1, null, 0, image.getWidth());

            image.setRGB(0, image.getHeight() - 1 - i, image.getWidth(), 1, upperRow, 0, image.getWidth());
            image.setRGB(0,i, image.getWidth(), 1, lowerRow, 0, image.getWidth());
        }
    }

    /**
     * Flips buffered image horizontally.
     *
     * @param image
     *              A valid buffered image object.
     */
    private static void flipHorizontally(BufferedImage image) {
        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth()/2; j++) {
                int temp = image.getRGB(j, i);

                image.setRGB(j, i, image.getRGB(image.getWidth()-(1+j), i));
                image.setRGB(image.getWidth()-(1+j), i, temp);
            }
        }
    }

    /**
     * Rotates buffered image 90 degrees clockwise
     *
     * @param image
     *              A valid buffered image object.
     * @return If successful, a valid buffered image.
     */
    private static BufferedImage rotate90CW(BufferedImage image) {
        BufferedImage tempImage = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());

        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                tempImage.setRGB(tempImage.getWidth()-1-i, j, image.getRGB(j, i));
            }
        }

        return tempImage;
    }

    /**
     * Rotates buffered image 90 degrees counter clockwise
     *
     * @param image
     *              A valid buffered image object.
     * @return If successful, a valid buffered image.
     */
    private static BufferedImage rotate90CCW(BufferedImage image) {
        BufferedImage tempImage = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());

        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                tempImage.setRGB(i, tempImage.getHeight()-1-j, image.getRGB(j, i));
            }
        }

        return tempImage;
    }

}