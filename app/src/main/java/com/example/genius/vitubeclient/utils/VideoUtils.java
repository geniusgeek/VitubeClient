package com.example.genius.vitubeclient.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

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


    /**
     * this method retrieed the metadata for a video by passing the mediaMetadataRetriever using dependency injection
     * pattern/IDIOM
     * @param path
     * @param mediaMetadataRetriever
     * @return
     */
    static public  Map<String,String> getMetaDataForVideo(Context context,Uri contentUri, MediaMetadataRetriever mediaMetadataRetriever){
        Map<String,String> map =  new HashMap<>();
        mediaMetadataRetriever.setDataSource(context,contentUri);
        map.put(VIDEO_DATE, mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
        map.put(VIDEO_DURATION,mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) ;
        map.put(VIDEO_TITLE, mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)) ;
        map.put(VIDEO_MIME_TYPE, mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)) ;
         return map;

    }



    private VideoUtils(){
        try {
            throw  new InstantiationException("cannot instantiate this class");
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
