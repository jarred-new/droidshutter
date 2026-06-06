package com.jarredapps.shuttercount.android;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory;
import com.drew.metadata.exif.makernotes.FujifilmMakernoteDirectory;
import com.drew.metadata.exif.makernotes.NikonType2MakernoteDirectory;
import com.drew.metadata.exif.makernotes.SonyTag9050bDirectory;
import androidx.exifinterface.media.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;


public class MetadataParser {

    public static final String TAG = "MetadataParser";

    public String getShutterCount(
        Context context,
        Uri uri) {

        try {

            InputStream inputStream =
                context.getContentResolver()
                .openInputStream(uri);

            BufferedInputStream buffered =
                new BufferedInputStream(inputStream);

            Metadata metadata =
                ImageMetadataReader.readMetadata(buffered);         

            // Nikon
            NikonType2MakernoteDirectory nikon =
                metadata.getFirstDirectoryOfType(
                NikonType2MakernoteDirectory.class);

            if (nikon != null &&
                nikon.containsTag(
                    0x00A7)) {

                long shutter =
                    nikon.getLong(
                    0x00A7);

                return "Shutter Count: "
                    + shutter;
            }

            // Canon
            CanonMakernoteDirectory canon =
                metadata.getFirstDirectoryOfType(
                CanonMakernoteDirectory.class);

            /* if (canon != null) {

             for (Tag tag : canon.getTags()) {

             if (tag.getTagName()
             .toLowerCase()
             .contains("image")) {

             return "Canon: "
             + tag.getDescription();
             }
             }
             } */

            if (canon != null &&
                canon.containsTag(CanonMakernoteDirectory.TAG_CANON_IMAGE_NUMBER)) {
                long shutter = canon.getLong(
                    CanonMakernoteDirectory.TAG_CANON_IMAGE_NUMBER);

                return "Shutter Count: " 
                    + shutter;
            }

            // Sony
            SonyTag9050bDirectory sony =
                metadata.getFirstDirectoryOfType(
                SonyTag9050bDirectory.class);

            /*if (sony != null) {

             for (Tag tag : sony.getTags()) {

             String name =
             tag.getTagName().toLowerCase();

             if (name.contains("count") ||
             name.contains("shutter")) {

             return tag.getDescription();
             }
             }
             }*/

            if (sony != null &&
                sony.containsTag(SonyTag9050bDirectory.TAG_SHUTTER_COUNT)) {
                long shutter =
                    sony.getLong(SonyTag9050bDirectory.TAG_SHUTTER_COUNT);

                return "Shutter Count: " + shutter;
            }

            // Fujifilm
            FujifilmMakernoteDirectory fuji =
                metadata.getFirstDirectoryOfType(
                FujifilmMakernoteDirectory.class);

            /*if (fuji != null) {

             for (Tag tag : fuji.getTags()) {

             String name =
             tag.getTagName().toLowerCase();

             if (name.contains("count")) {

             return tag.getDescription();
             }
             }
             } */

            if (fuji != null &&
                fuji.containsTag(FujifilmMakernoteDirectory.TAG_FRAME_NUMBER)) {
                long shutter =
                    fuji.getLong(FujifilmMakernoteDirectory.TAG_FRAME_NUMBER);

                return "Shutter Count" + shutter;
            }

            // For all camera brands
            for (Directory directory : metadata.getDirectories()) {

                Log.d("DIR", "===== " + directory.getName());

                for (Tag tag : directory.getTags()) {

                    Log.d("TAG",
                          tag.getTagName()
                          + " = "
                          + tag.getDescription());
                    
                   return "Shutter Count: " + tag.getDescription();
                }
            }

            return "Shutter count not found";

        } catch (Exception e) {

            e.printStackTrace();

            return e.toString();
        }
    }

