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
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;

import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.IOException;

final class JpegImageMetadataReader {

    private JpegImageMetadataReader() {
        throw new IllegalStateException("Not intended for instantiation");
    }

    /**
     * Attempts to read {@code JpegImageMetadata}.
     *
     * @param bytes
     *              {@code bytes} containing a JPEG image file.
     * @return If successful, a valid {@code JpegImageMetadata} instance.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImageMetadata} either does not
     *              exist or is unable to be read.
     */
    protected static org.apache.commons.imaging.formats.jpeg.JpegImageMetadata readRawMetadata(final byte[] bytes) throws JpegAutorotateException {
        try {
            org.apache.commons.imaging.common.ImageMetadata imageMetadata = Imaging.getMetadata(bytes);

            if(imageMetadata == null) {
                throw new JpegAutorotateException("JPEG image does not have necessary metadata.");
            }

            return (org.apache.commons.imaging.formats.jpeg.JpegImageMetadata) imageMetadata;
        } catch(IOException | ImageReadException e) {
            throw new JpegAutorotateException("Unable to read JPEG image metadata.", e);
        }
    }

    /**
     * Attempts to read {@code TiffImageMetadata}.
     *
     * @param metadata
     *              An instance of {@code JpegImageMetadata}.
     * @return If successful, a valid {@code TiffImageMetadata} instance.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImageMetadata} is unable to be read.
     */
    protected static TiffImageMetadata readExif(final org.apache.commons.imaging.formats.jpeg.JpegImageMetadata metadata) throws JpegAutorotateException {
        if(metadata.getExif() == null) {
            throw new JpegAutorotateException("Unable to read JPEG image EXIF metadata.");
        }

        return metadata.getExif();
    }

    /**
     * Attempts to read {@code JpegImage} {@code TIFFOutputSet} from the {@code TiffImageMetadata}.
     * <p>
     * In the event the {@code JpegImage} does not contain a {@code TiffOutputSet}, it will be created.
     * </p>
     *
     * @param exif
     *              An instance of {@code TiffImageMetadata}.
     * @return If successful, a valid {@code TiffOutputSet} instance.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} {@code TiffImageMetadata} is unable to be read.
     */
    protected static TiffOutputSet readOutputSet(final TiffImageMetadata exif) throws JpegAutorotateException{
        try {
            if(exif.getOutputSet() == null) {
                return new TiffOutputSet();
            }

            return exif.getOutputSet();
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to read JPEG image TIFF metadata.", e);
        }
    }

    /**
     * Attempts to read {@code JpegImage} {@code ICCProfile}.
     *
     * @param bytes
     *              {@code bytes} containing a JPEG image file.
     * @return If successful, a valid {@code ICCProfile} instance.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} {@code ICCProfile} metadata is unable to be read.
     */
    protected static ICC_Profile readIccProfile(final byte[] bytes) throws JpegAutorotateException {
        try {
            return Imaging.getICCProfile(bytes);
        } catch (ImageReadException | IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG image ICC Profile metadata.", e);
        }
    }

    /**
     * Attempts to read {@code JpegImage} {@code XmpXml) metadata.
     *
     * @param bytes
     *              {@code bytes} containing a JPEG image file.
     * @return If successful, a valid {@code XmpXml} metadata String. Otherwise, returns null.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} {@code XmpXml} metadata is unable to be read.
     */
    protected static String readXmpXml(final byte[] bytes) throws JpegAutorotateException {
      try {
          return Imaging.getXmpXml(bytes);
      } catch(IOException | ImageReadException e) {
          throw new JpegAutorotateException("Unable to read JPEG image XMP metadata.", e);
      }
    }

    /**
     * Attempts to read {@code ExifThumbnail} from {@code JpegImageMetadata}.
     *
     * @param metadata
     *              An instance of {@code JpegImageMetadata}.
     * @return If successful, a {@code BufferedImage} containing the thumbnail image data.
     *         Otherwise, returns null.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} {@code ExifThumbnail} is unable to be read.
     */
    protected static BufferedImage readThumbnail(final org.apache.commons.imaging.formats.jpeg.JpegImageMetadata metadata) throws JpegAutorotateException {
        try {
            return metadata.getEXIFThumbnail();
        } catch (ImageReadException | IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG image thumbnail.", e);
        }
    }

}
