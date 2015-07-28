package com.example.genius.vitubeclient.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/** This is a vidoe utility class that s reponsile for handling all the video related generic operations.
 * This plays the role of  a singleton class in the singleton pattern.
 * Created by Genius on 7/21/2015.
 */
public class VideoFileUtils {

    /**
     * Content Uri scheme for Downloads Provider.
     */
    public static final String DOWNLOADS_PROVIDER_PATH =  "content://downloads/public_downloads";




    /**
     * Get a Video file path from a Uri. This will get the the path
     * for Storage Access Framework Documents, as well as the _data
     * field for the MediaStore and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     *
     * return videoFilePath
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI(final Context context,
                                 final Uri uri) {
        // Check if the version of current device is greater
        // than API 19 (KitKat).
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider. document:// .... or tree/document:// ...
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) { //check if the path scheme is a document

            final String docId = DocumentsContract.getDocumentId(uri);//get the document id
            // ExternalStorageProvider document://primary.... or tree/document://primary...
            if (isExternalStorageDocument(uri)) {
                final String[] split = docId.split(":");
                final String type =   split[0];//get the sc

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory()
                            + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                 final Uri contentUri =
                        ContentUris.withAppendedId
                                (Uri.parse(DOWNLOADS_PROVIDER_PATH),
                                        Long.valueOf(docId));//content://downloads/...

                return getVideoDataColumn(context,
                        contentUri,
                        null,
                        null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                 final String[] split = docId.split(":");
                // content://id
                final Uri contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                final String selection = BaseColumns._ID+" = ?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                // Get the FilePath from Video MediStore
                // for given Uri, selection, selectionArgs.
                return getVideoDataColumn(context,
                        contentUri,
                        selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general) . get the video path for other versions or mediastore
        else if ("content".equalsIgnoreCase(uri.getScheme()))
            return getVideoDataColumn(context,
                    uri,
                    null,
                    null);
            // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
            return uri.getPath();

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful
     * for MediaStore Uris, and other file-based ContentProviders.
     * Be sure to call this method in a thread other than the main thread
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    @SuppressLint("NewApi")
    private static String getVideoDataColumn(Context context,
                                             Uri uri,
                                             String selection,
                                             String[] selectionArgs) {
        // Projection used to query Android Video Content Provider.
        final String[] projection = {
                MediaStore.Video.Media.DATA  //the _data column of the video
        };

        //Query and get a cursor to Android Video
        // Content Provider.
        try (Cursor cursor =
                     context.getContentResolver().query(uri,
                             projection,
                             selection,
                             selectionArgs,
                             null)) {
            // If video is present, get the file path of the video.
            if (cursor != null  && cursor.moveToFirst())
                return cursor.getString(cursor.getColumnIndexOrThrow (MediaStore.Video.Media.DATA));
        }

        // No video present. returns null.
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents"
                .equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents"
                .equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents"
                .equals(uri.getAuthority());
    }

    private VideoFileUtils(){
        try {
            throw  new InstantiationException("cannot instantiate this class");
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