    public String getCamera(Context context, Uri uri, boolean onList) {
        try {
            InputStream inputStream =
                context.getContentResolver()
                .openInputStream(uri);

            BufferedInputStream buffered =
                new BufferedInputStream(inputStream);

            Metadata metadata =
                ImageMetadataReader.readMetadata(buffered);

            ExifIFD0Directory exif =
                metadata.getFirstDirectoryOfType(
                ExifIFD0Directory.class);

            if (exif != null) {
                if (onList == true)
                    return "Camera: " + exif.getString(ExifIFD0Directory.TAG_MODEL);
                if (onList == false)
                    return exif.getString(ExifIFD0Directory.TAG_MODEL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "No Camera Model Found";
    }

    public String getCameraLens(Context context, Uri uri) {
        try {
            InputStream inputStream =
                context.getContentResolver()
                .openInputStream(uri);

            BufferedInputStream buffered =
                new BufferedInputStream(inputStream);

            Metadata metadata =
                ImageMetadataReader.readMetadata(buffered);

            ExifIFD0Directory exif =
                metadata.getFirstDirectoryOfType(
                ExifIFD0Directory.class);

            if (exif != null) {
                return "Lens: " + exif.getString(ExifIFD0Directory.TAG_LENS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "No Camera Lens Found";
    }

    public String getCameraLensModel(Context context, Uri uri) {
        try {
            InputStream inputStream =
                context.getContentResolver()
                .openInputStream(uri);

            BufferedInputStream buffered =
                new BufferedInputStream(inputStream);

            Metadata metadata =
                ImageMetadataReader.readMetadata(buffered);

            ExifIFD0Directory exif =
                metadata.getFirstDirectoryOfType(
                ExifIFD0Directory.class);

            if (exif != null) {
                return "Lens Model: " + exif.getString(ExifIFD0Directory.TAG_LENS_MODEL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "No Camera Lens Model Found";
    }
    
    public String getFocalLength(Context context, Uri uri) {
        try {
            InputStream inputStream =
                context.getContentResolver()
                .openInputStream(uri);

            BufferedInputStream buffered =
                new BufferedInputStream(inputStream);

            Metadata metadata =
                ImageMetadataReader.readMetadata(buffered);

            ExifIFD0Directory exif =
                metadata.getFirstDirectoryOfType(
                ExifIFD0Directory.class);

            if (exif != null) {
                return "Focal Length: " + exif.getString(ExifIFD0Directory.TAG_FOCAL_LENGTH);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "No Camera Lens Focal Length Found";
    }    
    
    public String getAperture(Context context, Uri uri) {
        /*try {
            File imageFile = new File(uri.toString());
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

            // Fetch both possible directories
            ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            String aperture = null;
            //String shutter = null;
            //String iso = null;

            // 1. Try SubIFD first (Standard)
            if (subIFD != null) {
                aperture = subIFD.getString(ExifSubIFDDirectory.TAG_FNUMBER);
                //shutter = subIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
                //iso = subIFD.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
            }

            // 2. Fallback to IFD0 if values are still null
            if (ifd0 != null) {
                if (aperture == null) aperture = ifd0.getString(ExifIFD0Directory.TAG_FNUMBER);
                //if (shutter == null) shutter = ifd0.getString(ExifIFD0Directory.TAG_EXPOSURE_TIME);
                //if (iso == null) iso = ifd0.getString(ExifIFD0Directory.TAG_ISO_EQUIVALENT);
            }

            // Print the final retrieved items
            return "Aperture: " + (aperture != null ? "f/" + aperture : "Not Found");
            //System.out.println("Shutter Speed: " + (shutter != null ? shutter + "s" : "Not Found"));
            //System.out.println("ISO: " + (iso != null ? iso : "Not Found"));

        } catch (Exception e) {
            e.printStackTrace();
        } */
        try {
            InputStream stream = 
                context.getContentResolver().openInputStream(uri);
            
            if (stream != null) {
                ExifInterface exif = new ExifInterface(stream);
                
                String aperture =
                    exif.getAttribute(ExifInterface.TAG_F_NUMBER);
                
                return "Aperture: f/" + aperture;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "No Aperture Found";
    }
    
    public String getShutterSpeed(Context context, Uri uri) {
        /* try {
            File imageFile = new File(uri.toString());
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

            // Fetch both possible directories
            ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            //String aperture = null;
            String shutter = null;
            //String iso = null;

            // 1. Try SubIFD first (Standard)
            if (subIFD != null) {
                //aperture = subIFD.getString(ExifSubIFDDirectory.TAG_FNUMBER);
                shutter = subIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
                //iso = subIFD.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
            }

            // 2. Fallback to IFD0 if values are still null
            if (ifd0 != null) {
                //if (aperture == null) aperture = ifd0.getString(ExifIFD0Directory.TAG_FNUMBER);
                if (shutter == null) shutter = ifd0.getString(ExifIFD0Directory.TAG_EXPOSURE_TIME);
                //if (iso == null) iso = ifd0.getString(ExifIFD0Directory.TAG_ISO_EQUIVALENT);
            }

            // Print the final retrieved items
            //System.out.println("Aperture: " + (aperture != null ? "f/" + aperture : "Not Found"));
            return "Shutter Speed: " + (shutter != null ? shutter + "s" : "Not Found");
            //System.out.println("ISO: " + (iso != null ? iso : "Not Found"));

        } catch (Exception e) {
            e.printStackTrace();
        } */
        
        try {
            InputStream stream = 
                context.getContentResolver().openInputStream(uri);

            if (stream != null) {
                ExifInterface exif = new ExifInterface(stream);

                String shutterspeed =
                    exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);

                return "Shutter Speed: " + shutterspeed;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "No Shutter Speed Found";
    }
    
    public String getISO(Context context, Uri uri) {
        /* try {
            File imageFile = new File(uri.toString());
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

            // Fetch both possible directories
            ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            //String aperture = null;
            //String shutter = null;
            String iso = null;

            // 1. Try SubIFD first (Standard)
            if (subIFD != null) {
                //aperture = subIFD.getString(ExifSubIFDDirectory.TAG_FNUMBER);
                //shutter = subIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
                iso = subIFD.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
            }

            // 2. Fallback to IFD0 if values are still null
            if (ifd0 != null) {
                //if (aperture == null) aperture = ifd0.getString(ExifIFD0Directory.TAG_FNUMBER);
                //if (shutter == null) shutter = ifd0.getString(ExifIFD0Directory.TAG_EXPOSURE_TIME);
                if (iso == null) iso = ifd0.getString(ExifIFD0Directory.TAG_ISO_EQUIVALENT);
            }

            // Print the final retrieved items
            //System.out.println("Aperture: " + (aperture != null ? "f/" + aperture : "Not Found"));
            //System.out.println("Shutter Speed: " + (shutter != null ? shutter + "s" : "Not Found"));
            return "ISO: " + (iso != null ? iso : "Not Found");

        } catch (Exception e) {
            e.printStackTrace();
        } */
        
        try {
            InputStream stream = 
                context.getContentResolver().openInputStream(uri);

            if (stream != null) {
                ExifInterface exif = new ExifInterface(stream);

                String iso =
                    exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);

                return "ISO: " + iso;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "No Camera ISO Found";
    }
}
