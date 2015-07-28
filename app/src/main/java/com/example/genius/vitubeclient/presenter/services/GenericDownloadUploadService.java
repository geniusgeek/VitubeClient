package com.example.genius.vitubeclient.presenter.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.genius.vitubeclient.model.Video;
import com.example.genius.vitubeclient.model.VideoMessageWrapperFacade;
import com.example.genius.vitubeclient.model.mediator.webendpoint.VideoServiceProxy;
import com.example.genius.vitubeclient.model.mediator.webendpoint.VideoStatus;
import com.example.genius.vitubeclient.presenter.VideoOps;
import com.example.genius.vitubeclient.utils.Constants;
import com.example.genius.vitubeclient.utils.Utils;

/**
 * Created by Genius on 7/20/2015.
 */
public abstract class GenericDownloadUploadService extends IntentService {
    protected final String TAG = getClass().getSimpleName();
    protected static final String REQUEST_CODE="request_code";
    protected static final String API_PROXY="api";
    public static final String VIDEO_ID="video_id";
    public static final String VIDEO_NAME ="video_name";
    protected int requestCode;
    private String mDownloadUploadMessage="";
    protected VideoServiceProxy mVideoApi;
    protected VideoMessageWrapperFacade mVideoMessageWrapperFacade;
    protected NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private int NOTIFICATION_ID=1;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GenericDownloadUploadService(String name) {
        super(name);
        mVideoMessageWrapperFacade = new VideoMessageWrapperFacade();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Gets access to the Android Notification Service.
        mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!Utils.checkConnectionStatus(this, Constants.WEB_ENDPOINT))
            return;
    }

    /**
     * generic factory method for selecting the  long running operation to perform
     *
     * @param operation   the operation type
     * @param videoStatus the status of the operation performed
     *
     * @return
     */

    protected void setDownloadMessage(VideoStatus.VideoState videoStatus, VideoOps.Operations operation, Video video){
        if(video==null)
            setmDownloadUploadMessage("video recieved from upload is null");
        switch (videoStatus){
            case   NO_STATUS:
                //the video upload was not successful
                setmDownloadUploadMessage("the video "+ operation+" was not successful");
                return;
            case PROCESSING:
                //the video upload was successful
                setmDownloadUploadMessage("the video"+ operation+"was successful");
                return;
            case READY:
                //the video upload was broken or could not complete
                setmDownloadUploadMessage("the video"+ operation+"was broken or could not complete");
                return;
            default:
                setmDownloadUploadMessage("something undescribeable happened");
                return;
        }
    }



    /**
     * Finish the Notification after the Video is Uploaded.
     *
     * @param status
     */
    protected void finishNotification(String status) {
        // When the loop is finished, updates the notification.
        mBuilder.setContentTitle(status)
                // Removes the progress bar.
                .setProgress (0,
                        0,
                        false)
                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                .setContentText("")
                .setTicker(status);

        // Build the Notification with the given
        // Notification Id.
        mNotificationManager.notify(getNOTIFICATION_ID(),
                mBuilder.build());
    }

    /**
     * Starts the Notification to show the progress of video upload.
     */
    protected void startNotification(String title, String text, String ticker) {

        // Create the Notification and set a progress indicator for an
        // operation of indeterminate length.
        mBuilder = new NotificationCompat
                .Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setTicker(ticker)
                .setProgress(0,
                        0,
                        true);
        Notification notification= mBuilder.build();
        notification.flags|=Notification.FLAG_AUTO_CANCEL;//bitwise auto cancel
        // Build and issue the notification.
        mNotificationManager.notify(getNOTIFICATION_ID(),notification);
    }

    public int getNOTIFICATION_ID() {
        return NOTIFICATION_ID;
    }

    public void setNOTIFICATION_ID(int NOTIFICATION_ID) {
        this.NOTIFICATION_ID = NOTIFICATION_ID;
    }

    public String getmDownloadUploadMessage() {
        return mDownloadUploadMessage;
    }

    public void setmDownloadUploadMessage(String mDownloadUploadMessage) {
        this.mDownloadUploadMessage = mDownloadUploadMessage;
    }

    protected void sendLocalBroadcast(String action, long videoId){

        Intent intent = getIntent(action, videoId);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * factory method to construct an intent containing intentFiler with action
     * @param action
     * @return
     */
    @NonNull
    private Intent getIntent(String action,long videoId) {
        Intent intent= new Intent(action);
        intent.putExtra(VIDEO_ID,videoId);
        return intent;
    }
}
