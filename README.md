![Logo](logo.png)

JPEG Autorotate is a Java library to rotate JPEG images based on EXIF orientation.

---

## Features

Applies the right orientation to a JPEG image, based on its EXIF tag. More precisely, it:
- Rotates the pixels
- Rotates the thumbnail, if present
- Sets the `Orientation` EXIF tag to `1` (default orientation value)
- Updates the following metadata values, if present:
    - EXIF
        - `ExifImageWidth`
        - `ExifImageLength`
        - `RelatedImageWidth`
        - `RelatedImageHeight`
    - XMP
        - `tiff:Orientation`
        - `tiff:ImageWidth`
        - `tiff:ImageLength`
        - `exif:PixelXDimension`
        - `exif:PixelYDimension`
        - `xmp:ThumbnailsWidth`
        - `xmp:ThumbnailsHeight`
- Retains and does **not** alter other metadata tags
- Currently does **not** support updating `MakerNotes` metadata

> More information about EXIF metadata is available at:
> - [EXIF Orientation Handling Is a Ghetto](https://www.daveperrett.com/articles/2012/07/28/exif-orientation-handling-is-a-ghetto/)
> - [ExifTool Tag Names](https://exiftool.org/TagNames/)
> - [Standard Exif Tags](https://www.exiv2.org/tags.html)

## Installation

TODO

## Usage

The library will load an image by either a `String` file path, `File` or `InputStream`, apply rotation, update metadata and return the image as a byte array.

```java
// File path
byte[] image = JpegAutorotate.rotate("path/to/directory");

// OR

// File
byte[] image = JpegAutorotate.rotate(new File("path/to/directory"));

// OR

// InputStream
byte[] image = JpegAutorotate.rotate(new FileInputStream(new File("path/to/directory")));
```

### Error Handling

The library throws the following exceptions:
- `JpegAutorotateException`
- `FileNotFoundException`
    - Only thrown if loading image by either a `String` file path or `File`)

## Contributing

TODO

## Credits

### Libraries

This library uses the following libraries:
- [Apache Commons Imaging](https://github.com/apache/commons-imaging), available under the [Apache License v2](https://www.apache.org/licenses/LICENSE-2.0) containing the following [notices and atrtibutions](https://github.com/apache/commons-imaging/blob/master/NOTICE.txt).
- [Apache Commons IO](https://github.com/apache/commons-io), available under the [Apache License v2](https://www.apache.org/licenses/LICENSE-2.0) containing the following [notices and atrtibutions](https://github.com/apache/commons-io/blob/master/NOTICE.txt).

### Logo

The JPEG Autorotate logo uses the following icons from [Font Awesome](https://fontawesome.com/), available under the [Creative Commons Attribution 4.0 International License](https://creativecommons.org/licenses/by/4.0/):
- [file-image](https://fontawesome.com/icons/file-image?style=regular)
- [sync-alt](https://fontawesome.com/icons/sync-alt?style=solid)

The following icon changes were made:
- Stroke width decreased for all icons by 0.25
- Detached arrows from [sync-alt](https://fontawesome.com/icons/sync-alt?style=solid)

### Other

- [EXIF Info](https://exifinfo.org/)
- [exif-samples](https://github.com/ianare/exif-samples)
- [exif-orientation-examples](https://github.com/recurser/exif-orientation-examples)

## License

This code is under the [Apache License v2](LICENSE)