package com.example.genius.vitubeclient.presenter.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.example.genius.vitubeclient.model.Video;
import com.example.genius.vitubeclient.utils.VideoStorageUtils;

import retrofit.client.Response;

/**
 * Created by Genius on 7/20/2015.
 */
public class DownloadIntentService  extends GenericDownloadUploadService{
    public static final String  ACTION_DOWNLOAD_COMPLETE="com.example.genius.vitubeclient.ACTION_DOWNLOAD_COMPLETE";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadIntentService( ) {
        super("DownloadIntentService");
        setNOTIFICATION_ID(MSG_DOWNLOAD);//set the notification Id

    }

    @Override
    protected void doAction(Intent intent) {
        int videoId=intent.getIntExtra(VIDEO_ID,0);
        String videoTitle=intent.getStringExtra(VIDEO_NAME);
        startNotification("video download","download in progress","downloading video");
        Response response=mVideoApi.getData(videoId);
        boolean status=VideoStorageUtils.storeVideo(response,this,videoTitle);
        finishNotification(status ? "video download successful" : "video download unsuccesful");
        if(status){
            //start broadcast reciever to notify the activity to refresh the content, greyify the download button
            sendLocalBroadcast(ACTION_DOWNLOAD_COMPLETE, videoId);
            //stopService(intent);//stop the service , this is optional, since  its an intent service , it will stop on its own
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

    }





    /**
     * Factory method for creating intent to download a video
     * @param context
     * @param requestCode
     * @param video
      * @return
     */
    public static Intent makeIntent(Context context,int requestCode ,Video video ){
        Intent intent = new Intent(context,DownloadIntentService.class);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(VIDEO_ID, video.getId());
         intent.putExtra(VIDEO_NAME,video.getTitle());
        return intent;
    }




}
