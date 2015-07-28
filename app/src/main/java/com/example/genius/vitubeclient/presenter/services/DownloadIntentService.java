package com.example.genius.vitubeclient.presenter.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.example.genius.vitubeclient.model.Video;
import com.example.genius.vitubeclient.model.VideoMessageWrapperFacade;
import com.example.genius.vitubeclient.utils.Constants;
import com.example.genius.vitubeclient.utils.Utils;
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
        setNOTIFICATION_ID(1);//set the notification Id
    }

    @SuppressLint("NewApi")
    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        Message message=intent.getParcelableExtra(API_PROXY);//get the message
        mVideoApi= mVideoMessageWrapperFacade.getApiObjectForMessage(message);
        startNotification("video download","download in progress","downloading video");
        int videoId=intent.getIntExtra(VIDEO_ID,0);
        String videoTitle=intent.getStringExtra(VIDEO_NAME);
        Response response=mVideoApi.getData(videoId);
        boolean status=VideoStorageUtils.storeVideo(response,this,videoTitle);
        finishNotification(status?"video download successful":"video download unsuccesful");
        if(status){
            //start broadcast reciever to notify the activity to refresh the content, greyify the download button
            sendLocalBroadcast(ACTION_DOWNLOAD_COMPLETE, videoId);
            //stopService(intent);//stop the service , this is optional, since  its an intent service , it will stop on its own
        }
    }


    /**
     * Factory method for creating intent to download a video
     * @param context
     * @param requestCode
     * @param video
     * @param api
     * @return
     */
    public static Intent makeIntent(Context context,int requestCode ,Video video,VideoMessageWrapperFacade api){
        Intent intent = new Intent(context,DownloadIntentService.class);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(API_PROXY,api.getMessage());
        intent.putExtra(VIDEO_ID, video.getId());
        intent.putExtra(VIDEO_NAME,video.getTitle());
        return intent;
    }




}
