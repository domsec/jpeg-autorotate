/**
 * MIT License
 *
 * Copyright (c) 2019 Domenic Seccareccia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * More information about this project is available at:
 *     https://github.com/domsec/jpeg-autorotate
 */

package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;

import java.awt.image.BufferedImage;

final class JpegImageTransform {

    /**
     * Not intended for instantiation.
     */
    private JpegImageTransform() {
        throw new IllegalStateException("Not intended for instantiation.");
    }

    /**
     * Attempts to automatically rotate {@code JpegImage} {@code image}.
     *
     * @param jpegImage
     *              An instance of {@code JpegImage}.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} is unable to be rotated.
     */
    protected static void rotateImage(JpegImage jpegImage) throws JpegAutorotateException {
        BufferedImage image = rotateAndFlip(jpegImage.getImage(), jpegImage.getMetadata().getOrientation());

        jpegImage.setImage(image);
    }

    /**
     * Attempts to automatically rotate {@code JpegImage} metadata {@code ExifThumbnail} image.
     *
     * @param metadata
     *              An instance of {@code JpegImageMetadata}.
     * @throws JpegAutorotateException
     *              In the event the {@code ExifThumbnail} image is unable to be rotated.
     */
    protected static void rotateThumbnail(JpegImageMetadata metadata) throws JpegAutorotateException {
        BufferedImage bufferedImage = rotateAndFlip(metadata.getThumbnail(), metadata.getOrientation());

        metadata.setThumbnail(bufferedImage);
    }

    /**
     * Attempts to determine and process the transformation (rotate and flip) required for the {@code JpegImage}.
     * Transformation is depends on the EXIF {@code Orientation} metadata tag value.
     * <p>
     * {@code JpegImage} may not contain the EXIF {@code Orientation} metadata tag.
     * </p>
     *
     * @param image
     *              A {@code BufferedImage} containing image data.
     * @param orientation
     *              A EXIF {@code Orientation} metadata tag value.
     * @return If successful, a {@code BufferedImage} containing transformed image data.
     * @throws JpegAutorotateException
     *            In the event the {@code Orientation} value is unknown.
     */
    private static BufferedImage rotateAndFlip(BufferedImage image, final int orientation) throws JpegAutorotateException {
        switch (orientation) {
            case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL:
                flipHorizontally(image);
                break;
            case TiffTagConstants.ORIENTATION_VALUE_ROTATE_180:
                flipVertically(image);
                flipHorizontally(image);
                break;
            case TiffTagConstants.ORIENTATION_VALUE_MIRROR_VERTICAL:
                flipVertically(image);
                break;
            case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_270_CW:
                image = rotate90CW(image);
                flipHorizontally(image);
                break;
            case TiffTagConstants.ORIENTATION_VALUE_ROTATE_90_CW:
                image = rotate90CW(image);
                break;
            case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_90_CW:
                image = rotate90CCW(image);
                flipHorizontally(image);
                break;
            case TiffTagConstants.ORIENTATION_VALUE_ROTATE_270_CW:
                image = rotate90CCW(image);
                break;
            default:
                throw new JpegAutorotateException("JPEG image has an unknown EXIF Orientation metadata tag.");
        }

        return image;
    }

    /**
     * Flips a {@code BufferedImage} vertically.
     *
     * @param image
     *              A {@code BufferedImage} containing image data.
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
     * Flips a {@code BufferedImage} horizontally.
     *
     * @param image
     *              A {@code BufferedImage} containing image data.
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
     * Rotates a {@code BufferedImage} 90 degrees clockwise.
     *
     * @param image
     *              A {@code BufferedImage} containing image data.
     * @return A {@code BufferedImage} rotated containing rotated image data.
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
     * Rotates a {@code BufferedImage} 90 degrees counter clockwise.
     *
     * @param image
     *              A {@code BufferedImage} containing image data.
     * @return A {@code BufferedImage} rotated containing rotated image data.
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