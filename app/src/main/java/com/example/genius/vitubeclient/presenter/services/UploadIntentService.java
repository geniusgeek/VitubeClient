package com.example.genius.vitubeclient.presenter.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Message;

import com.example.genius.vitubeclient.model.Video;
import com.example.genius.vitubeclient.model.VideoMessageWrapperFacade;
import com.example.genius.vitubeclient.model.mediator.webendpoint.VideoStatus;
import com.example.genius.vitubeclient.presenter.VideoOps;
import com.example.genius.vitubeclient.utils.Constants;
import com.example.genius.vitubeclient.utils.VideoUtils;

import java.io.File;
import java.util.Map;

import retrofit.mime.TypedFile;

/**
 * Created by Genius on 7/20/2015.
 */
public class UploadIntentService extends GenericDownloadUploadService{
    public static final String  ACTION_UPLOAD_COMPLETE="com.example.genius.vitubeclient.ACTION_UPLOAD_COMPLETE";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name  Used to name the worker thread, important only for debugging.
     */
    public UploadIntentService() {
        super("upload Intent Service");
        setNOTIFICATION_ID(2);//set the notification id to use, setting different notification id for download and upload
        //allow 2 notification to be showed at thesame time.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Message message=intent.getParcelableExtra(API_PROXY);//get the message
        mVideoApi= mVideoMessageWrapperFacade.getApiObjectForMessage(message);
        Uri uri = intent.getData();
        Map<String,String> map = VideoUtils.getMetaDataForVideo(this, uri, new MediaMetadataRetriever());
        final Video video= new Video(map.get(VideoUtils.VIDEO_TITLE),//get the title
                     Long.getLong(map.get(VideoUtils.VIDEO_DURATION), 0l)//get the video duration
                     ,map.get(VideoUtils.VIDEO_MIME_TYPE));//get the mime type

        //show notification
        startNotification("video upload","upload in progress","uploading video");
        //very dangerous to run on uithread
        Video returnedVideo=mVideoApi.addVideo(video);//post the video metadata to the server
        VideoStatus videoStatus=null;
        //next upload the actual video data
        TypedFile typedFile= new TypedFile(video.getContentType(),new File(uri.getPath()));//create a TypedFile for the video
        if(typedFile.length()<=Constants.MAX_SIZE_MEGA_BYTE){
              videoStatus= mVideoApi.setVideoData(returnedVideo.getId(), typedFile);//get the status of the upload
            setDownloadMessage(videoStatus.getState(), VideoOps.Operations.UPLOAD,returnedVideo);
        }else{
            setmDownloadUploadMessage("file too large");
        }

        finishNotification(getmDownloadUploadMessage());

        if(videoStatus !=null && videoStatus.equals(VideoStatus.VideoState.READY)){
            sendLocalBroadcast(ACTION_UPLOAD_COMPLETE,returnedVideo.getId());
        }

    }

    /**
     * Factory method for creating an intent to upload video
     * @param context
     * @param requestCode
     * @param resourceUri
     * @param api
     * @return
     */

    public static Intent makeIntent(Context context,int requestCode,Uri resourceUri ,VideoMessageWrapperFacade api){
        Intent intent = new Intent(context,UploadIntentService.class);
        intent.setData(resourceUri);
        intent.putExtra(REQUEST_CODE,requestCode);
        intent.putExtra(API_PROXY, api.getMessage());
        intent.setData(resourceUri);//upload a resource, metadata , then video
        return intent;
    }
}
