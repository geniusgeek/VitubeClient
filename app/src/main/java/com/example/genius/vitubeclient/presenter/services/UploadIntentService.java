package com.example.genius.vitubeclient.presenter.services;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.example.genius.vitubeclient.model.Video;
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
        setNOTIFICATION_ID(MSG_UPLOAD);//set the notification id to use, setting different notification id for download and upload
        //allow 2 notification to be showed at thesame time.

    }

    @Override
    protected void doAction(Intent intent) {
        Uri mUri = intent.getData();
        Map<String,String> map = VideoUtils.getMetaDataForVideo(this, mUri, new MediaMetadataRetriever());
        VideoUtils.printMap(map);
        Video mVideo= new Video(map.get(VideoUtils.VIDEO_TITLE),//get the title
                Long.parseLong(map.get(VideoUtils.VIDEO_DURATION))//get the video duration
                ,map.get(VideoUtils.VIDEO_MIME_TYPE));//get the mime type

        Log.d(TAG, "video to upoad:" + mVideo);
        //show notification
        startNotification("video upload","upload in progress","uploading video");


        try{
            VideoStatus videoStatus=null;
            Video returnedVideo=mVideoApi.addVideo(mVideo);//post the video metadata to the server

            //next upload the actual video data
            TypedFile typedFile= new TypedFile(mVideo.getContentType(),new File(mUri.getPath()));//create a TypedFile for the video
            if(typedFile.length()<=Constants.MAX_SIZE_MEGA_BYTE){
                videoStatus= mVideoApi.setVideoData(returnedVideo.getId(), typedFile);//get the status of the upload
                setDownloadMessage(videoStatus.getState(), VideoOps.Operations.UPLOAD,returnedVideo);
            }else{
                setmDownloadUploadMessage("file too large");
            }


            if(videoStatus !=null && videoStatus.equals(VideoStatus.VideoState.READY)){
                sendLocalBroadcast(ACTION_UPLOAD_COMPLETE, returnedVideo.getId());
            }
        }catch(Exception ex){
            ex.printStackTrace();
            setmDownloadUploadMessage(ex.getMessage());
        }

        finishNotification(getmDownloadUploadMessage());

    }



    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

    }



    /**
     * Factory method for creating an intent to upload video
     * @param context
     * @param requestCode
     * @param resourceUri
     * @return
     */

    public static Intent makeIntent(Context context,int requestCode,Uri resourceUri){
        Intent intent = new Intent(context,UploadIntentService.class);
        intent.setData(resourceUri);
        intent.putExtra(REQUEST_CODE, requestCode);
        return intent;
    }
}
