package com.example.genius.vitubeclient.model.mediator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;

import com.example.genius.vitubeclient.model.Video;
import com.example.genius.vitubeclient.model.VideoMessageWrapperFacade;
import com.example.genius.vitubeclient.model.mediator.webendpoint.VideoServiceProxy;
import com.example.genius.vitubeclient.presenter.services.DownloadIntentService;
import com.example.genius.vitubeclient.presenter.services.UploadIntentService;
import com.example.genius.vitubeclient.utils.Constants;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Genius on 7/19/2015.
 * This plays the role of the Concrete mediator in the mediator pattern. THis mediates between the client and server
 * This also uses a variant of the asynchronous completion token pattern. TO request for a service and passing the
 * token(request code) and passes the request code(token)  as the result of the operation
 * The methods in this class are synchronized thereby avoiding some concurrent operational issues
 * This mediator also serves as a thin facade of the facede pattern around the VideoServiceProxy
 */
public class VideoServiceMediator implements MediatorInterface {

    /**
     * the proxy, Api this
     */
    private VideoServiceProxy videoApi;


    public VideoServiceMediator() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.WEB_ENDPOINT)
                .build();
        videoApi = restAdapter.create(VideoServiceProxy.class);


    }

    /**
     * this is an asynchronous event to upload a video to the server and recieve response via a broadcast reciever
     * @param requestCode
     * @param context
     * @param requestUri
     * @return
     */
    @Override
    public synchronized void uploadVideo(int requestCode,Context context, final Uri requestUri) {

        if(requestCode!=REQUEST_UPLOAD)
            throw new IllegalArgumentException("wrong request code passed for this operation");
        VideoMessageWrapperFacade message = getMessage();
        Intent intent= UploadIntentService.makeIntent(context, requestCode, requestUri, message);
        context.startService(intent);

    }

    /**
     * Factory method to get the message which is encapsulated with the videoApi to be sent to the service to execute
     * the download or upload operation
     * @return
     */
    private VideoMessageWrapperFacade getMessage() {
        VideoMessageWrapperFacade message=new VideoMessageWrapperFacade(new Message());
        message.setApiObject(videoApi);
        return message;
    }

    @Override
    public synchronized void downloadVideo(int requestCode, Context context, Video video) {

        if(requestCode!=REQUEST_DOWNLOAD)
            throw new IllegalArgumentException("wrong request code passed for this operation");
        VideoMessageWrapperFacade message = getMessage();
        Intent intent= DownloadIntentService.makeIntent(context, requestCode, video, message);
        context.startService(intent);
    }


    public synchronized double rateVideo(int requestCode,Video video, double rating) {

        if(requestCode!=REQUEST_RATE)
            throw new IllegalArgumentException("wrong request code passed for this operation");
       return videoApi.setAndGetRatingForVideo(video.getId(),rating);
    }

    @Override
    public List<Video> getVideoList() {
        return (List<Video>) videoApi.getVideoList();
    }
}
