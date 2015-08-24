package com.example.genius.vitubeclient.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Genius on 7/21/2015.
 */
public class VideoUtils {
    public static String VIDEO_DATE="date";
    public static String VIDEO_DURATION="duration";
    public static String VIDEO_TITLE="title";
    public static String VIDEO_MIME_TYPE="content_Type";
    public static final String TAG=VideoUtils.class.getSimpleName();

    /**
     * this method retrieed the metadata for a video by passing the mediaMetadataRetriever using dependency injection
     * pattern/IDIOM
     * @param path
     * @param mediaMetadataRetriever
     * @return
     */
    static public  Map<String,String> getMetaDataForVideo(Context context,Uri contentUri, MediaMetadataRetriever mediaMetadataRetriever){
        Map<String,String> map =  new HashMap<>();
        try{
             String path=VideoFileUtils.getRealPathFromURI(context, contentUri);

            mediaMetadataRetriever.setDataSource(path);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        Log.d(TAG,"title to put is:"+ mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)) ;

        map.put(VIDEO_DATE, mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
        map.put(VIDEO_DURATION,mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) ;
        map.put(VIDEO_TITLE, new File(contentUri.toString()).getName()) ;
        map.put(VIDEO_MIME_TYPE, mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)) ;

         return map;

    }

    static public void printMap(Map<?,?> map){
        System.out.println("printing values from map");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            System.out.println("key, " + key + " value " + value );
        }
    }



    private VideoUtils(){
        try {
            throw  new InstantiationException("cannot instantiate this class");
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
