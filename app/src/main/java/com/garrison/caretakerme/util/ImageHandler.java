package com.garrison.caretakerme.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by Garrison on 11/3/2014.
 */
public class ImageHandler {

    private static final String LOG_TAG = ImageHandler.class.getSimpleName();

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {

            String[] projection = { MediaStore.Images.Media.DATA };

            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception ioE) {
            Log.v(LOG_TAG, "ioE CAUGHT = " + ioE.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static Bitmap resizeImage(Context context, String photoUri, int newWidth, int newHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = true;

        String absoluteUri = ImageHandler.getRealPathFromURI(context, Uri.parse(photoUri));
        BitmapFactory.decodeFile(absoluteUri, options);

        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(absoluteUri, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
